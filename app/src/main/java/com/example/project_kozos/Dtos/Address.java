package com.example.project_kozos.Dtos;

public class Address {
    private String country;
    private String state;
    private String city;
    private String street;
    private String house_number;

    public Address(String country, String state, String city, String street, String houseNumber) {
        this.country = country;
        this.state = state;
        this.city = city;
        this.street = street;
        house_number = houseNumber;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country){
        this.country = country;
    }
    public String getState() {
        return state;
    }
    public void setState(String state){
        this.state = state;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city){
        this.city = city;
    }
    public String getStreet() {
        return street;
    }
    public void setStreet(String street){
        this.street = street;
    }
    public String getHouse_number() {
        return house_number;
    }
    public void setHouse_number(String houseNumber){
        this.house_number = houseNumber;
    }
}
