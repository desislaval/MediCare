package com.dessylazarova.medicare.data;


import com.google.firebase.Timestamp;

public class CheckUp {
    private Timestamp date;
    private String treatment;
    private String symptoms;

    public CheckUp(){
        //required by firestore
    }
    public CheckUp( String treatment, String symptoms) {
        this.date = Timestamp.now();
        this.treatment = treatment;
        this.symptoms = symptoms;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getTreatment() {
        return treatment;
    }

    public String getSymptoms() {
        return symptoms;
    }
}
