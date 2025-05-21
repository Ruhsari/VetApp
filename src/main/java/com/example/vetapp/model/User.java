package com.example.vetapp.model;

public class User {
    private String username;
    private String password;
    private String role; // Добавляем поле для роли (например, "client" или "admin")
    private String fullName;
    private String phone;
    private String address;

    // Конструктор для трёх параметров (для Client)
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = ""; // Устанавливаем значения по умолчанию
        this.phone = "";
        this.address = "";
    }

    // Конструктор для пяти параметров (для чтения из файла)
    public User(String username, String fullName, String phone, String address, String password) {
        this.username = username;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.role = "client"; // По умолчанию роль "client", если не указана
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return username + "," + fullName + "," + phone + "," + address + "," + password + "," + role;
    }

    public static User fromString(String line) {
        if (line == null || line.trim().isEmpty()) return null;

        String[] parts = line.split(",");
        if (parts.length != 6) return null; // Теперь ожидаем 6 полей: username, fullName, phone, address, password, role

        String username = parts[0].trim();
        String fullName = parts[1].trim();
        String phone = parts[2].trim();
        String address = parts[3].trim();
        String password = parts[4].trim();
        String role = parts[5].trim();

        User user = new User(username, fullName, phone, address, password);
        user.role = role; // Устанавливаем роль
        return user;
    }
}