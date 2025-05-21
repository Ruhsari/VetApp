package com.example.vetapp.model;

public class Appointment {
    private String username;
    private String clinicId;
    private String specialistId;
    private String petName;
    private String petType;
    private String date;
    private String time;
    private String reason;

    public Appointment(String username, String clinicId, String specialistId, String petName, String petType, String date, String time, String reason) {
        this.username = username;
        this.clinicId = clinicId;
        this.specialistId = specialistId;
        this.petName = petName;
        this.petType = petType;
        this.date = date;
        this.time = time;
        this.reason = reason;
    }

    public String getUsername() {
        return username;
    }

    public String getClinicId() {
        return clinicId;
    }

    public String getSpecialistId() {
        return specialistId;
    }

    public String getPetName() {
        return petName;
    }

    public String getPetType() {
        return petType;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return username + "," + clinicId + "," + specialistId + "," + petName + "," + petType + "," + date + "," + time + "," + reason;
    }

    public static Appointment fromString(String line) {
        String[] parts = line.split(",", -1); // Используем -1, чтобы сохранить пустые поля
        if (parts.length != 8) return null;
        return new Appointment(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]);
    }
}