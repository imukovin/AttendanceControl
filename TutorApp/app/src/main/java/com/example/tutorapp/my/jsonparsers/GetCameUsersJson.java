package com.example.tutorapp.my.jsonparsers;

import com.example.tutorapp.my.objects.Event;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetCameUsersJson {
    private String teacherId;
    private String name;

    public GetCameUsersJson(String teacherId, String name) {
        this.teacherId = teacherId;
        this.name = name;
    }

    public JSONObject build() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requestType", "GetCameUsersTutor");
        jsonObject.put("id", teacherId);
        jsonObject.put("name", name);
        return jsonObject;
    }

    public List<String> parseAndGetResult(String resultJson) throws JSONException {
        Gson gson = new Gson();
        JSONObject jsonObject = new JSONObject(resultJson);
        boolean status = jsonObject.getBoolean("status");
        List<String> camePersons = new ArrayList<>();
        if (status) {
            String per = jsonObject.getString("camePersons").replace("[", "").replace("]", "");
            for (String s : per.split(",")) {
                camePersons.add(s.trim());
            }
            return camePersons;
        }
        return null;
    }
}
