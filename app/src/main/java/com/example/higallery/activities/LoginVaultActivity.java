package com.example.higallery.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.example.higallery.R;

public class LoginVaultActivity extends Activity {
    private final int PIN_LENGTH = 4;
    private final String CORRECT_PIN = "1234";

    private RadioButton[] dots = new RadioButton[PIN_LENGTH];
    private String currentPIN = "";
    private Button[] numkeys = new Button[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_vault);

        //Get radio
        for (int i = 0; i < PIN_LENGTH; i++) {
            String radioID = "dot" + String.valueOf(i) + "_login_vault";
            int resID = getResources().getIdentifier(radioID, "id", getPackageName());
            dots[i] = (RadioButton) findViewById(resID);
        }

        //Get and set event for keyboard
        for (int i = 0; i < 10; i++) {
            String btnID = "num" + String.valueOf(i) + "_vault";
            int resID = getResources().getIdentifier(btnID, "id", getPackageName());
            numkeys[i] = (Button) findViewById(resID);

            final Button btn = numkeys[i];
            final int num = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Tránh trường hợp đang xác thực mã PIN mà người dùng vẫn nhập
                    if (currentPIN.length() == PIN_LENGTH) {
                        return;
                    }

                    currentPIN = currentPIN + String.valueOf(num);
                    dots[currentPIN.length() - 1].setChecked(true);
                    if (currentPIN.length() == PIN_LENGTH) {
                        validatePIN();
                    }
                }
            });
        }
    }

    private void validatePIN() {
        if (currentPIN.equals(CORRECT_PIN)) {
            Intent intent = new Intent(this, VaultAlbumActivity.class);
            finish();
            startActivity(intent);
        } else {
            currentPIN = "";
            for (int i = 0; i < PIN_LENGTH; i++) {
                dots[i].setChecked(false);
            }
        }
    }

    public void onDelete(View view) {
        if (!currentPIN.isEmpty()) {
            dots[currentPIN.length() - 1].setChecked(false);
            currentPIN = currentPIN.substring(0, currentPIN.length() - 1);
        }
    }

    public void onForgot(View view) {
        finish();
    }
}