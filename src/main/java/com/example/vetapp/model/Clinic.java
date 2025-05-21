package com.example.vetapp.model;

import java.util.Arrays;
import java.util.List;

public class Clinic {
    private String id;
    private String name;
    private String address;
    private String district;
    private double rating;
    private List<String> services;

    public Clinic(String id, String name, String address, String district, double rating, List<String> services) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.district = district;
        this.rating = rating;
        this.services = services;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getDistrict() {
        return district;
    }

    public double getRating() {
        return rating;
    }

    public List<String> getServices() {
        return services;
    }

    public static Clinic fromString(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 6) {
            System.err.println("Некорректная строка клиники: " + line + " (ожидается минимум 6 частей)");
            return null;
        }
        try {
            String id = parts[0].trim();
            String name = parts[1].trim();
            String address = parts[2].trim();
            String district = parts[3].trim();
            double rating = Double.parseDouble(parts[4].trim());
            List<String> services = Arrays.asList(parts[5].trim().split("\\s+"));
            return new Clinic(id, name, address, district, rating, services);
        } catch (Exception e) {
            System.err.println("Ошибка парсинга клиники: " + line + " - " + e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%.1f,%s",
                id, name, address, district, rating, String.join(" ", services));
    }
}