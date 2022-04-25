package com.example.project1ds;

public class Passenger extends Node{
    private int flightNumber;
    private int ticketNumber;
    private String fullName;
    private String passportNumber;
    private String nationality;
    private String birthDate;

    //Getters & Setters
    public int getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(int flightNumber) {
        this.flightNumber = flightNumber;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(int ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }


    public Passenger(int flightNumber, int ticketNumber, String fullName, String passportNumber, String nationality, String birthDate) {
        this.flightNumber = flightNumber;
        this.ticketNumber = ticketNumber;
        this.fullName = fullName;
        this.passportNumber = passportNumber;
        this.nationality = nationality;
        this.birthDate = birthDate;
    }

    @Override
    public void setKey(int key) {
        this.ticketNumber = key;
    }

    @Override
    public String toString() {
        return String.format("Flight #: %-5s | Ticket #: %-10s | Full Name: %-20s | Passport #: %-10s | " +
                " Nationality: %-12s | Birthdate: %-10s",flightNumber,ticketNumber,fullName,passportNumber,
                nationality,birthDate);
    }

    @Override
    public String toData() {
        return (this.flightNumber + "," + this.ticketNumber + "," + this.fullName + "," + this.passportNumber + ","
                + this.nationality + "," + this.birthDate);
    }

    @Override
    public int getKey() {
        return this.ticketNumber;
    }
}
