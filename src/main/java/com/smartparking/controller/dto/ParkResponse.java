package com.smartparking.controller.dto;

import com.smartparking.model.SpotSize;
import com.smartparking.model.VehicleSize;

public class ParkResponse {

    private String licensePlate;
    private VehicleSize vehicleSize;
    private Long spotId;
    private SpotSize spotSize;
    private int distanceToExit;
    private String message;

    public ParkResponse() {
    }

    public ParkResponse(String licensePlate, VehicleSize vehicleSize, Long spotId,
                        SpotSize spotSize, int distanceToExit, String message) {
        this.licensePlate = licensePlate;
        this.vehicleSize = vehicleSize;
        this.spotId = spotId;
        this.spotSize = spotSize;
        this.distanceToExit = distanceToExit;
        this.message = message;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public VehicleSize getVehicleSize() {
        return vehicleSize;
    }

    public void setVehicleSize(VehicleSize vehicleSize) {
        this.vehicleSize = vehicleSize;
    }

    public Long getSpotId() {
        return spotId;
    }

    public void setSpotId(Long spotId) {
        this.spotId = spotId;
    }

    public SpotSize getSpotSize() {
        return spotSize;
    }

    public void setSpotSize(SpotSize spotSize) {
        this.spotSize = spotSize;
    }

    public int getDistanceToExit() {
        return distanceToExit;
    }

    public void setDistanceToExit(int distanceToExit) {
        this.distanceToExit = distanceToExit;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ParkResponse{"
                + "licensePlate='" + licensePlate + '\''
                + ", vehicleSize=" + vehicleSize
                + ", spotId=" + spotId
                + ", spotSize=" + spotSize
                + ", distanceToExit=" + distanceToExit
                + ", message='" + message + '\''
                + '}';
    }
}
