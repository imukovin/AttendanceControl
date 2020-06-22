package com.example.tutorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorapp.helpers.Helper;
import com.example.tutorapp.helpers.TheApplication;
import com.example.tutorapp.my.connection.MyConnection;
import com.example.tutorapp.my.jsonparsers.AuthJson;
import com.example.tutorapp.my.jsonparsers.RegistrJson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.sql.Connection;

public class RegistrationActivity extends AppCompatActivity {
    public final String SERVER_ADDRESS = Helper.getConfigValue(TheApplication.getInstance().getApplicationContext(),"server_address");
    public final String SERVER_PORT = Helper.getConfigValue(TheApplication.getInstance().getApplicationContext(),"server_port");

    public EditText fio, login, password, confitmPassword;
    Button button;
    TextView serverAnswerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        fio = (EditText) findViewById(R.id.fio);
        login = (EditText) findViewById(R.id.masked_edit_text);
        password = (EditText) findViewById(R.id.pass);
        confitmPassword = (EditText) findViewById(R.id.confirmPass);
        button = (Button) findViewById(R.id.button);
        button.setEnabled(false);
        serverAnswerTextView = (TextView) findViewById(R.id.serverAnswerTextView);

        EditText[] edList = {fio, login, password, confitmPassword};
        RegistrationActivity.CustomTextWatcher textWatcher = new RegistrationActivity.CustomTextWatcher(edList, button);
        for (EditText editText : edList) {
            editText.addTextChangedListener(textWatcher);
        }

        /*
                По нажатию на клавишу button вызывается метод отправляющий логин, пароль и ФИО на сервер.
                Принимается  ответ с сервера об успешной регистрации.
                Запусается LoginActivity через Intent.

                В методе tryRegistr создается JSON-объект (запрос) для отправки на сервер (класс RegistJson),
                создается класс MyConnection, который отправляет запрос и принимает ответ. Если ответ
                не получен в течении 10 сек, то ошибка.
         */
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = password.getText().toString().trim();
                String confPass = confitmPassword.getText().toString().trim();
                if (pass.equals(confPass)) {
                    /*try {
                        tryRegistr();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                } else {
                    serverAnswerTextView.setText("Пароли не совпадают!");
                }
            }
        });
    }

    private void tryRegistr() throws JSONException {
        RegistrJson registrJson = new RegistrJson(this);
        JSONObject request = registrJson.build();
        MyConnection connection = new MyConnection(SERVER_ADDRESS,
                Integer.parseInt(SERVER_PORT), request);
        connection.execute();

        String answer = "";
        try {
            answer = connection.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (connection.isAnswer) {
            boolean isRegistr = registrJson.parseAndGetResult(
                    connection.getResponse());
            if (isRegistr) {
                Toast toast = Toast.makeText(getApplicationContext(), "Регистрация прошла успешно! Авторизуйтесь!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                serverAnswerTextView.setText("Ошибка регисрации! Повторите попытку позже!");
            }
        } else {
            serverAnswerTextView.setText(answer);
        }
    }

    //------------------------------------------------------------------------------
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
