package com.example.project1ds;

public class SLL {
    private Node head;
    private Node tail;
    private int length;

    //Getters & Setters
    public Node getHead() {
        return head;
    }

    public void setHead(Node head) {
        this.head = head;
    }

    public Node getTail() {
        return tail;
    }

    public void setTail(Node tail) {
        this.tail = tail;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }


    //Inserting Flights
    public void insertFlight(int number, String airline, String source, String destination, int capacity){
        Flight newFlight = new Flight(number,airline,source,destination,capacity);
        if (this.head == null && this.tail == null) {
            this.head = newFlight;
            this.tail = newFlight;
            length = 1;
            return;
        }
        if (this.head.getNext() == null){
            Flight headFlight = (Flight) head;
            if (headFlight.getNumber() > number){
                insertNodeAtFirst(newFlight);
                return;
            }
            insertNodeAtLast(newFlight);
            return;
        }
           insertFlightBefore(flightNumberBefore(number),newFlight);
    }

    private void insertFlightBefore(int key, Flight newFlight){
        Flight current = (Flight) head, currentNext = (Flight) head.getNext();
        while (currentNext.getNext() != null && currentNext.getNumber() != key){
            current = currentNext;
            currentNext = (Flight) currentNext.getNext();
        }
        if (currentNext.getNumber() == key) {
            if (currentNext == head){
                insertNodeAtFirst(newFlight);
                return;
            }
            newFlight.setNext(currentNext);
            current.setNext(newFlight);
        }else{
            currentNext.setNext(newFlight);
            tail = newFlight;
        }
        length++;
    }

    private int flightNumberBefore(int newNumber){
        Flight current = (Flight) head;
        while (current.getNext() != null && current.getNumber() < newNumber)
            current = (Flight) current.getNext();
        if (current.getNumber() > newNumber)
            return current.getNumber();
        return newNumber;
    }


    //Inserting Passengers
    public void insertPassenger(int flightNumber, int ticketNumber, String fullName, String passportNumber, String nationality, String birthDate) {
        Passenger newPassenger = new Passenger(flightNumber,ticketNumber,fullName,passportNumber,nationality,birthDate);
        if (this.head == null && this.tail == null) {
            this.head = newPassenger;
            this.tail = newPassenger;
            length = 1;
            return;
        }
        if (this.head.getNext() == null){
            Passenger headPassenger = (Passenger) head;
            if (fullName.compareTo(headPassenger.getFullName()) < 0){
                insertNodeAtFirst(newPassenger);
                return;
            }
            insertNodeAtLast(newPassenger);
            return;
        }
        insertPassengerBefore(passengerNumberBefore(fullName),newPassenger);
    }

    private void insertPassengerBefore(int key,Passenger newPassenger){
        Passenger current = (Passenger) head, currentNext = (Passenger) head;
        while (currentNext.getNext() != null && currentNext.getTicketNumber() != key){
            current = currentNext;
            currentNext = (Passenger) currentNext.getNext();
        }
        if (currentNext.getTicketNumber() == key) {
            if (currentNext == head){
                insertNodeAtFirst(newPassenger);
                return;
            }
            newPassenger.setNext(currentNext);
            current.setNext(newPassenger);
        }else{
            currentNext.setNext(newPassenger);
            tail = newPassenger;
        }
        length++;
    }

    private int passengerNumberBefore(String newName){
        Passenger current = (Passenger) head;
        while (current.getNext() != null && newName.compareTo(current.getFullName()) > 0)
            current = (Passenger) current.getNext();
        return current.getTicketNumber();
    }

    //Inserting At First & At Last
    private void insertNodeAtFirst(Node newNode){
        newNode.setNext(head);
        head = newNode;
        length++;
    }

    private void insertNodeAtLast(Node newNode){
        tail.setNext(newNode);
        tail = newNode;
        length++;
    }


    //Getting A Specific Element With Key
    public Passenger getPassenger(int ticketNumber){
        Node current = this.head;
        while (current.getNext() != null && current.getKey() != ticketNumber)
            current = current.getNext();
        if (current.getKey() == ticketNumber)
            return (Passenger) current;
        return null;
    }
    public Passenger getPassengerByName(String name){
        name = name.toLowerCase();
        Passenger current = (Passenger) this.head;
        while (current.getNext() != null && current.getFullName().toLowerCase() != name) {
            if (current.getFullName().compareToIgnoreCase(name) == 0)
                return current;
            current = (Passenger) current.getNext();
        }
        if (current.getFullName().compareToIgnoreCase(name) == 0)
            return current;
        return null;
    }

    public Flight getFlight(int flightNumber){
        Node current = this.head;
        while (current.getNext() != null && current.getKey() != flightNumber)
            current = current.getNext();
        if (current.getKey() == flightNumber)
            return (Flight) current;
        return null;
    }

    //Exists
    public boolean flightExists(int key) {
        Node n1 = this.getFlight(key);
        if (n1 == null)
            return false;
        return true;
    }

    public boolean passengerExists(int key) {
        Node n1 = this.getPassenger(key);
        if (n1 == null)
            return false;
        return true;
    }

    //Removing From List
        public boolean removeByKey(int key){
        if (this.head == null)
            return false;

        Node current = this.head;
        Node prev = this.head;

        while (current.getNext() != null && current.getKey() != key) {
            prev = current;
            current = current.getNext();
        }
        if (current.getKey() == key){
            if (current == head)
                return removeFromFirst();
            if (current == tail)
                return removeFromLast();
            current.setKey(0);
            prev.setNext(current.getNext());
            current = null;
            return true;
        }
        return false;
    }

    public boolean removeFromFirst(){
        if (head == null)
            return false;
        Node temp = head;
        temp.setKey(0);
        head = head.getNext();
        temp = null;
        return true;
    }
    public boolean removeFromLast(){
        if (tail == null)
            return false;
        Node current = head;
        Node prev = head;
        while (current.getNext() != null){
            prev = current;
            current = current.getNext();
        }
        prev.setNext(null);
        tail = prev;
        current.setKey(0);
        current = null;
        return true;
    }

    public int maxKeyInList(){
        if (head == null)
            return 0;
        Node current = head;
        int max = current.getKey();
        while (current.getNext() != null){
            if (max < current.getKey())
                max = current.getKey();
            current = current.getNext();
        }
        return max;
    }
}
