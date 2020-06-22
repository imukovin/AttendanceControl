package com.example.tutorapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.example.tutorapp.my.jsonparsers.GetAllTeacherEvents;
import com.example.tutorapp.my.jsonparsers.GetCameUsersJson;
import com.example.tutorapp.my.jsonparsers.SendCameUsersJson;
import com.example.tutorapp.my.objects.Event;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static io.netty.handler.codec.rtsp.RtspHeaders.Values.SERVER_PORT;

public class CamePersonActivity extends AppCompatActivity {
    public final String SERVER_ADDRESS = Helper.getConfigValue(TheApplication.getInstance().getApplicationContext(),"server_address");
    public final String SERVER_PORT = Helper.getConfigValue(TheApplication.getInstance().getApplicationContext(),"server_port");

    private Event event;
    private List<String> camePerson;
    private ListView posetiteliLv;
    private TextView personTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_came_person);

        posetiteliLv = findViewById(R.id.posetiteli_lv);
        personTv = findViewById(R.id.posetiteli_tv);
        event = (Event) getIntent().getSerializableExtra("event");

        try {
            getCamePersonFromServer();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getCamePersonFromServer() throws JSONException {
        GetCameUsersJson getCameUsersJson = new GetCameUsersJson(event.getIdMainPerson(), event.getName());
        JSONObject request = getCameUsersJson.build();
        MyConnection connection = new MyConnection(SERVER_ADDRESS, Integer.parseInt(SERVER_PORT), request);
        connection.execute();

        String answer = "";
        try {
            answer = connection.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (connection.isAnswer) {
            camePerson = getCameUsersJson.parseAndGetResult(connection.getResponse());
            if (camePerson != null) {
                fillListView(camePerson);
            } else {
                personTv.append("\n\nПосетителей не было!");
            }
        } else {
            personTv.setText("Ошибка соединения с сервером!");
        }
    }

    private void fillListView(List<String> list) {
        System.out.println(list.size());
        String[] names = new String[event.getVisitors().size()];
        final SparseArray subjectMap = new SparseArray();
        for (int i = 0; i < event.getVisitors().size(); i++) {
            //System.out.println(list.get(i).toString());
            subjectMap.put(i, event.getVisitors().get(i));
            String sub;
            if (list.contains(event.getVisitors().get(i))) {
                sub = "  +  " + event.getVisitors().get(i);
            } else {
                sub = "  -  " + event.getVisitors().get(i);
            }
            names[i] = sub;
        }


        /*for (int i = 0; i < list.size(); i++) {
            //System.out.println(list.get(i).toString());
            subjectMap.put(i, list.get(i));
            String sub = list.get(i);
            names[i] = sub;
        }*/
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(TheApplication.getInstance(),
                R.layout.my_list_item, names);

        posetiteliLv.setAdapter(adapter);
        /*posetiteliLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event s = (Event) subjectMap.get(position);

                Intent intent = new Intent(TheApplication.getInstance(), SendCameUsersJson.class);
                intent.putExtra("event", s);
                startActivity(intent);
            }
        });*/
    }
}
