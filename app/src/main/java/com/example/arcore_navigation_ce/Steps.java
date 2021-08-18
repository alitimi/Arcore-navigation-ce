package com.example.arcore_navigation_ce;

public class Steps {

    String name;
    String instruction;
    Distance distance;
    Duration duration;
    String polyline;
    String maneuver;

    public Steps(String name, String instruction, Distance distance, Duration duration, String polyline, String maneuver) {
        this.name = name;
        this.instruction = instruction;
        this.distance = distance;
        this.duration = duration;
        this.polyline = polyline;
        this.maneuver = maneuver;
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
