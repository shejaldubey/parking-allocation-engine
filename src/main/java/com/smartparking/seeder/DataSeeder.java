package com.smartparking.seeder;

import com.smartparking.model.ParkingSpot;
import com.smartparking.model.SpotSize;
import com.smartparking.repository.ParkingSpotRepository;
import com.smartparking.service.ParkingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final int FLOORS = 5;
    private static final int SPOTS_PER_FLOOR = 25;

    private final ParkingSpotRepository parkingSpotRepository;
    private final ParkingService parkingService;

    public DataSeeder(ParkingSpotRepository parkingSpotRepository, ParkingService parkingService) {
        this.parkingSpotRepository = parkingSpotRepository;
        this.parkingService = parkingService;
    }

    @Override
    public void run(String... args) {
        if (parkingSpotRepository.count() > 0) {
            parkingService.initializeQueues(parkingSpotRepository.findAll());
            return;
        }

        List<ParkingSpot> spots = new ArrayList<>();
        SpotSize[] sizes = SpotSize.values();

        for (int floor = 1; floor <= FLOORS; floor++) {
            for (int position = 1; position <= SPOTS_PER_FLOOR; position++) {
                SpotSize size = sizes[(floor + position) % sizes.length];
                int distanceToExit = (floor * 100) + position;
                ParkingSpot spot = new ParkingSpot(size, distanceToExit, false, null);
                spots.add(spot);
            }
        }

        List<ParkingSpot> savedSpots = parkingSpotRepository.saveAll(spots);
        parkingService.initializeQueues(savedSpots);
    }
}
