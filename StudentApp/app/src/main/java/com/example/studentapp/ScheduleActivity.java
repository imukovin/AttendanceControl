package com.example.studentapp;

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

import com.example.studentapp.helpers.Helper;
import com.example.studentapp.helpers.TheApplication;
import com.example.studentapp.my.connection.MyConnection;
import com.example.studentapp.my.jsonparsers.ScheduleJson;
import com.example.studentapp.my.objects.Event;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {
    public final String SERVER_ADDRESS = Helper.getConfigValue(TheApplication.getInstance().getApplicationContext(),"server_address");
    public final String SERVER_PORT = Helper.getConfigValue(TheApplication.getInstance().getApplicationContext(),"server_port");

    private TextView date;
    private ListView lvSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        date = findViewById(R.id.date_tv);
        lvSchedule = findViewById(R.id.lvSchedule);

        try {
            getSchedule();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        findViewById(R.id.close_schedule_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleActivity.this, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void getSchedule() throws JSONException, ParseException {
        ScheduleJson scheduleJson = new ScheduleJson();
        JSONObject request = scheduleJson.build();
        MyConnection connection = new MyConnection(SERVER_ADDRESS, Integer.parseInt(SERVER_PORT), request);
        connection.execute();

        String answer = "";
        try {
            answer = connection.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (connection.isAnswer) {
            List<Event> schedule = scheduleJson.parseAndGetResult(connection.getResponse());
            if (schedule != null) {
                fillListView(schedule);
            } else {
                date.append("\n\nРасписание на данный день отсутствует!");
            }
        } else {
            date.setText("Ошибка соединения с сервером!");
        }
    }

    private void fillListView(List<Event> list) {
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

        lvSchedule.setAdapter(adapter);
        lvSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event s = (Event) subjectMap.get(position);

                Intent intent = new Intent(TheApplication.getInstance(), ProgrammActivity.class);
                intent.putExtra("event", s);
                startActivity(intent);
            }
        });
    }
}
