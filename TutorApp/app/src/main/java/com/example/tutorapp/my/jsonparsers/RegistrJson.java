package com.example.tutorapp.my.jsonparsers;

import com.example.tutorapp.RegistrationActivity;
import com.example.tutorapp.my.objects.Person;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrJson {
    private RegistrationActivity rg;
    private String fio;
    private String login;
    private String pass;

    public RegistrJson(RegistrationActivity rg) {
        this.rg = rg;
        this.fio = null;
        this.login = null;
        this.pass = null;
    }

    public JSONObject build() throws JSONException {
        Person person = new Person();
        person.setName(rg.fio.getText().toString());
        person.setPhone(rg.login.getText().toString());
        person.setPassword(rg.password.getText().toString());
        person.setGroup("tutor");

        Gson gson = new Gson();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requestType", "RegistrRequestTutor");
        jsonObject.put("tutor", gson.toJson(person));
        System.out.println(jsonObject.toString());
        return jsonObject;
    }

    public boolean parseAndGetResult(String resultJson) throws JSONException {
        JSONObject jsonObject = new JSONObject(resultJson);
        return jsonObject.getBoolean("status");
    }
}
