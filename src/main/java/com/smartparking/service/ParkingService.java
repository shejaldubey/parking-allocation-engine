package com.smartparking.service;

import com.smartparking.controller.dto.LeaveResponse;
import com.smartparking.controller.dto.ParkResponse;
import com.smartparking.model.ParkingSpot;
import com.smartparking.model.SpotSize;
import com.smartparking.model.Vehicle;
import com.smartparking.model.VehicleSize;
import com.smartparking.repository.ParkingSpotRepository;
import com.smartparking.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

@Service
public class ParkingService {

    private static final double HOURLY_RATE = 2.0;
    private static final double MINIMUM_FEE = 2.0;

    private final ParkingSpotRepository parkingSpotRepository;
    private final VehicleRepository vehicleRepository;

    private final PriorityQueue<ParkingSpot> smallSpots = new PriorityQueue<>(
            Comparator.comparingInt(ParkingSpot::getDistanceToExit));
    private final PriorityQueue<ParkingSpot> mediumSpots = new PriorityQueue<>(
            Comparator.comparingInt(ParkingSpot::getDistanceToExit));
    private final PriorityQueue<ParkingSpot> largeSpots = new PriorityQueue<>(
            Comparator.comparingInt(ParkingSpot::getDistanceToExit));

    private final Map<String, ParkingSpot> licensePlateToSpot = new HashMap<>();

    public ParkingService(ParkingSpotRepository parkingSpotRepository,
                          VehicleRepository vehicleRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public synchronized void initializeQueues(List<ParkingSpot> spots) {
        smallSpots.clear();
        mediumSpots.clear();
        largeSpots.clear();
        licensePlateToSpot.clear();

        for (ParkingSpot spot : spots) {
            if (!spot.isOccupied()) {
                addSpotToQueue(spot);
            } else if (spot.getCurrentLicensePlate() != null) {
                licensePlateToSpot.put(spot.getCurrentLicensePlate(), spot);
            }
        }
    }

    @Transactional
    public synchronized ParkResponse parkVehicle(String licensePlate, VehicleSize vehicleSize) {
        if (licensePlateToSpot.containsKey(licensePlate)) {
            throw new IllegalStateException("Vehicle with license plate " + licensePlate + " is already parked");
        }
        if (vehicleRepository.existsById(licensePlate)) {
            throw new IllegalStateException("Vehicle with license plate " + licensePlate + " is already parked");
        }

        ParkingSpot assignedSpot = findClosestAvailableSpot(vehicleSize);
        if (assignedSpot == null) {
            throw new IllegalStateException("No available parking spot for vehicle size " + vehicleSize);
        }

        removeSpotFromQueue(assignedSpot);

        assignedSpot.setOccupied(true);
        assignedSpot.setCurrentLicensePlate(licensePlate);
        parkingSpotRepository.save(assignedSpot);

        Vehicle vehicle = new Vehicle(licensePlate, vehicleSize, LocalDateTime.now());
        vehicleRepository.save(vehicle);

        licensePlateToSpot.put(licensePlate, assignedSpot);

        return new ParkResponse(
                licensePlate,
                vehicleSize,
                assignedSpot.getId(),
                assignedSpot.getSize(),
                assignedSpot.getDistanceToExit(),
                "Vehicle parked successfully in spot " + assignedSpot.getId()
        );
    }

    @Transactional
    public synchronized LeaveResponse leaveVehicle(String licensePlate) {
        ParkingSpot spot = licensePlateToSpot.get(licensePlate);
        if (spot == null) {
            throw new IllegalStateException("Vehicle with license plate " + licensePlate + " is not parked");
        }

        Vehicle vehicle = vehicleRepository.findById(licensePlate)
                .orElseThrow(() -> new IllegalStateException(
                        "Vehicle with license plate " + licensePlate + " is not parked"));

        double fee = calculateFee(vehicle.getEntryTime(), LocalDateTime.now());

        vehicleRepository.delete(vehicle);

        spot.setOccupied(false);
        spot.setCurrentLicensePlate(null);
        parkingSpotRepository.save(spot);

        licensePlateToSpot.remove(licensePlate);
        addSpotToQueue(spot);

        return new LeaveResponse(
                licensePlate,
                spot.getId(),
                fee,
                "Vehicle left spot " + spot.getId() + ". Fee charged: $" + String.format("%.2f", fee)
        );
    }

    public List<ParkingSpot> getAllSpots() {
        return parkingSpotRepository.findAll();
    }

    private ParkingSpot findClosestAvailableSpot(VehicleSize vehicleSize) {
        ParkingSpot bestSpot = null;

        if (vehicleSize == VehicleSize.SMALL) {
            bestSpot = selectClosest(bestSpot, smallSpots.peek());
            bestSpot = selectClosest(bestSpot, mediumSpots.peek());
            bestSpot = selectClosest(bestSpot, largeSpots.peek());
        } else if (vehicleSize == VehicleSize.MEDIUM) {
            bestSpot = selectClosest(bestSpot, mediumSpots.peek());
            bestSpot = selectClosest(bestSpot, largeSpots.peek());
        } else if (vehicleSize == VehicleSize.LARGE) {
            bestSpot = largeSpots.peek();
        }

        return bestSpot;
    }

    private ParkingSpot selectClosest(ParkingSpot currentBest, ParkingSpot candidate) {
        if (candidate == null) {
            return currentBest;
        }
        if (currentBest == null) {
            return candidate;
        }
        if (candidate.getDistanceToExit() < currentBest.getDistanceToExit()) {
            return candidate;
        }
        return currentBest;
    }

    private void removeSpotFromQueue(ParkingSpot spot) {
        if (spot.getSize() == SpotSize.SMALL) {
            smallSpots.remove(spot);
        } else if (spot.getSize() == SpotSize.MEDIUM) {
            mediumSpots.remove(spot);
        } else if (spot.getSize() == SpotSize.LARGE) {
            largeSpots.remove(spot);
        }
    }

    private void addSpotToQueue(ParkingSpot spot) {
        if (spot.getSize() == SpotSize.SMALL) {
            smallSpots.offer(spot);
        } else if (spot.getSize() == SpotSize.MEDIUM) {
            mediumSpots.offer(spot);
        } else if (spot.getSize() == SpotSize.LARGE) {
            largeSpots.offer(spot);
        }
    }

    private double calculateFee(LocalDateTime entryTime, LocalDateTime exitTime) {
        long totalMinutes = Duration.between(entryTime, exitTime).toMinutes();
        if (totalMinutes < 0) {
            totalMinutes = 0;
        }
        long billableHours = (totalMinutes + 59) / 60;
        if (billableHours < 1) {
            billableHours = 1;
        }
        double fee = billableHours * HOURLY_RATE;
        if (fee < MINIMUM_FEE) {
            fee = MINIMUM_FEE;
        }
        return fee;
    }
}
