package com.smartparking.controller.dto;

import com.smartparking.model.VehicleSize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ParkRequest {

    @NotBlank(message = "License plate is required")
    private String licensePlate;

    @NotNull(message = "Vehicle size is required")
    private VehicleSize size;

    public ParkRequest() {
    }

    public ParkRequest(String licensePlate, VehicleSize size) {
        this.licensePlate = licensePlate;
        this.size = size;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public VehicleSize getSize() {
        return size;
    }

    public void setSize(VehicleSize size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "ParkRequest{"
                + "licensePlate='" + licensePlate + '\''
                + ", size=" + size
                + '}';
    }
}
