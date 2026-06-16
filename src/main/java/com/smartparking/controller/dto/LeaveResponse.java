package com.smartparking.controller.dto;

public class LeaveResponse {

    private String licensePlate;
    private Long spotId;
    private double fee;
    private String message;

    public LeaveResponse() {
    }

    public LeaveResponse(String licensePlate, Long spotId, double fee, String message) {
        this.licensePlate = licensePlate;
        this.spotId = spotId;
        this.fee = fee;
        this.message = message;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Long getSpotId() {
        return spotId;
    }

    public void setSpotId(Long spotId) {
        this.spotId = spotId;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "LeaveResponse{"
                + "licensePlate='" + licensePlate + '\''
                + ", spotId=" + spotId
                + ", fee=" + fee
                + ", message='" + message + '\''
                + '}';
    }
}
