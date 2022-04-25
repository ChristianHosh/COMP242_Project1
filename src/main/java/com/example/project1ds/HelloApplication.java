package com.example.project1ds;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class HelloApplication extends Application {

    //Static Variables
    static Color beigeBackgroundColor = Color.web("#BCB0A1");
    static Color coffeeMediumColor = Color.web("#985836");
    static Color coffeeDarkColor = Color.web("#482317");
    static String beigeBackgroundColorS = "#BCB0A1";
    static String coffeeMediumColorS = "#985836";
    static String coffeeDarkColorS = "#482317";

    static File passengerFile;
    static File flightsFile;

    static Button topMenuBtn_DisplayFlightInfo = new Button("FLIGHT INFO");
    static Button topMenuBtn_DisplayPassengerInfo = new Button("PASSENGER INFO");
    static Button topMenuBtn_SaveExit = new Button("SAVE & EXIT");

    static AnchorPane flightPane = new AnchorPane();
    static AnchorPane passengerPane = new AnchorPane();

    static SLL flightsList = new SLL();
    static SLL passengersList = new SLL();

    //Reading From Database
    private static void readFlights(File flightsFile) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(flightsFile);
        while (fileScanner.hasNext()){
            String line = fileScanner.nextLine();
            String[] lineSplit = line.split(",");
            flightsList.insertFlight(Integer.parseInt(lineSplit[0]),lineSplit[1],lineSplit[2],lineSplit[3],Integer.parseInt(lineSplit[4]));
        }
        fileScanner.close();
    }

    private static void readPassengers(File passengersFile) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(passengersFile);
        while (fileScanner.hasNext()){
            String line = fileScanner.nextLine();
            String[] lineSplit = line.split(",");
            int flightNum = Integer.parseInt(lineSplit[0]);
            Flight current = flightsList.getFlight(flightNum);
            if (current != null)
                current.getPassengersList().insertPassenger(flightNum,Integer.parseInt(lineSplit[1]),lineSplit[2],lineSplit[3],lineSplit[4],lineSplit[5]);
            passengersList.insertPassenger(flightNum,Integer.parseInt(lineSplit[1]),lineSplit[2],lineSplit[3],lineSplit[4],lineSplit[5]);
        }
        fileScanner.close();
    }

    //Main Driver Methods
    @Override
    public void start(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Passengers File");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Text Files", "*.txt"),
                new ExtensionFilter("All Files", "*.*"));
        passengerFile = fileChooser.showOpenDialog(stage);
        fileChooser.setTitle("Open Flights File");
        flightsFile = fileChooser.showOpenDialog(stage);
        try{
            readFlights(flightsFile);
            readPassengers(passengerFile);
        }catch (Exception e){
            System.out.println("Error reading from files");
        }

        AnchorPane root = setUpRoot();
        Scene scene = new Scene(root);
        stage.setMaximized(true);
        stage.setTitle("Project 1: COMP242: Flight Reservations");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        launch();
    }

    //Roots
    public AnchorPane setUpRoot(){
        AnchorPane root = new AnchorPane();
        root.setStyle("-fx-background-color: " + beigeBackgroundColorS);

        HBox topMenu = setUpTopMenu();
        passengerPane = getDatabasePaneFor(passengersList);
        flightPane = getDatabasePaneFor(flightsList);

        setAnchors(topMenu,0,0,0,-60);

        root.getChildren().addAll(topMenu,passengerPane,flightPane);

        return root;
    }

    public HBox setUpTopMenu(){
        HBox root = new HBox();
        root.setStyle("-fx-background-color: #985836");
        setUpButtons(Arrays.asList(topMenuBtn_DisplayFlightInfo,topMenuBtn_DisplayPassengerInfo,topMenuBtn_SaveExit), root);
        topMenuBtn_DisplayPassengerInfo.setOnAction(e -> {
            passengerPane.setVisible(true);
            flightPane.setVisible(false);
        });
        topMenuBtn_DisplayFlightInfo.setOnAction(e -> {
            flightPane.setVisible(true);
            passengerPane.setVisible(false);
        });
        topMenuBtn_SaveExit.setOnAction(e -> saveAndExit());
        return root;
    }

    //Passenger Methods
    private HBox setUpPassengersAdditionsBottom() {
        HBox root = new HBox();
        Stage reserveTicketStage = setUpReserveTicketStage();
        Stage cancelReservationStage = setUpCancelReservationStage();
        Stage checkTicketStage = setUpCheckTicketStage();
        Button cancelReservation = new Button("CANCEL RESERVATION");
        Button reserveTicket = new Button("RESERVE TICKET");
        Button checkTicket = new Button("CHECK TICKET RESERVATION");
        setUpButtons(Arrays.asList(reserveTicket,cancelReservation,checkTicket),root);
        reserveTicket.setOnAction(e -> reserveTicketStage.showAndWait());
        cancelReservation.setOnAction(e -> cancelReservationStage.showAndWait());
        checkTicket.setOnAction(e -> checkTicketStage.showAndWait());
        return root;
    }

    private AnchorPane setUpPassengersAdditionsTop(TextArea textArea) {
        AnchorPane root = new AnchorPane();
        Label label_searchForTicket = new Label("SEARCH TICKETS");
        TextField textField_searchForTicket = new TextField();
        Button showAll = new Button("SHOW ALL PASSENGERS");
        setUpSearchAndReset(label_searchForTicket,textField_searchForTicket,showAll);
        setUpButtonB(showAll);
        showAll.setOnAction(e -> textArea.setText(listToString(passengersList)));
        root.getChildren().addAll(label_searchForTicket,textField_searchForTicket,showAll);
        textField_searchForTicket.setOnAction(e -> {
            String name = textField_searchForTicket.getText();
            Passenger current = passengersList.getPassengerByName(name);
            if (current == null)
                showAlert("Error","Can't Find Passenger","Can't find a passenger with this specific name, try a different one.");
            else
                textArea.setText(current.toString());
        });
        showAll.setOnAction(e -> {
            textArea.setText(listToString(passengersList));
        });
        return root;
    }

    private Stage setUpCheckTicketStage(){
        VBox mainRoot = new VBox();
        VBox innerRoot = new VBox();
        innerRoot.setStyle("-fx-background-color: " + coffeeMediumColorS);
        mainRoot.setStyle("-fx-background-color: " + coffeeMediumColorS);
        innerRoot.setSpacing(5);
        mainRoot.setSpacing(10);
        mainRoot.setAlignment(Pos.CENTER);

        mainRoot.setStyle("-fx-background-color: " + coffeeMediumColorS);
        Label label_flightNumber = new Label("FLIGHT NUMBER");
        Label label_passengerName = new Label("PASSENGER NAME");
        TextField textField_flightNumber = new TextField();
        TextField textField_passengerName = new TextField();
        Button checkTicket = new Button("CHECK TICKET RESERVATION");
        setUpInnerRoots(Arrays.asList(label_flightNumber,label_passengerName),Arrays.asList(textField_flightNumber,textField_passengerName));
        innerRoot.getChildren().addAll(label_flightNumber,textField_flightNumber,label_passengerName,textField_passengerName);
        mainRoot.getChildren().add(innerRoot);
        setUpButtons(Arrays.asList(checkTicket),mainRoot);
        checkTicket.setOnAction(e -> {
            int flightNum;
            String name = textField_passengerName.getText();
            try{
                flightNum = Integer.parseInt(textField_flightNumber.getText());
            }catch (NumberFormatException exception) {
                showAlert("Error", "Flight Numbers Can't Have Letters", "Flight numbers can't have letters, make sure you type only Integers.");
                return;
            }
            Flight currentFlight = flightsList.getFlight(flightNum);
            if (currentFlight == null){
                showAlert("Error","Can't Find Flight","Can't find a flight with this specific number, try a different one.");
                return;
            }
            Passenger currentPassenger = currentFlight.getPassengersList().getPassengerByName(name);
            if (currentPassenger == null) {
                showAlert("Ticket Is Not Reserved","This Ticket Is Not On This Flight","");
                return;
            }
            showAlert("Ticket Is Reserved","This Ticket Is On This Flight","");
        });
        Scene scene = new Scene(mainRoot,280,480);
        Stage stage = new Stage();
        stage.setScene(scene);
        return stage;
    }

    private Stage setUpCancelReservationStage() {
        VBox mainRoot = new VBox();
        VBox innerRoot = new VBox();
        innerRoot.setStyle("-fx-background-color: " + coffeeMediumColorS);
        mainRoot.setStyle("-fx-background-color: " + coffeeMediumColorS);
        innerRoot.setSpacing(5);
        mainRoot.setSpacing(10);
        mainRoot.setAlignment(Pos.CENTER);

        mainRoot.setStyle("-fx-background-color: " + coffeeMediumColorS);
        Label label_flightNumber = new Label("FLIGHT NUMBER");
        Label label_passengerName = new Label("PASSENGER NAME");
        TextField textField_flightNumber = new TextField();
        TextField textField_passengerName = new TextField();
        Button cancelReservation = new Button("CANCEL RESERVATION");

        setUpInnerRoots(Arrays.asList(label_flightNumber,label_passengerName),Arrays.asList(textField_flightNumber,textField_passengerName));

        innerRoot.getChildren().addAll(label_flightNumber,textField_flightNumber,label_passengerName,textField_passengerName);
        mainRoot.getChildren().add(innerRoot);
        setUpButtons(Arrays.asList(cancelReservation),mainRoot);
        cancelReservation.setOnAction(e -> {
            int flightNum;
            String name = textField_passengerName.getText();
            try{
                flightNum = Integer.parseInt(textField_flightNumber.getText());
            }catch (NumberFormatException exception) {
                showAlert("Error", "Flight Numbers Can't Have Letters", "Flight numbers can't have letters, make sure you type only Integers.");
                return;
            }
            Flight currentFlight = flightsList.getFlight(flightNum);
            if (currentFlight == null){
                showAlert("Error","Can't Find Flight","Can't find a flight with this specific number, try a different one.");
                return;
            }
            Passenger currentPassenger = currentFlight.getPassengersList().getPassengerByName(name);
            if (currentPassenger == null) {
                showAlert("Error","Can't Find Passenger","Can't find a passenger on this flight with this specific name, try a different one.");
                return;
            }
            int passengerKey = currentPassenger.getKey();
            currentPassenger.setFlightNumber(0);
            if (currentFlight.getPassengersList().removeByKey(passengerKey) && passengersList.removeByKey(passengerKey))
                showAlert("Success","Ticket Reservation Canceled","");
            else
                showAlert("Error","Can't Cancel Reservation","There seems to be a problem in canceling this reservation.");
        });

        Scene scene = new Scene(mainRoot,240,480);
        Stage stage = new Stage();
        stage.setScene(scene);
        return stage;

    }

    private void setUpInnerRoots(List<Label> labels, List<TextField> textFields){
        for (Label label: labels){
            label.setTextFill(beigeBackgroundColor);
            label.setFont(Font.font("Consolas", 18));
        }
        for (TextField textField: textFields){
            textField.setFont(Font.font("Consolas", 16));
            textField.setPrefWidth(240);
            textField.setStyle("-fx-control-inner-background: " + coffeeMediumColorS + "; -fx-highlight-fill: " + beigeBackgroundColorS + "; "
                    + "-fx-highlight-text-fill: " + coffeeMediumColorS + "; -fx-text-fill: " + beigeBackgroundColorS + "; "
                    + "-fx-border-color: " + coffeeMediumColorS + "; -fx-border-width: 1; ");
        }

    }

    private Stage setUpReserveTicketStage() {
        VBox mainRoot = new VBox();
        VBox innerRoot = new VBox();
        innerRoot.setStyle("-fx-background-color: " + coffeeMediumColorS);
        mainRoot.setStyle("-fx-background-color: " + coffeeMediumColorS);
        innerRoot.setSpacing(5);
        mainRoot.setSpacing(10);
        mainRoot.setAlignment(Pos.CENTER);

        Label label_FlightNumber = new Label("FLIGHT NUMBER");
        Label label_PassName = new Label("PASSENGER NAME");
        Label label_PassPort = new Label("PASSPORT NUMBER");
        Label label_Nationality = new Label("NATIONALITY");
        Label label_Birthday = new Label("BIRTHDAY");
        TextField textField_FlightNumber = new TextField();
        TextField textField_PassName = new TextField();
        TextField textField_PassPort = new TextField();
        TextField textField_Nationality = new TextField();
        TextField textField_Birthday = new TextField();

        setUpInnerRoots(Arrays.asList(label_Birthday,label_Nationality,label_FlightNumber,label_PassName,label_PassPort),
                Arrays.asList(textField_Birthday,textField_Nationality,textField_Nationality,textField_PassName,textField_PassPort,textField_FlightNumber));
        innerRoot.getChildren().addAll(label_FlightNumber,textField_FlightNumber,
                label_PassName,textField_PassName,label_PassPort,textField_PassPort,label_Nationality,textField_Nationality,
                label_Birthday,textField_Birthday);

        Button reserveTicket = new Button("RESERVE");
        reserveTicket.setOnAction(e -> {
            int flightNum, ticketNum;
            try{
                flightNum = Integer.parseInt(textField_FlightNumber.getText());
            }catch (NumberFormatException exception) {
                showAlert("Error", "Flight Numbers Can't Have Letters", "Flight numbers can't have letters, make sure you type only Integers.");
                return;
            }
            Flight current = flightsList.getFlight(flightNum);
            if (current == null){
                showAlert("Error","Can't Find Flight","Can't find a flight with this specific number, try a different one.");
                return;
            }
            int max = current.getPassengersList().maxKeyInList();
            ticketNum = max + 1;
            String name = textField_PassName.getText();
            String passport = textField_PassPort.getText();
            String birthday = textField_Birthday.getText();
            String nationality = textField_Nationality.getText();
            passengersList.insertPassenger(flightNum,ticketNum,name,passport,nationality,birthday);
            current.getPassengersList().insertPassenger(flightNum,ticketNum,name,passport,nationality,birthday);
            showAlert("Success","Passenger Added To Flight","Ticket Number:" + ticketNum);
        });
        mainRoot.getChildren().add(innerRoot);
        setUpButtons(Arrays.asList(reserveTicket),mainRoot);

        Scene scene = new Scene(mainRoot,240,480);
        Stage stage = new Stage();
        stage.setScene(scene);
        return stage;
    }

    //Flights Methods
    private AnchorPane setupFlightsPaneAdditionsTop(TextArea textArea) {
        AnchorPane root = new AnchorPane();
        Label lbl1 = new Label("SEARCH FLIGHTS");
        TextField searchField = new TextField();
        Button resetTextArea = new Button("SHOW ALL FLIGHTS");
        setUpButtonB(resetTextArea);
        setUpSearchAndReset(lbl1, searchField, resetTextArea);
        resetTextArea.setOnAction(e -> textArea.setText(listToString(flightsList)));


        searchField.setOnAction(e -> {
            try{
                int key = Integer.parseInt(searchField.getText());
                Flight current = flightsList.getFlight(key);
                if (current != null)
                    textArea.setText(current + "\nPassengers:\n" + listToString(current.getPassengersList()));
                else
                    showAlert("Error","Can't Find Flight","Can't find a flight with this specific number, try a different one.");
            }catch (NumberFormatException exception){
                showAlert("Error","Flight Numbers Can't Have Letters","Flight numbers can't have letters, make sure you search for an integer.");
            }
        });

        root.getChildren().addAll(lbl1,searchField,resetTextArea);
        return root;
    }

    private HBox setupFlightsPaneAdditionsBottom(){
        HBox root = new HBox();
        Button btn_edit = new Button("EDIT FLIGHT");
        Button btn_add = new Button("ADD FLIGHT");
        setUpButtons(Arrays.asList(btn_add,btn_edit), root);
        Stage editFlight = setUpEditFlightStage();
        Stage addFlight = setUpAddFlightStage();
        btn_add.setOnAction(e -> addFlight.showAndWait());
        btn_edit.setOnAction(e -> editFlight.showAndWait());


        return root;
    }

    private Stage setUpAddFlightStage() {
        VBox mainRoot = new VBox();
        mainRoot.setAlignment(Pos.CENTER);
        mainRoot.setSpacing(30);
        mainRoot.setStyle("-fx-background-color: " + coffeeMediumColorS);
        VBox innerRoot = new VBox();
        innerRoot.setStyle("-fx-background-color: " + coffeeMediumColorS);
        Label label_flightNum = new Label("FLIGHT NUMBER");
        Label label_airline = new Label("AIRLINE");
        Label label_source = new Label("SOURCE");
        Label label_destin = new Label("DESTINATION");
        Label label_cap = new Label("CAPACITY");
        TextField textField_FlightNum = new TextField();
        TextField textField_Airline = new TextField();
        TextField textField_Source = new TextField();
        TextField textField_Destination = new TextField();
        TextField textField_Capacity = new TextField();
        for (Label label: Arrays.asList(label_cap,label_airline,label_destin,label_source,label_flightNum)){
            label.setTextFill(beigeBackgroundColor);
            label.setFont(Font.font("Consolas", 18));
        }
        for (TextField textField: Arrays.asList(textField_FlightNum,textField_Airline,textField_Capacity,textField_Destination,textField_Source)){
            textField.setFont(Font.font("Consolas", 16));
            textField.setPrefWidth(240);
            textField.setStyle("-fx-control-inner-background: " + coffeeMediumColorS + "; -fx-highlight-fill: " + beigeBackgroundColorS + "; "
                    + "-fx-highlight-text-fill: " + coffeeMediumColorS + "; -fx-text-fill: " + beigeBackgroundColorS + "; "
                    + "-fx-border-color: " + coffeeMediumColorS + "; -fx-border-width: 1; ");
        }
        innerRoot.getChildren().addAll(label_flightNum,textField_FlightNum,label_airline,textField_Airline
                ,label_source,textField_Source,label_destin,textField_Destination,label_cap,textField_Capacity);
        innerRoot.setAlignment(Pos.BASELINE_LEFT);
        innerRoot.setSpacing(5);

        mainRoot.getChildren().add(innerRoot);
        Button addFlight = new Button("ADD FLIGHT");
        setUpButtons(Arrays.asList(addFlight),mainRoot);
        addFlight.setOnAction(e -> {
            int flightNum = 0;
            int cap = 0;
            try{
                flightNum = Integer.parseInt(textField_FlightNum.getText());
                if (flightsList.flightExists(flightNum)){
                    showAlert("Error","Flight Already Exists","You can't create a flight with this specific number, try a different one.");
                    return;
                }
            }catch (NumberFormatException exception){
                showAlert("Error","Flight Numbers Can't Have Letters","Flight numbers can't have letters, make sure you type only Integers.");
                return;
            }
            try{
                cap = Integer.parseInt(textField_Capacity.getText());
            }catch (NumberFormatException exception){
                showAlert("Error","Capacity Can't Have Letters","Capacity can't have letters, make sure you type only Integers.");
                return;
            }
            flightsList.insertFlight(flightNum,textField_Airline.getText(),textField_Source.getText(),textField_Destination.getText(),cap);
            showAlert("Success","Flight Added To Flight List","");
        });
        Scene scene = new Scene(mainRoot,260,420);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Adding Flights");
        stage.setScene(scene);
        return stage;
    }

    private Stage setUpEditFlightStage() {
        VBox mainRoot = new VBox();
        mainRoot.setAlignment(Pos.CENTER);
        mainRoot.setSpacing(30);
        mainRoot.setStyle("-fx-background-color: " + coffeeMediumColorS);
        VBox innerRoot = new VBox();
        innerRoot.setStyle("-fx-background-color: " + coffeeMediumColorS);
        Label label_flightNum = new Label("FLIGHT NUMBER");
        Label label_airline = new Label("AIRLINE");
        Label label_source = new Label("SOURCE");
        Label label_destin = new Label("DESTINATION");
        Label label_cap = new Label("CAPACITY");
        TextField textField_FlightNum = new TextField();
        TextField textField_Airline = new TextField();
        TextField textField_Source = new TextField();
        TextField textField_Destination = new TextField();
        TextField textField_Capacity = new TextField();
        for (Label label: Arrays.asList(label_cap,label_airline,label_destin,label_source,label_flightNum)){
            label.setTextFill(beigeBackgroundColor);
            label.setFont(Font.font("Consolas", 18));
        }
        for (TextField textField: Arrays.asList(textField_FlightNum,textField_Airline,textField_Capacity,textField_Destination,textField_Source)){
            textField.setFont(Font.font("Consolas", 16));
            textField.setPrefWidth(240);
            textField.setStyle("-fx-control-inner-background: " + coffeeMediumColorS + "; -fx-highlight-fill: " + beigeBackgroundColorS + "; "
                    + "-fx-highlight-text-fill: " + coffeeMediumColorS + "; -fx-text-fill: " + beigeBackgroundColorS + "; "
                    + "-fx-border-color: " + coffeeMediumColorS + "; -fx-border-width: 1; ");
        }
        innerRoot.getChildren().addAll(label_flightNum,textField_FlightNum,label_airline,textField_Airline
                ,label_source,textField_Source,label_destin,textField_Destination,label_cap,textField_Capacity);
        innerRoot.setAlignment(Pos.BASELINE_LEFT);
        innerRoot.setSpacing(5);

        mainRoot.getChildren().add(innerRoot);
        Button searchFlight = new Button("SEARCH FLIGHT");
        Button editFlight = new Button("EDIT FLIGHT");

        setUpButtons(Arrays.asList(editFlight,searchFlight),mainRoot);
        searchFlight.setOnAction(e -> {
            int flightNumber;
            try{
                flightNumber = Integer.parseInt(textField_FlightNum.getText());
                if (flightsList.flightExists(flightNumber)){
                    Flight current = flightsList.getFlight(flightNumber);
                    textField_Airline.setText(current.getAirline());
                    textField_Capacity.setText(String.valueOf(current.getCapacity()));
                    textField_Destination.setText(current.getDestination());
                    textField_Source.setText(current.getSource());
                    textField_FlightNum.setEditable(false);
                }else {
                    showAlert("Error","Can't Find Flight","Can't find a flight with this specific number, try a different one.");
                }
            }catch (NumberFormatException exception){
                showAlert("Error","Flight Numbers Can't Have Letters","Flight numbers can't have letters, make sure you type only Integers.");
            }
        });
        editFlight.setOnAction(e -> {
            int flightNumber, cap;
            Flight current = null;
            try{
                flightNumber = Integer.parseInt(textField_FlightNum.getText());
                if (flightsList.flightExists(flightNumber)){
                    current = flightsList.getFlight(flightNumber);
                }else {
                    showAlert("Error","Can't Find Flight","Can't find a flight with this specific number, try a different one.");
                    return;
                }
            }catch (NumberFormatException exception){
                showAlert("Error","Flight Numbers Can't Have Letters","Flight numbers can't have letters, make sure you type only Integers.");
            }
            try{
                cap = Integer.parseInt(textField_Capacity.getText());
            }catch (NumberFormatException exception){
                showAlert("Error","Capacity Can't Have Letters","Capacity can't have letters, make sure you type only Integers.");
                return;
            }
            if (current == null)
                return;

            current.setAirline(textField_Airline.getText());
            current.setDestination(textField_Destination.getText());
            current.setSource(textField_Source.getText());
            current.setCapacity(cap);
        });

        Scene scene = new Scene(mainRoot,260,505);
        Stage stage = new Stage();
        stage.setTitle("Editing Flights");
        stage.setScene(scene);
        return stage;
    }

    //General Methods
    private void saveAndExit() {
        try {
            printToFile(flightsFile, flightsList);
        } catch (FileNotFoundException e) {
            showAlert("Error","Can't Save To Flights File","Can't save, try again later.");
        }
        try {
            printToFile(passengerFile, passengersList);
            System.exit(0);
        } catch (FileNotFoundException e) {
            showAlert("Error","Can't Save To Passengers File","Can't save, try again later.");
        }
    }

    private void printToFile(File file, SLL list) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(file);
        Node current = list.getHead();
        while (current.getNext() != null){
            printWriter.println(current.toData());
            current = current.getNext();
        }
        printWriter.println(current.toData());
        printWriter.close();
    }

    private void setUpButtonB(Button button) {
        button.setStyle("-fx-background-color: " + coffeeDarkColorS);
        button.setTextFill(beigeBackgroundColor);
        button.setFont(Font.font("", FontWeight.BOLD,16));
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: " + beigeBackgroundColorS + " ; -fx-cursor: hand;");
            button.setTextFill(coffeeDarkColor);
        });
        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + coffeeDarkColorS);
            button.setTextFill(beigeBackgroundColor);
        });
        button.setPrefHeight(58);
    }

    private AnchorPane getDatabasePaneFor(SLL list) {
        AnchorPane root = new AnchorPane();
        AnchorPane topBar = new AnchorPane();
        HBox bottomBar = new HBox();
        TextArea textArea = new TextArea();

        if (list == flightsList) {
            topBar = setupFlightsPaneAdditionsTop(textArea);
            bottomBar = setupFlightsPaneAdditionsBottom();
        }else{
            topBar = setUpPassengersAdditionsTop(textArea);
            bottomBar = setUpPassengersAdditionsBottom();
        }

        root.setStyle("-fx-background-color: " + beigeBackgroundColorS);
        topBar.setStyle("-fx-background-color: " + coffeeDarkColorS);
        bottomBar.setStyle("-fx-background-color: " + coffeeMediumColorS);
        textArea.setFont(Font.font("Consolas", FontWeight.NORMAL, FontPosture.REGULAR, 22));
        textArea.setEditable(false);
        textArea.setStyle("-fx-control-inner-background: " + beigeBackgroundColorS + "; -fx-highlight-fill: " + coffeeMediumColorS + "; "
                + "-fx-highlight-text-fill: " + beigeBackgroundColorS + "; -fx-text-fill: " + coffeeMediumColorS + "; "
                + "-fx-border-color: " + beigeBackgroundColorS + "; -fx-border-width: 1; ");
        setAnchors(textArea,0,0,60,80);
        setAnchors(root,0,0,60,0);
        AnchorPane.setTopAnchor(topBar,0.0);
        AnchorPane.setLeftAnchor(topBar,0.0);
        AnchorPane.setRightAnchor(topBar,0.0);
        AnchorPane.setLeftAnchor(bottomBar,0.0);
        AnchorPane.setRightAnchor(bottomBar,0.0);
        AnchorPane.setBottomAnchor(bottomBar,0.0);

        topBar.setPrefHeight(60);
        bottomBar.setPrefHeight(60);


        root.getChildren().addAll(textArea,topBar,bottomBar);
        textArea.setText(listToString(list));

        return root;
    }

    private void setUpButtons(List<Button> buttons, Pane root) {
        for (Button b: buttons) {
            root.getChildren().add(b);
            b.setPrefHeight(58);
            b.setStyle("-fx-background-color: " + coffeeMediumColorS);
            b.setTextFill(beigeBackgroundColor);
            b.setFont(Font.font("", FontWeight.BOLD,16));
            b.setOnMouseEntered(mouseEnteredButton);
            b.setOnMouseExited(mouseLeftButton);
        }
    }

    private void setUpSearchAndReset(Label label, TextField textField, Button button) {
        label.setTextFill(beigeBackgroundColor);
        label.setFont(Font.font("Consolas", FontWeight.NORMAL, FontPosture.REGULAR, 14));
        textField.setFont(Font.font("Consolas", FontWeight.NORMAL, FontPosture.REGULAR, 14));
        textField.setMinWidth(120);
        textField.setStyle("-fx-control-inner-background: " + coffeeMediumColorS + "; -fx-highlight-fill: " + beigeBackgroundColorS + "; "
                + "-fx-highlight-text-fill: " + coffeeMediumColorS + "; -fx-text-fill: " + beigeBackgroundColorS + "; "
                + "-fx-border-color: " + coffeeMediumColorS + "; -fx-border-width: 1; ");
        AnchorPane.setLeftAnchor(label,5.0);
        AnchorPane.setTopAnchor(label,2.5);
        AnchorPane.setLeftAnchor(textField,5.0);
        AnchorPane.setTopAnchor(textField,20.0);
        AnchorPane.setRightAnchor(button,0.0);
        AnchorPane.setTopAnchor(button,0.0);
    }

    private void setAnchors(javafx.scene.Node node, double left, double right, double top, double bottom){
        AnchorPane.setLeftAnchor(node,left);
        AnchorPane.setRightAnchor(node,right);
        AnchorPane.setTopAnchor(node,top);
        AnchorPane.setBottomAnchor(node,bottom);
    }

    public EventHandler<MouseEvent> mouseEnteredButton = event -> {
        try {
            Button current = (Button)(event.getSource());
            current.setStyle("-fx-background-color: " + beigeBackgroundColorS + "  ;-fx-cursor: hand;");
            current.setTextFill(coffeeMediumColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public EventHandler<MouseEvent> mouseLeftButton = event -> {
        try {
            Button current = (Button)(event.getSource());
            current.setStyle("-fx-background-color: " + coffeeMediumColorS);
            current.setTextFill(beigeBackgroundColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public static void printList(SLL list){
        Node current = list.getHead();
        while (current != null){
            System.out.println(current);
            current = current.getNext();
        }
        System.out.println("Length: " + list.getLength());
        System.out.println("---------------------------");
        System.out.println();
    }

    public String listToString(SLL list){
        StringBuilder result = new StringBuilder();
        Node current = list.getHead();
        while (current != null){
            result.append(current);
            result.append("\n");
            current = current.getNext();
        }
        return result.toString();
    }

    public void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

}