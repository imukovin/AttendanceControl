package com.example.tutorapp.my.objects;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {
    private String name;
    private String date;
    private String time;
    private String location;
    private String idMainPerson;
    private ArrayList<String> visitors;

    public Event() {
    }

    public Event(String name, String date, String time, String location, String idMainPerson, ArrayList<String> visitors) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.idMainPerson = idMainPerson;
        this.visitors = visitors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIdMainPerson() {
        return idMainPerson;
    }

    public void setIdMainPerson(String idMainPerson) {
        this.idMainPerson = idMainPerson;
    }

    public ArrayList<String> getVisitors() {
        return visitors;
    }

    public void setVisitors(ArrayList<String> visitors) {
        this.visitors = visitors;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}