package com.example.tutorapp.my.jsonparsers;

import com.example.tutorapp.my.objects.Event;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetAllTeacherEvents {
    private String teacherId;

    public GetAllTeacherEvents(String teacherId) {
        this.teacherId = teacherId;
    }

    public JSONObject build() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requestType", "GetAllTeacherEventsTutor");
        jsonObject.put("id", teacherId);
        return jsonObject;
    }

    public List<Event> parseAndGetResult(String resultJson) throws JSONException {
        Gson gson = new Gson();
        JSONObject jsonObject = new JSONObject(resultJson);
        boolean status = jsonObject.getBoolean("status");
        List<Event> events = new ArrayList<>();
        if (status) {
            JSONArray array = new JSONArray(jsonObject.getString("events"));
            int len = array.length();
            for (int i = 0; i < len; i++){
                JSONObject event = (JSONObject) array.get(i);
                events.add(gson.fromJson(event.toString(), Event.class));
            }
            return events;
        }
        return null;
    }
}
