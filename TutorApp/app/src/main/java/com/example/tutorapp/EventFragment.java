package com.example.tutorapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tutorapp.helpers.Helper;
import com.example.tutorapp.helpers.TheApplication;
import com.example.tutorapp.my.connection.MyConnection;
import com.example.tutorapp.my.jsonparsers.AddEventJson;
import com.example.tutorapp.my.objects.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class EventFragment extends Fragment {
    public final String SERVER_ADDRESS = Helper.getConfigValue(TheApplication.getInstance().getApplicationContext(),"server_address");
    public final String SERVER_PORT = Helper.getConfigValue(TheApplication.getInstance().getApplicationContext(),"server_port");

    private SharedPreferences pref;
    private ListView visitorsListView;
    private TextView eventNameTV;
    private EditText eventDateEt;
    private EditText eventTimeEt;
    private TextView eventLocationTV;
    private Button eventAddBtn;
    private Context context;

    private ArrayList<String> visitors;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        pref = this.getActivity().getSharedPreferences("TutorAppSP", MODE_PRIVATE);
        visitorsListView = view.findViewById(R.id.visitors_list_view);
        eventNameTV = view.findViewById(R.id.eventNameEt);
        eventLocationTV = view.findViewById(R.id.eventLocationEt);
        eventDateEt = view.findViewById(R.id.event_date_tv);
        eventTimeEt = view.findViewById(R.id.event_time_tv);
        eventAddBtn = view.findViewById(R.id.add_event_btn);
        context = view.getContext();

        eventDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDatePicker();
            }
        });

        eventTimeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callTimePicker();
            }
        });

        eventAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                visitors = new ArrayList<>();
                SparseBooleanArray itemChecked = visitorsListView.getCheckedItemPositions();
                for (int i = 0; i < itemChecked.size(); i++) {
                    int key = itemChecked.keyAt(i);
                    boolean value = itemChecked.get(key);
                    if (value) {
                        visitors.add(visitorsListView.getItemAtPosition(key).toString());
                    }
                }

                if (visitors.isEmpty() || eventNameTV.getText().toString().equals("")
                        || eventDateEt.getText().toString().equals("")
                        || eventTimeEt.getText().toString().equals("")
                        || eventLocationTV.getText().toString().equals("")) {
                    Toast.makeText(context, "Одно из полей не заполнено!", Toast.LENGTH_SHORT).show();
                } else {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
                    String name = eventNameTV.getText().toString().trim();
                    String date = eventDateEt.getText().toString().trim();
                    try {
                        // преобразование даты (добавление 0)
                        Date date1 = formatter.parse(date);
                        date = formatter.format(date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String time = eventTimeEt.getText().toString().trim();
                    String location = eventLocationTV.getText().toString().trim();
                    String idMainPerson = pref.getString("id", "");
                    Event newEvent = new Event(name, date, time, location, idMainPerson, visitors);
                    try {
                        saveEventOnServer(newEvent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        try {
            getListOfVisitors();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void saveEventOnServer(Event newEvent) throws JSONException {
        AddEventJson addEventJson = new AddEventJson(this, newEvent);
        JSONObject request = addEventJson.build();
        MyConnection connection = new MyConnection(SERVER_ADDRESS, Integer.parseInt(SERVER_PORT), request);
        connection.execute();

        String answer = "";
        try {
            answer = connection.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (connection.isAnswer) {
            boolean wasSaved = addEventJson.parseAndGetResult(connection.getResponse());
            if (wasSaved) {
                Toast.makeText(context, "Мероприятие создано!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Мероприятие не создано!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getListOfVisitors() throws JSONException {
        JSONObject request = new JSONObject();
        request.put("requestType", "getListOfVisitors");
        MyConnection connection = new MyConnection(SERVER_ADDRESS, Integer.parseInt(SERVER_PORT), request);
        connection.execute();

        ArrayList<String> visitorsList;
        String answer = "";
        try {
            answer = connection.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (connection.isAnswer) {
            JSONArray jsonArray = new JSONArray(connection.getResponse());
            visitorsList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                String visitor = (String) jsonArray.get(i);
                visitorsList.add(visitor);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_list_item_multiple_choice, visitorsList);
            visitorsListView.setAdapter(adapter);
        }
    }

    private void callDatePicker() {
        // получаем текущую дату
        final Calendar cal = Calendar.getInstance();
        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DAY_OF_MONTH);

        // инициализируем диалог выбора даты текущими значениями
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String editTextDateParam = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
                        eventDateEt.setText(editTextDateParam);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    private void callTimePicker() {
        // получаем текущее время
        final Calendar cal = Calendar.getInstance();
        int mHour = cal.get(Calendar.HOUR_OF_DAY);
        int mMinute = cal.get(Calendar.MINUTE);

        // инициализируем диалог выбора времени текущими значениями
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String editTextTimeParam = hourOfDay + " : " + minute;
                        eventTimeEt.setText(editTextTimeParam);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }
}
