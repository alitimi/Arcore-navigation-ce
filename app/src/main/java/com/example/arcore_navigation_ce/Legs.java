package com.example.arcore_navigation_ce;

import java.util.List;

public class Legs {

    String summary;
    Distance distance;
    Duration duration;
    List<Steps> steps;

    public Legs(String summary, Distance distance, Duration duration, List<Steps> steps) {
        this.summary = summary;
        this.distance = distance;
        this.duration = duration;
        this.steps = steps;
    }

    public List<Steps> getSteps() {
        return steps;
    }

    public void setSteps(List<Steps> steps) {
        this.steps = steps;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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


}
