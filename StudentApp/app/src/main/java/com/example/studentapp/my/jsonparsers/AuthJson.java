package com.example.studentapp.my.jsonparsers;

import com.example.studentapp.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthJson {
    private LoginActivity lg;
    private String login;
    private String pass;
    private String name;
    private String class1;

    public AuthJson(LoginActivity lg) {
        this.lg = lg;
        this.login = null;
        this.pass = null;
        this.name = null;
        this.class1 = null;
    }

    public JSONObject build() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requestType", "AuthRequestStudent");
        jsonObject.put("login", lg.login.getText());
        return jsonObject;
    }

    public boolean parseAndGetResult(String resultJson) throws JSONException {
        JSONObject jsonObject = new JSONObject(resultJson);
        boolean status = jsonObject.getBoolean("status");
        if (status) {
            JSONObject ob = new JSONObject(jsonObject.getString("student"));
            login = ob.getString("phone");
            pass = ob.getString("password");
            name = ob.getString("name");
            class1 = ob.getString("class");
            return true;
        }
        return false;
    }

    public LoginActivity getLg() {
        return lg;
    }

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }

    public String getName() {
        return name;
    }

    public String getClass1() {
        return class1;
    }
}
