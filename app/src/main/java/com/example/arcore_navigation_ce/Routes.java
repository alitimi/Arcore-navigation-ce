package com.example.arcore_navigation_ce;

import java.util.List;

public class Routes {

    List<Legs> legs;
    Overview_Polyline overview_polyline;

    public Routes(List<Legs> legs, Overview_Polyline overview_polyline) {
        this.legs = legs;
        this.overview_polyline = overview_polyline;
    }

    public List<Legs> getLegs() {
        return legs;
    }

    public void setLegs(List<Legs> legs) {
        this.legs = legs;
    }

    public Overview_Polyline getOverview_polyline() {
        return overview_polyline;
    }

    public void setOverview_polyline(Overview_Polyline overview_polyline) {
        this.overview_polyline = overview_polyline;
    }
}
