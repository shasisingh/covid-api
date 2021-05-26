package com.api.covid.model;


import java.util.List;


public class Root {
    public List<Center> centers;

    public List<Center> getCenters() {
        return centers;
    }

    public void setCenters(List<Center> centers) {
        this.centers = centers;
    }
}
