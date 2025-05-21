package com.example.vetapp.model;

public class Specialist {
    private String id;
    private String clinicId;
    private String name;
    private String specialization;

    public Specialist(String id, String clinicId, String name, String specialization) {
        this.id = id;
        this.clinicId = clinicId;
        this.name = name;
        this.specialization = specialization;
    }

    public String getId() {
        return id;
    }

    public String getClinicId() {
        return clinicId;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public static Specialist fromString(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 4) {
            System.err.println("Некорректная строка специалиста: " + line + " (ожидается 4 части)");
            return null;
        }
        try {
            return new Specialist(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim());
        } catch (Exception e) {
            System.err.println("Ошибка парсинга специалиста: " + line + " - " + e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s", id, clinicId, name, specialization);
    }
}