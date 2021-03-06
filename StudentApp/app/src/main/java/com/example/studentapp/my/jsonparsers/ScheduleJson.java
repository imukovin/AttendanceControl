package com.example.studentapp.my.jsonparsers;

import android.content.SharedPreferences;

import com.example.studentapp.helpers.TheApplication;
import com.example.studentapp.my.objects.Event;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class ScheduleJson {
    private SharedPreferences preferences;

    public JSONObject build() {
        preferences = TheApplication.getInstance().getSharedPreferences("StudentAppSP", MODE_PRIVATE);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("requestType", "getScheduleStudent");
            jsonObject.put("name", preferences.getString("name", ""));
            jsonObject.put("date", new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public List<Event> parseAndGetResult(String resultJson) throws JSONException, ParseException {
        JSONObject jsonObject = new JSONObject(resultJson);
        Gson gson = new Gson();
        boolean status = jsonObject.getBoolean("status");
        List<Event> schedule = new ArrayList<>();
        if (status) {
            JSONArray array = new JSONArray(jsonObject.getString("schedule"));
            int len = array.length();
            for (int i = 0; i < len; i++){
                JSONObject event = (JSONObject) array.get(i);
                schedule.add(gson.fromJson(event.toString(), Event.class));
            }
            return schedule;
        }
        return null;
    }
}
