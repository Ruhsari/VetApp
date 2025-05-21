//package com.example.vetapp.service;
//
//import com.example.vetapp.model.Clinic;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ClinicService {
//    private static final Path CLINICS_PATH = Paths.get("C:", "CourseWork", "vetapp", "src", "main", "java", "com", "example", "vetapp", "data", "clinics.txt");
//
//    public static List<Clinic> loadClinics() {
//        List<Clinic> clinics = new ArrayList<>();
//        try {
//            Files.createDirectories(CLINICS_PATH.getParent());
//            if (!Files.exists(CLINICS_PATH)) {
//                System.out.println("Файл клиник не существует, создаем новый: " + CLINICS_PATH.toAbsolutePath());
//                Files.createFile(CLINICS_PATH);
//                // Добавим несколько клиник по умолчанию
//                try (BufferedWriter writer = Files.newBufferedWriter(CLINICS_PATH, StandardCharsets.UTF_8, StandardOpenOption.WRITE)) {
//                    writer.write("Клиника 1,ул. Центральная 1,Центр,4.5,Вакцинация;Обследование\n");
//                    writer.write("Клиника 2,ул. Северная 5,Север,3.8,Хирургия;УЗИ\n");
//                    writer.write("Клиника 3,ул. Южная 10,Юг,4.8,Вакцинация;Стоматология\n");
//                }
//            }
//
//            try (BufferedReader reader = Files.newBufferedReader(CLINICS_PATH, StandardCharsets.UTF_8)) {
//                String line;
//                int lineNumber = 0;
//                while ((line = reader.readLine()) != null) {
//                    lineNumber++;
//                    String[] parts = line.split(",", -1);
//                    if (parts.length == 5) { // Теперь ожидаем 5 полей
//                        try {
//                            String name = parts[0].trim();
//                            String location = parts[1].trim();
//                            String district = parts[2].trim();
//                            double rating = Double.parseDouble(parts[3].trim());
//                            String services = parts[4].trim();
//                            Clinic clinic = new Clinic(name, location, district, rating, services);
//                            clinics.add(clinic);
//                            System.out.println("Успешно загружена клиника: " + clinic);
//                        } catch (Exception e) {
//                            System.err.println("Ошибка при создании Clinic из строки " + lineNumber + ": " + e.getMessage());
//                        }
//                    } else {
//                        System.err.println("Некорректная строка " + lineNumber + ": " + line + " (ожидается 5 частей)");
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Ошибка при загрузке клиник: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return clinics;
//    }
//}