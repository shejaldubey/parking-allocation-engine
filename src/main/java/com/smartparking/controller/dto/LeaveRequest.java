package com.smartparking.controller.dto;

import jakarta.validation.constraints.NotBlank;

public class LeaveRequest {

    @NotBlank(message = "License plate is required")
    private String licensePlate;

    public LeaveRequest() {
    }

    public LeaveRequest(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    @Override
    public String toString() {
        return "LeaveRequest{"
                + "licensePlate='" + licensePlate + '\''
                + '}';
    }
}
