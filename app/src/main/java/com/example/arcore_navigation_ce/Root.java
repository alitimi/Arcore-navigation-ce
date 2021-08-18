package com.example.arcore_navigation_ce;

import java.util.List;

import okhttp3.Route;

public class Root{
    public List<Routes> routes;


    public Root(List<Routes> routes) {
        this.routes = routes;
    }

    public List<Routes> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Routes> routes) {
        this.routes = routes;
    }
}