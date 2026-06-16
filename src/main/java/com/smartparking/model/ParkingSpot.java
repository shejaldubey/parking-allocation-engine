package com.smartparking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "parking_spots")
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpotSize size;

    @Column(nullable = false)
    private int distanceToExit;

    @Column(nullable = false)
    private boolean isOccupied;

    @Column
    private String currentLicensePlate;

    public ParkingSpot() {
    }

    public ParkingSpot(SpotSize size, int distanceToExit, boolean isOccupied, String currentLicensePlate) {
        this.size = size;
        this.distanceToExit = distanceToExit;
        this.isOccupied = isOccupied;
        this.currentLicensePlate = currentLicensePlate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SpotSize getSize() {
        return size;
    }

    public void setSize(SpotSize size) {
        this.size = size;
    }

    public int getDistanceToExit() {
        return distanceToExit;
    }

    public void setDistanceToExit(int distanceToExit) {
        this.distanceToExit = distanceToExit;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public String getCurrentLicensePlate() {
        return currentLicensePlate;
    }

    public void setCurrentLicensePlate(String currentLicensePlate) {
        this.currentLicensePlate = currentLicensePlate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParkingSpot that = (ParkingSpot) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ParkingSpot{"
                + "id=" + id
                + ", size=" + size
                + ", distanceToExit=" + distanceToExit
                + ", isOccupied=" + isOccupied
                + ", currentLicensePlate='" + currentLicensePlate + '\''
                + '}';
    }
}
