package com.team2.higallery.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.team2.higallery.Configuration;
import com.team2.higallery.R;
import com.team2.higallery.models.Account;
import com.team2.higallery.models.EncryptedImage;
import com.team2.higallery.models.VaultManager;
import com.team2.higallery.utils.DataUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.io.File;
import java.io.IOException;


public class SignUpVaultActivity extends AppCompatActivity {
    private final int PIN_LENGTH = 6;

    EditText emailInput, pinInput, retypeInput;
    TextView emailError, passwordError, retypePasswordError;
    ProgressBar progressBar;

    boolean finishAfterSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.set(this);
        setContentView(R.layout.activity_sign_up_vault);

        Intent intent = getIntent();
        finishAfterSignUp = intent.getBooleanExtra("finishAfterSignUp", false);
        setResult(RESULT_CANCELED);

        emailInput = (EditText) findViewById(R.id.email_input_signup_vault);
        emailError = (TextView) findViewById(R.id.signup_vault_email_error);

        pinInput = (EditText) findViewById(R.id.pin_input_signup_vault);
        passwordError = (TextView) findViewById(R.id.signup_vault_password_error);

        retypeInput = (EditText) findViewById(R.id.retype_pin_input_signup_vault);
        retypePasswordError = (TextView) findViewById(R.id.signup_vault_confirm_password_error);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_sign_up_vault);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void restore(View v) {
        String email = emailInput.getText().toString();
        String pin = pinInput.getText().toString();
        String retype = retypeInput.getText().toString();

        if (!validateInput(email, pin, retype)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String uid = task.getResult().getUser().getUid();
                    Account.store(uid, email, pin, SignUpVaultActivity.this);

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("users").document(uid).collection("encrypted_images").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                final int[] nRemainFiles = {task.getResult().size()};

                                if (nRemainFiles[0] == 0) {
                                    if (finishAfterSignUp) {
                                        setResult(RESULT_OK);
                                        finish();
                                    } else {
                                        goToVaultAlbum();
                                    }
                                }

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    EncryptedImage encryptedImage = EncryptedImage.fromMap(document.getData());
                                    String fileName = encryptedImage.getFileName();
                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                    StorageReference ref = storage.getReference().child("encrypted_files/" + fileName);

                                    File localFile = new File(SignUpVaultActivity.this.getFilesDir(), fileName);
                                    ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            VaultManager.getInstance(SignUpVaultActivity.this).addEncryptedImage(encryptedImage);
                                            nRemainFiles[0]--;
                                            if (nRemainFiles[0] == 0) {
                                                if (finishAfterSignUp) {
                                                    setResult(RESULT_OK);
                                                    finish();
                                                } else {
                                                    goToVaultAlbum();
                                                }
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle any errors
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            }
                        }
                    });
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    FirebaseAuthException exception = (FirebaseAuthException) task.getException();
                    showAuthError(exception.getErrorCode());
                }
            }
        });

    }

    public void start(View v) {
        String email = emailInput.getText().toString();
        String pin = pinInput.getText().toString();
        String retype = retypeInput.getText().toString();

        if (!validateInput(email, pin, retype)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pin)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = task.getResult().getUser().getUid();
                            Account.store(uid, email, pin, SignUpVaultActivity.this);
                            if (finishAfterSignUp) {
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                goToVaultAlbum();
                            }
                        } else {
                            FirebaseAuthException exception = (FirebaseAuthException) task.getException();
                            showAuthError(exception.getErrorCode());
                        }
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean validateInput(String email, String pin, String retype) {
        if (!DataUtils.validateEmail(email)) {
            emailError.setText(R.string.signup_vault_invalid_email);
            emailError.setVisibility(View.VISIBLE);

            //add event check email with real time
            addEventValidateEmail();

            return false;
        }

        if (pin.length() != PIN_LENGTH) {
            passwordError.setText(R.string.signup_vault_pin_too_short);
            passwordError.setVisibility(View.VISIBLE);

            //add event check password with real time
            addEventValidatePassword();

            return false;
        }

        if (!pin.equals(retype)) {
            retypePasswordError.setText(R.string.signup_vault_pin_not_match);
            retypePasswordError.setVisibility(View.VISIBLE);

            //add event confirm password with real time
            addEventValidateConfirmPassword();

            return false;
        }

        return true;
    }

    private void addEventValidateConfirmPassword() {
        retypeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String pin = pinInput.getText().toString();
                String retype = retypeInput.getText().toString();

                if (!pin.equals(retype)) {
                    retypePasswordError.setText(R.string.signup_vault_pin_not_match);
                    retypePasswordError.setVisibility(View.VISIBLE);
                } else {
                    retypePasswordError.setVisibility(View.GONE);
                }
            }
        });
    }

    private void addEventValidatePassword() {
        pinInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String pin = pinInput.getText().toString();
                if (pin.length() != PIN_LENGTH) {
                    passwordError.setText(R.string.signup_vault_pin_too_short);
                    passwordError.setVisibility(View.VISIBLE);
                } else {
                    passwordError.setVisibility(View.GONE);
                }
            }
        });
    }

    private void addEventValidateEmail() {
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String email = emailInput.getText().toString();
                if (!DataUtils.validateEmail(email)) {
                    emailError.setText(R.string.signup_vault_invalid_email);
                    emailError.setVisibility(View.VISIBLE);
                } else {
                    emailError.setVisibility(View.GONE);
                }
            }
        });
    }

    private void goToVaultAlbum() {
        Intent intent = new Intent(this, VaultAlbumActivity.class);
        finish();
        startActivity(intent);
    }

    private void showAuthError(String errorCode) {
        switch (errorCode) {
            case "ERROR_INVALID_EMAIL":
                Toast.makeText(this, getResources().getString(R.string.signup_vault_invalid_email), Toast.LENGTH_LONG).show();
                break;
            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(this, getResources().getString(R.string.signup_vault_not_found), Toast.LENGTH_LONG).show();
                break;
            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(this, getResources().getString(R.string.signup_vault_email_exists), Toast.LENGTH_LONG).show();
                break;
            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(this, getResources().getString(R.string.restore_vault_wrong_pin), Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(this, getResources().getString(R.string.signup_vault_unknown_error), Toast.LENGTH_LONG).show();
        }
    }
}