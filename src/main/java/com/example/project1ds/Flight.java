package com.example.project1ds;

public class Flight extends Node {
    private int number;
    private String airline;
    private String source;
    private String destination;
    private int capacity;
    private SLL passengersList;

    //Getters & Setters
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public SLL getPassengersList() {
        return passengersList;
    }

    public void setPassengersList(SLL passengersList) {
        this.passengersList = passengersList;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Flight(int number, String airline, String source, String destination, int capacity) {
        this.number = number;
        this.airline = airline;
        this.source = source;
        this.destination = destination;
        this.capacity = capacity;
        this.passengersList = new SLL();
    }


    @Override
    public void setKey(int key) {
        this.number = key;
    }

    @Override
    public String toString() {
        return String.format("Flight #: %-5d | Airline: %-28s | From %-15s To %-15s | Capacity: %-5d",this.number,this.airline,this.source,this.destination,this.capacity);
    }

    @Override
    public String toData() {
        return (this.number + "," + this.airline + "," + this.source + "," + this.destination + "," + this.capacity);
    }

    @Override
    public int getKey() {
        return this.number;
    }


}
