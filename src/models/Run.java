/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

public class Run {
    private int id;
    private String trainName;
    private String origin;
    private String destination;
    private String departureTime;
    private String arrivalTime;

    public Run(int id, String trainName, String origin, String destination, String departureTime, String arrivalTime) {
        this.id = id;
        this.trainName = trainName;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    // Getters
    public int getId() { return id; }
    public String getTrainName() { return trainName; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
}
