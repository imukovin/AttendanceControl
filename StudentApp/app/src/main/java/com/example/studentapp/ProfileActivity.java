package com.example.studentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setInformation();

        findViewById(R.id.close_profile_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void setInformation() {
        preferences = this.getSharedPreferences("StudentAppSP", MODE_PRIVATE);
        editor = preferences.edit();

        String n = "Имя: " + preferences.getString("name", "");
        String l = "Логин: " + preferences.getString("login", "");
        String c = "Группа: " + preferences.getString("class", "");

        TextView name = findViewById(R.id.name);
        TextView login = findViewById(R.id.login);
        TextView class1 = findViewById(R.id.class1);
        name.setText(n);
        login.setText(l);
        class1.setText(c);

        Button exit = findViewById(R.id.exit_btn);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.remove("name");
                editor.remove("login");
                editor.remove("class");
                editor.remove("isLogin");
                editor.apply();

                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
