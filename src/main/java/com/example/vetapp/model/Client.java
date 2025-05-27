package com.example.vetapp.model;

public class Client extends User {
    private String fullName;
    private String phone;
    private String address;

    public Client(String username, String password, String fullName, String phone, String address) {
        super(username, password, "client");
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
    }

    // Getters
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }

    @Override
    public String toString() {
        return "Client{" +
                "username='" + getUsername() + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}