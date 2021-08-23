package com.example.arcore_navigation_ce;

import java.util.ArrayList;

public class Steps {

    String name;
    String instruction;
    Distance distance;
    Duration duration;
    String polyline;
    String maneuver;
    ArrayList<Double> start_location;

    public Steps(String name, String instruction, Distance distance, Duration duration, String polyline, String maneuver, ArrayList<Double> start_location) {
        this.name = name;
        this.instruction = instruction;
        this.distance = distance;
        this.duration = duration;
        this.polyline = polyline;
        this.maneuver = maneuver;
        this.start_location = start_location;
    }

    public ArrayList<Double> getStart_location() {
        return start_location;
    }

    public void setStart_location(ArrayList<Double> start_location) {
        this.start_location = start_location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getPolyline() {
        return polyline;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }

    public String getManeuver() {
        return maneuver;
    }

    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }
}
