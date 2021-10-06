package com.example.higallery;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    RadioButton[] radiosPassword = new RadioButton[4];
    EditText password;
    Button[] btnsKeyboard = new Button[10];
    Button back, delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get EditText
        password = (EditText) findViewById(R.id.password);

        //Get radio
        for (int i = 0; i < 4; i++) {
            String radioID = "radio_" + String.valueOf(i);
            int resID = getResources().getIdentifier(radioID, "id", getPackageName());
            radiosPassword[i] = (RadioButton) findViewById(resID);
        }

        //Get and set event for keyboard
        for (int i = 0; i < 10; i++) {
            //Get
            String btnID = "btn_" + String.valueOf(i);
            int resID = getResources().getIdentifier(btnID, "id", getPackageName());
            btnsKeyboard[i] = (Button) findViewById(resID);

            //Set
            final Button btn = btnsKeyboard[i];
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String valueOfPassword = password.getText().toString();
                    if(valueOfPassword.length() < 4) {
                        //Set new password
                        String newPassword = valueOfPassword + btn.getText().toString();
                        password.setText(newPassword);

                        //Set new status of radio
                        radiosPassword[newPassword.length() - 1].setChecked(true);
                        Toast.makeText(Login.this, password.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //Get and set event for delete
        delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valueOfPassword = password.getText().toString();
                if(valueOfPassword.length() > 0) {
                    //Set new password
                    String newPassword = valueOfPassword.substring(0, valueOfPassword.length() - 1);
                    password.setText(newPassword);

                    //Set new status of radio
                    radiosPassword[newPassword.length()].setChecked(false);
                    Toast.makeText(Login.this, password.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Get and set event for back
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}