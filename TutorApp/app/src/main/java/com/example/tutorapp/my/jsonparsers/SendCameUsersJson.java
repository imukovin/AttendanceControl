package com.example.tutorapp.my.jsonparsers;

import com.example.tutorapp.my.objects.Event;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SendCameUsersJson {
    private ArrayList<String> cameUsers;
    private Event event;

    public SendCameUsersJson(ArrayList<String> cameUsers, Event event) {
        this.cameUsers = cameUsers;
        this.event = event;
    }

    public JSONObject build() throws JSONException {
        Gson gson = new Gson();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requestType", "saveCameUsersTutor");
        jsonObject.put("event", gson.toJson(event));
        jsonObject.put("cameUsers", cameUsers);
        return jsonObject;
    }

    public boolean parseAndGetResult(String resultJson) throws JSONException {
        JSONObject jsonObject = new JSONObject(resultJson);
        return jsonObject.getBoolean("status");
    }
}
