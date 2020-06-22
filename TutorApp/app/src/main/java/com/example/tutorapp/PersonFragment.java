package com.example.tutorapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class PersonFragment extends Fragment {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        setInformation(view);
        return view;
    }

    private void setInformation(View view) {
        preferences = this.getActivity().getSharedPreferences("TutorAppSP", MODE_PRIVATE);
        editor = preferences.edit();

        TextView id = view.findViewById(R.id._id);
        TextView name = view.findViewById(R.id.name);
        TextView login = view.findViewById(R.id.login);

        view.findViewById(R.id.otchet_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListOtchetActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        if (!checkDataInPreferen()) {
            Button exit = view.findViewById(R.id.exit);
            exit.setVisibility(View.INVISIBLE);

        } else {
            String i = "Id: " + preferences.getString("id", "");
            String n = "Имя: " + preferences.getString("name", "");
            String l = "Логин: " + preferences.getString("login", "");

            id.setText(i);
            name.setText(n);
            login.setText(l);

            Button exit = view.findViewById(R.id.exit);
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.clear();
                    editor.apply();
                    if (!checkDataInPreferen()) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private boolean checkDataInPreferen() {
        if (preferences.getString("name", "").equals("")) {
            return false;
        }
        return true;
    }
}
