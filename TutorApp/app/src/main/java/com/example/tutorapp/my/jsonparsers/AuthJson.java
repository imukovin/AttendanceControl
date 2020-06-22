package com.example.tutorapp.my.jsonparsers;

import com.example.tutorapp.LoginActivity;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthJson {
    private LoginActivity lg;
    private String id;
    private String login;
    private String pass;
    private String name;

    public AuthJson(LoginActivity lg) {
        this.lg = lg;
        this.login = null;
        this.pass = null;
        this.name = null;
    }

    public JSONObject build() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requestType", "AuthRequestTutor");
        jsonObject.put("login", lg.login.getText());
        return jsonObject;
    }

    public boolean parseAndGetResult(String resultJson) throws JSONException {
        JSONObject jsonObject = new JSONObject(resultJson);
        boolean status = jsonObject.getBoolean("status");
        if (status) {
            JSONObject ob = new JSONObject(jsonObject.getString("tutor"));
            id = new JSONObject(ob.getString("_id")).get("$oid").toString();
            login = ob.getString("phone");
            pass = ob.getString("password");
            name = ob.getString("name");
            return true;
        }
        return false;
    }

    public String getId() {
        return id;
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
}