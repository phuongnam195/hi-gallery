package com.team2.higallery.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

    TextView emailInput;
    TextView pinInput;
    TextView retypeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.set(this);
        setContentView(R.layout.activity_sign_up_vault);

        emailInput = (TextView) findViewById(R.id.email_input_signup_vault);
        pinInput = (TextView) findViewById(R.id.pin_input_signup_vault);
        retypeInput = (TextView) findViewById(R.id.retype_pin_input_signup_vault);
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
            Toast.makeText(this, getResources().getString(R.string.signup_vault_invalid_email), Toast.LENGTH_LONG).show();
            return false;
        }

        if (pin.length() != PIN_LENGTH) {
            Toast.makeText(this, getResources().getString(R.string.signup_vault_pin_too_short), Toast.LENGTH_LONG).show();
            return false;
        }

        if (!pin.equals(retype)) {
            Toast.makeText(this, getResources().getString(R.string.signup_vault_pin_not_match), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
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