package com.example.vetapp.service;

import com.example.vetapp.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileDataService {

    private static final String CLINICS_FILE = "C:\\vetapp\\data\\clinics.txt";
    private static final String SPECIALISTS_FILE = "C:\\vetapp\\data\\specialists.txt";
    private static final String SCHEDULES_FILE = "C:\\vetapp\\data\\schedules.txt";
    private static final String APPOINTMENTS_FILE = "C:\\vetapp\\data\\appointments.txt";
    private static final String USERS_FILE = "C:\\vetapp\\data\\users.txt";
    private static final String SPECIALIST_LOGIN_FILE = "C:\\vetapp\\data\\specialist_login.txt";

    public static List<Clinic> loadClinics() throws IOException {
        List<Clinic> clinics = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(CLINICS_FILE));
        for (String line : lines) {
            String[] parts = line.split(",", -1);
            if (parts.length >= 6) {
                String id = parts[0].trim();
                String name = parts[1].trim();
                String address = parts[2].trim();
                String district = parts[3].trim();
                double rating = Double.parseDouble(parts[4].trim());
                List<String> services = Arrays.asList(parts[5].trim().split(";"));
                clinics.add(new Clinic(id, name, address, district, rating, services));
            }
        }
        return clinics;
    }

    public static List<User> loadUsers() throws IOException {
        List<User> users = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(USERS_FILE));
        for (String line : lines) {
            String[] parts = line.split(",", -1);
            System.out.println("Строка " + (lines.indexOf(line) + 1) + ": " + line + " | Частей: " + parts.length);
            if (parts.length == 6) {
                String username = parts[0].trim();
                String password = parts[1].trim();
                String role = parts[2].trim();
                String fullName = parts[3].trim();
                String phone = parts[4].trim();
                String address = parts[5].trim();
                if (role.equalsIgnoreCase("client")) {
                    Client client = new Client(username, password, fullName, phone, address);
                    users.add(client);
                    System.out.println("Успешно загружен пользователь: " + client);
                } else {
                    System.err.println("Роль '" + role + "' не поддерживается для клиента: " + line);
                }
            } else {
                System.err.println("Некорректная строка в файле пользователей: " + line);
            }
        }
        System.out.println("Всего загружено пользователей: " + users.size());
        return users;
    }

    public static List<Specialist> loadSpecialists() throws IOException {
        List<Specialist> specialists = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(SPECIALISTS_FILE));
        for (String line : lines) {
            String[] parts = line.split(",", -1);
            if (parts.length == 4) {
                String id = parts[0].trim();
                String clinicId = parts[1].trim();
                String name = parts[2].trim();
                String specialization = parts[3].trim();
                specialists.add(new Specialist(id, clinicId, name, specialization));
            }
        }
        return specialists;
    }

    public static List<Specialist> loadSpecialistsByClinicId(String clinicId) throws IOException {
        return loadSpecialists().stream()
                .filter(s -> s.getClinicId().equals(clinicId))
                .collect(Collectors.toList());
    }

    public static List<Schedule> loadSchedules() throws IOException {
        List<Schedule> schedules = new ArrayList<>();
        File file = new File(SCHEDULES_FILE);
        if (!file.exists()) {
            return schedules;
        }
        List<String> lines = Files.readAllLines(Paths.get(SCHEDULES_FILE));
        for (String line : lines) {
            String[] parts = line.split(",", -1);
            if (parts.length == 3) {
                String specialistId = parts[0].trim();
                String date = parts[1].trim();
                String time = parts[2].trim();
                schedules.add(new Schedule(specialistId, date, time));
            }
        }
        return schedules;
    }

    public static List<Appointment> loadAppointments() throws IOException {
        List<Appointment> appointments = new ArrayList<>();
        File file = new File(APPOINTMENTS_FILE);
        if (!file.exists()) {
            return appointments;
        }
        List<String> lines = Files.readAllLines(Paths.get(APPOINTMENTS_FILE));
        for (String line : lines) {
            String[] parts = line.split(",", -1);
            if (parts.length == 8) {
                String username = parts[0].trim();
                String clinicId = parts[1].trim();
                String specialistId = parts[2].trim();
                String petName = parts[3].trim();
                String petType = parts[4].trim();
                String date = parts[5].trim();
                String time = parts[6].trim();
                String reason = parts[7].trim();
                appointments.add(new Appointment(username, clinicId, specialistId, petName, petType, date, time, reason));
            } else {
                System.err.println("Некорректная строка записи: " + line);
            }
        }
        return appointments;
    }

    public static List<Appointment> loadAppointmentsByUsername(String username) throws IOException {
        return loadAppointments().stream()
                .filter(a -> a.getUsername().equals(username))
                .collect(Collectors.toList());
    }

    public static void saveAppointment(Appointment appointment) throws IOException {
        String appointmentLine = String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                appointment.getUsername(),
                appointment.getClinicId(),
                appointment.getSpecialistId(),
                appointment.getPetName(),
                appointment.getPetType(),
                appointment.getDate(),
                appointment.getTime(),
                appointment.getReason());
        List<Schedule> schedules = loadSchedules();
        schedules.removeIf(s -> s.getSpecialistId().equals(appointment.getSpecialistId()) &&
                s.getDate().equals(appointment.getDate()) &&
                s.getTime().equals(appointment.getTime()));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(APPOINTMENTS_FILE, true))) {
            writer.write(appointmentLine);
            writer.newLine();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCHEDULES_FILE))) {
            for (Schedule schedule : schedules) {
                writer.write(String.format("%s,%s,%s", schedule.getSpecialistId(), schedule.getDate(), schedule.getTime()));
                writer.newLine();
            }
        }
    }

    public static void deleteAppointment(Appointment appointment) throws IOException {
        List<Appointment> appointments = loadAppointments();
        appointments.removeIf(a -> a.getUsername().equals(appointment.getUsername()) &&
                a.getClinicId().equals(appointment.getClinicId()) &&
                a.getSpecialistId().equals(appointment.getSpecialistId()) &&
                a.getPetName().equals(appointment.getPetName()) &&
                a.getPetType().equals(appointment.getPetType()) &&
                a.getDate().equals(appointment.getDate()) &&
                a.getTime().equals(appointment.getTime()) &&
                a.getReason().equals(appointment.getReason()));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(APPOINTMENTS_FILE))) {
            for (Appointment a : appointments) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                        a.getUsername(), a.getClinicId(), a.getSpecialistId(),
                        a.getPetName(), a.getPetType(), a.getDate(), a.getTime(), a.getReason()));
                writer.newLine();
            }
        }
    }

    public static void addSchedule(Schedule schedule) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCHEDULES_FILE, true))) {
            writer.write(String.format("%s,%s,%s",
                    schedule.getSpecialistId(), schedule.getDate(), schedule.getTime()));
            writer.newLine();
        }
    }

    public static String authenticateSpecialistLogin(String username, String password) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(SPECIALIST_LOGIN_FILE), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length == 3 && parts[0].trim().equals(username) && parts[1].trim().equals(password)) {
                    return parts[2].trim();
                }
            }
        }
        return null;
    }
}