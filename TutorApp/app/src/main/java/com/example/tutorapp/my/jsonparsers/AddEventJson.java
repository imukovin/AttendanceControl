package com.example.tutorapp.my.jsonparsers;

import com.example.tutorapp.EventFragment;
import com.example.tutorapp.my.objects.Event;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class AddEventJson {
    private EventFragment ev;
    private Event event;

    public AddEventJson(EventFragment ev, Event event) {
        this.ev = ev;
        this.event = event;
    }

    public JSONObject build() throws JSONException {
        Gson gson = new Gson();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("requestType", "AddNewEventTutor");
        jsonObject.put("event", gson.toJson(event));
        return jsonObject;
    }

    public boolean parseAndGetResult(String resultJson) throws JSONException {
        JSONObject jsonObject = new JSONObject(resultJson);
        boolean status = jsonObject.getBoolean("status");
        return status;
    }
}
