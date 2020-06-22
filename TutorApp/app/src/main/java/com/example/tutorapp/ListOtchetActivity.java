package com.example.tutorapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tutorapp.helpers.Helper;
import com.example.tutorapp.helpers.TheApplication;
import com.example.tutorapp.my.connection.MyConnection;
import com.example.tutorapp.my.jsonparsers.SendCameUsersJson;
import com.example.tutorapp.my.jsonparsers.GetAllTeacherEvents;
import com.example.tutorapp.my.objects.Event;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ListOtchetActivity extends AppCompatActivity {
    public final String SERVER_ADDRESS = Helper.getConfigValue(TheApplication.getInstance().getApplicationContext(),"server_address");
    public final String SERVER_PORT = Helper.getConfigValue(TheApplication.getInstance().getApplicationContext(),"server_port");


    private TextView otchetTv;
    private ListView listView;
    private SharedPreferences pref;
    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_otchet);

        otchetTv = findViewById(R.id.otchet_tv);
        listView = findViewById(R.id.all_subjects_lv);
        pref = getSharedPreferences("TutorAppSP", MODE_PRIVATE);
        String teacherId = pref.getString("id", "");

        try {
            getEventListFromServer(teacherId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getEventListFromServer(String teacherId) throws JSONException {
        GetAllTeacherEvents getAllTeacherEvents = new GetAllTeacherEvents(teacherId);
        JSONObject request = getAllTeacherEvents.build();
        MyConnection connection = new MyConnection(SERVER_ADDRESS, Integer.parseInt(SERVER_PORT), request);
        connection.execute();

        String answer = "";
        try {
            answer = connection.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (connection.isAnswer) {
            eventList = getAllTeacherEvents.parseAndGetResult(connection.getResponse());
            if (eventList != null) {
                fillListView(eventList);
            } else {
                otchetTv.append("\n\nУ вас не было мероприятий!");
            }
        } else {
            otchetTv.setText("Ошибка соединения с сервером!");
        }
    }

    private void fillListView(List<Event> list) {
        System.out.println(list.size());
        String[] names = new String[list.size()];
        final SparseArray subjectMap = new SparseArray();
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).toString());
            Event s = list.get(i);
            subjectMap.put(i, list.get(i));
            String sub = s.getName() + "\nДата и время: "
                    + s.getDate() + " " + s.getTime().replace(" ", "") + "\nМесто проведения: "
                    + s.getLocation();
            names[i] = sub;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(TheApplication.getInstance(),
                R.layout.my_list_item, names);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event s = (Event) subjectMap.get(position);

                Intent intent = new Intent(TheApplication.getInstance(), CamePersonActivity.class);
                intent.putExtra("event", s);
                startActivity(intent);
            }
        });
    }
}
