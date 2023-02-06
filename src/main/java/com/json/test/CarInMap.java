package com.json.test;

import java.util.Map;

public class CarInMap {
    private String model;
    private double engineVolume;
    private Map<String,Car> properties;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getEngineVolume() {
        return engineVolume;
    }

    public void setEngineVolume(double engineVolume) {
        this.engineVolume = engineVolume;
    }

    public Map<String, Car> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Car> properties) {
        this.properties = properties;
    }
}
