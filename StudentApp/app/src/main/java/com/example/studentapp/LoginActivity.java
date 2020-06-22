package com.example.studentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.studentapp.helpers.Helper;
import com.example.studentapp.helpers.TheApplication;
import com.example.studentapp.my.connection.MyConnection;
import com.example.studentapp.my.jsonparsers.AuthJson;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    public final String SERVER_ADDRESS = Helper.getConfigValue(TheApplication.getInstance().getApplicationContext(),"server_address");
    public final String SERVER_PORT = Helper.getConfigValue(TheApplication.getInstance().getApplicationContext(),"server_port");

    public EditText login, password;
    Button button;
    TextView serverAnswerTextView;
    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (EditText) findViewById(R.id.masked_edit_text);
        password = (EditText) findViewById(R.id.editText2);
        button = (Button) findViewById(R.id.button);
        button.setEnabled(false);
        serverAnswerTextView = (TextView) findViewById(R.id.serverAnswerTextView);

        EditText[] edList = {login, password};
        CustomTextWatcher textWatcher = new CustomTextWatcher(edList, button);
        for (EditText editText : edList) {
            editText.addTextChangedListener(textWatcher);
        }

        /*
            Описание работы tryLogin в проекте TutorApp в классе LoginActivity
         */
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*try {
                    tryLogin();
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        });
    }

    private void tryLogin() throws JSONException {
        AuthJson authJson = new AuthJson(this);
        JSONObject request = authJson.build();
        MyConnection connection = new MyConnection(SERVER_ADDRESS, Integer.parseInt(SERVER_PORT), request);
        connection.execute();

        String answer = "";
        try {
            answer = connection.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (connection.isAnswer) {
            boolean isLogin = authJson.parseAndGetResult(connection.getResponse());
            if (isLogin && authJson.getPass().contentEquals(password.getText())) {
                sPref = getSharedPreferences("StudentAppSP", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("name", authJson.getName());
                ed.putString("login", authJson.getLogin());
                ed.putString("class", authJson.getClass1());
                ed.putString("isLogin", "true");
                ed.apply();
                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                serverAnswerTextView.setText("Указан неверный логин или пароль!");
            }
        } else {
            serverAnswerTextView.setText(answer);
        }
    }

    // класс проверяющий заполненность полей и активирующий кнопку входа
    public class CustomTextWatcher implements TextWatcher {
        View v;
        EditText[] edList;

        public CustomTextWatcher(EditText[] edList, Button v) {
            this.v = v;
            this.edList = edList;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            for (EditText editText : edList) {
                if (editText.getText().toString().trim().length() <= 0) {
                    v.setEnabled(false);
                    break;
                }
                else v.setEnabled(true);
            }
        }
    }
}
