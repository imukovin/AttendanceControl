package com.example.tutorapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import com.example.tutorapp.helpers.Helper;
import com.example.tutorapp.helpers.TheApplication;
import com.example.tutorapp.my.connection.MyConnection;
import com.example.tutorapp.my.objects.Event;
import com.example.tutorapp.my.jsonparsers.ScheduleJson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class ScheduleFragment extends Fragment {
    public final String SERVER_ADDRESS = Helper.getConfigValue(TheApplication.getInstance().getApplicationContext(),"server_address");
    public final String SERVER_PORT = Helper.getConfigValue(TheApplication.getInstance().getApplicationContext(),"server_port");

    TextView date;
    ListView lvSchedule;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        date = (TextView) view.findViewById(R.id.date);
        lvSchedule = (ListView) view.findViewById(R.id.lvSchedule);
        String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
        date.setText(currentDate);
        try {
            getSchedule();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
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

            Intent intent = new Intent(TheApplication.getInstance(), SubjectActivity.class);
            intent.putExtra("event", s);
            startActivity(intent);
            }
        });
    }
}
