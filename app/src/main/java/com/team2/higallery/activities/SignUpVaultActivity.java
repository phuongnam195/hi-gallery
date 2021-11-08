package com.team2.higallery.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuthException;
import com.team2.higallery.Configuration;
import com.team2.higallery.R;
import com.team2.higallery.models.Account;
import com.team2.higallery.utils.DataUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;


public class SignUpVaultActivity extends AppCompatActivity {
    private final int PIN_LENGTH = 6;

    EditText emailInput, pinInput, retypeInput;
    TextView emailError, passwordError, retypePasswordError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.set(this);
        setContentView(R.layout.activity_sign_up_vault);

        emailInput = (EditText) findViewById(R.id.email_input_signup_vault);
        emailError = (TextView) findViewById(R.id.signup_vault_email_error);

        pinInput = (EditText) findViewById(R.id.pin_input_signup_vault);
        passwordError = (TextView) findViewById(R.id.signup_vault_password_error);

        retypeInput = (EditText) findViewById(R.id.retype_pin_input_signup_vault);
        retypePasswordError = (TextView) findViewById(R.id.signup_vault_confirm_password_error);

    }

    public void restore(View v) {
        String email = emailInput.getText().toString();
        String pin = pinInput.getText().toString();
        String retype = retypeInput.getText().toString();

        if (!validateInput(email, pin, retype)) {
            return;
        }

        Account.auth.signInWithEmailAndPassword(email, pin).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Account.store(email, pin, SignUpVaultActivity.this);
                    goToVaultAlbum();
                } else {
                    FirebaseAuthException exception = (FirebaseAuthException) task.getException();
                    showAuthError(exception.getErrorCode());
                }
            }
        });

        // TODO: Download info and data from Firebase database
    }

    public void start(View v) {
        String email = emailInput.getText().toString();
        String pin = pinInput.getText().toString();
        String retype = retypeInput.getText().toString();

        if (!validateInput(email, pin, retype)) {
            return;
        }

        Account.auth.createUserWithEmailAndPassword(email, pin)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Account.store(email, pin, SignUpVaultActivity.this);
                            goToVaultAlbum();
                        } else {
                            FirebaseAuthException exception = (FirebaseAuthException) task.getException();
                            showAuthError(exception.getErrorCode());
                        }
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
                }
                else{
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
                }
                else{
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
                }
                else{
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