package com.team2.higallery.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.team2.higallery.R;
import com.team2.higallery.models.Account;
import com.team2.higallery.models.VaultManager;

public class LoginVaultActivity extends Activity {
    private final int PIN_LENGTH = 6;

    private RadioButton[] dots = new RadioButton[PIN_LENGTH];
    private String currentPIN = "";
    private TextView message;
    private Button[] numkeys = new Button[10];

    private Animation animationForRadioGroups;
    ProgressBar progressBar;

    boolean alreadyResetPIN = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_vault);

        //Add animation
        animationForRadioGroups = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.login_vault_vibrate_animation);

        //Get radio
        for (int i = 0; i < PIN_LENGTH; i++) {
            String radioID = "dot" + String.valueOf(i) + "_login_vault";
            int resID = getResources().getIdentifier(radioID, "id", getPackageName());
            dots[i] = (RadioButton) findViewById(resID);
        }

        //Get message Textview
        message = (TextView) findViewById(R.id.message_login_vault);
        message.setText(R.string.login_vault_subtitle);

        //Get and set event for keyboard
        setupKeyboard();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_login_vault);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void setupKeyboard() {
        for (int i = 0; i < 10; i++) {
            String btnID = "num" + String.valueOf(i) + "_vault";
            int resID = getResources().getIdentifier(btnID, "id", getPackageName());
            numkeys[i] = (Button) findViewById(resID);

            final Button btn = numkeys[i];
            final int num = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Tránh trường hợp đang xác thực mã PIN mà người dùng vẫn nhập
                    if (currentPIN.length() == PIN_LENGTH) {
                        return;
                    }

                    //Status of radio group when input password
                    currentPIN = currentPIN + String.valueOf(num);
                    dots[currentPIN.length() - 1].setChecked(true);

                    //Status of notification when input first word
                    if (currentPIN.length() == 1) {
                        message.setText("");
                    }

                    //Check password when input enough
                    if (currentPIN.length() == PIN_LENGTH) {
                        progressBar.setVisibility(View.VISIBLE);
                        validatePIN();
                    }
                }
            });
        }
    }

    private void validatePIN() {
        new Thread() {
            @Override
            public void run() {
                if (alreadyResetPIN) {
                    FirebaseAuth.getInstance().signOut();
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(Account.email, currentPIN).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Account.updatePIN(currentPIN, LoginVaultActivity.this);
                                progressBar.setVisibility(View.INVISIBLE);
                                alreadyResetPIN = false;

                                Intent intent = new Intent(LoginVaultActivity.this, VaultAlbumActivity.class);
                                finish();
                                startActivity(intent);
                            } else {
                                alertWrongPIN();
                            }

                        }
                    });
                } else if (Account.checkPIN(currentPIN)) {
                    VaultManager.getInstance(LoginVaultActivity.this).getAllDecryptedBitmaps();
                    progressBar.setVisibility(View.INVISIBLE);

                    Intent intent = new Intent(LoginVaultActivity.this, VaultAlbumActivity.class);
                    finish();
                    startActivity(intent);
                } else {
                    alertWrongPIN();
                }
            }
        }.start();
    }

    private void alertWrongPIN() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);

                //Add animation for radio group
                LinearLayout radioGroup = (LinearLayout) findViewById(R.id.pin_login_vault);
                radioGroup.startAnimation(animationForRadioGroups);

                //Delete status of all dots
                currentPIN = "";
                for (int i = 0; i < PIN_LENGTH; i++) {
                    dots[i].setChecked(false);
                }

                //Notification for user that "password fail"
                message.setText(R.string.login_vault_password_incorrect);
            }
        });
    }

    public void onDelete(View view) {
        if (!currentPIN.isEmpty()) {
            dots[currentPIN.length() - 1].setChecked(false);
            currentPIN = currentPIN.substring(0, currentPIN.length() - 1);
        }
    }

    public void onForgot(View view) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(Account.email);
        alreadyResetPIN = true;
        Toast.makeText(this, getResources().getString(R.string.login_vault_forgot_message), Toast.LENGTH_LONG).show();
    }
}