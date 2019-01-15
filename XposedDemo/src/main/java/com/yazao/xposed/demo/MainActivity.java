package com.yazao.xposed.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText userName;
    private EditText passWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = findViewById(R.id.username);
        passWord = findViewById(R.id.password);

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLegacy(userName.getText().toString(), passWord.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Submit Successfully!!!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Username or Password error!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkLegacy(String username, String password) {
        return !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && username.equals("moonlife") && password.equals("123456");
    }
}
