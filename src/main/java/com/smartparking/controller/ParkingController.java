package com.smartparking.controller;

import com.smartparking.controller.dto.LeaveRequest;
import com.smartparking.controller.dto.LeaveResponse;
import com.smartparking.controller.dto.ParkRequest;
import com.smartparking.controller.dto.ParkResponse;
import com.smartparking.model.ParkingSpot;
import com.smartparking.service.ParkingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @PostMapping("/park")
    public ResponseEntity<ParkResponse> park(@Valid @RequestBody ParkRequest request) {
        ParkResponse response = parkingService.parkVehicle(
                request.getLicensePlate().trim().toUpperCase(),
                request.getSize()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/leave")
    public ResponseEntity<LeaveResponse> leave(@Valid @RequestBody LeaveRequest request) {
        LeaveResponse response = parkingService.leaveVehicle(
                request.getLicensePlate().trim().toUpperCase()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/spots")
    public ResponseEntity<List<ParkingSpot>> getAllSpots() {
        return ResponseEntity.ok(parkingService.getAllSpots());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
