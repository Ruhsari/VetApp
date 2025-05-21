package com.example.vetapp.model;

public class Schedule {
    private String specialistId;
    private String date;
    private String time;

    public Schedule(String specialistId, String date, String time) {
        this.specialistId = specialistId;
        this.date = date;
        this.time = time;
    }

    public String getSpecialistId() {
        return specialistId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return specialistId + "," + date + "," + time;
    }

    public static Schedule fromString(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(",");
        if (parts.length != 3) return null;
        return new Schedule(parts[0].trim(), parts[1].trim(), parts[2].trim());
    }
}