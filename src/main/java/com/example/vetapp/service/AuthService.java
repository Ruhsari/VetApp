package com.example.vetapp.service;

import com.example.vetapp.model.User;
import com.example.vetapp.model.Client;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class AuthService {
    private static final Path USERS_PATH = Paths.get("C:", "vetapp", "data", "users.txt");

    public static User authenticate(String username, String password) throws IOException {
        System.out.println("Чтение файла для аутентификации: " + USERS_PATH.toAbsolutePath());
        List<User> users = loadUsers();
        System.out.println("Загружено пользователей: " + users.size());
        for (User user : users) {
            System.out.println("Сравниваю: введено username='" + username.trim() + "', password='" + password.trim() + "'");
            System.out.println("Сравниваю: в базе username='" + user.getUsername() + "', password='" + user.getPassword() + "'");
            if (user.getUsername().equalsIgnoreCase(username.trim()) &&
                    user.getPassword().equals(password.trim())) {
                System.out.println("Успешная аутентификация: " + user.getUsername());
                return user;
            }
        }
        System.out.println("Аутентификация не удалась для: " + username);
        return null;
    }

    public static boolean registerClient(Client client) throws IOException {
        Files.createDirectories(USERS_PATH.getParent());
        System.out.println("Проверка существования файла: " + USERS_PATH.toAbsolutePath());
        if (isUserExists(client.getUsername().trim())) {
            System.out.println("Пользователь уже существует: " + client.getUsername());
            return false;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                USERS_PATH,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            String line = String.format("%s,%s,client,%s,%s,%s%n",
                    client.getUsername().trim(),
                    client.getPassword().trim(),
                    client.getFullName().trim(),
                    client.getPhone().trim(),
                    client.getAddress().trim());
            writer.write(line);
            writer.flush();
            System.out.println("Пользователь зарегистрирован: " + client.getUsername() + " | Записано: " + line);
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return true;
    }

    private static boolean isUserExists(String username) throws IOException {
        return loadUsers().stream()
                .anyMatch(user -> user.getUsername().equalsIgnoreCase(username.trim()));
    }

    private static List<User> loadUsers() throws IOException {
        List<User> users = new ArrayList<>();
        if (!Files.exists(USERS_PATH)) {
            System.out.println("Файл не существует, создаем новый: " + USERS_PATH.toAbsolutePath());
            Files.createDirectories(USERS_PATH.getParent());
            Files.createFile(USERS_PATH);
            return users;
        }

        try (BufferedReader reader = Files.newBufferedReader(USERS_PATH, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split(",", -1);
                System.out.println("Строка " + lineNumber + ": " + line + " | Частей: " + parts.length);
                if (parts.length == 6) {
                    try {
                        Client client = new Client(
                                parts[0].trim(),
                                parts[1].trim(),
                                parts[3].trim(),
                                parts[4].trim(),
                                parts[5].trim()
                        );
                        users.add(client);
                        System.out.println("Успешно загружен пользователь: " + client);
                    } catch (Exception e) {
                        System.err.println("Ошибка при создании Client из строки " + lineNumber + ": " + e.getMessage());
                    }
                } else {
                    System.err.println("Некорректная строка " + lineNumber + ": " + line + " (ожидается 6 частей)");
                }
            }
        }
        System.out.println("Всего загружено пользователей: " + users.size());
        return users;
    }
}