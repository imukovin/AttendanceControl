package com.example.tutorapp.my.objects;

import java.time.LocalDateTime;
import java.util.Date;

public class Schedule {
    private Integer id;
    private String tutorName;
    private String subjectName;
    private Date date;

    public Schedule() {
    }

    public Schedule(Integer id, String tutorName, String subjectName, Date date) {
        this.id = id;
        this.tutorName = tutorName;
        this.subjectName = subjectName;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
