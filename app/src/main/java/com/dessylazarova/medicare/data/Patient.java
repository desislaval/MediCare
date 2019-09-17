package com.dessylazarova.medicare.data;

public class Patient {
    private String name;
    private String egn;
    private String address;
    private String phoneNumber;
    private String email;
    private String chronicDiseases;
    private String allergies;

    public Patient(String name, String egn, String address, String phoneNumber, String email, String chronicDiseases, String allergies) {
        this.name = name;
        this.egn = egn;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.chronicDiseases = chronicDiseases;
        this.allergies = allergies;
    }

    public Patient() {
        //Empty constructor needed by Firestore
    }

    public String getName() {
        return name;
    }

    public String getEGN() {
        return egn;
    }

    public String getAddress() {
        return address;
    }

    public String getChronicDiseases() {
        return chronicDiseases;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAllergies() {
        return allergies;
    }
}
