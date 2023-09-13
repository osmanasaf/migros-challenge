package com.example.migros.controller;

import com.example.migros.model.Courier;
import com.example.migros.model.GeoLocation;
import com.example.migros.service.CourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courier")
public class CourierController {

    private final CourierService courierService;

    @Autowired
    public CourierController(CourierService courierService) {
        this.courierService = courierService;
    }

    @PostMapping
    public Courier addCourier(@RequestBody Courier courier) {
        return courierService.addCourier(courier);
    }

    @PutMapping("/{courierId}/location")
    public Courier updateCourierLocation(@PathVariable Long courierId, @RequestBody GeoLocation location) {
        return courierService.updateCourierLocation(courierId, location);
    }

    @GetMapping("/{courierId}")
    public Courier getCourierById(@PathVariable Long courierId) {
        return courierService.getCourierById(courierId);
    }

    @GetMapping("/{courierId}/distance")
    public double getTotalTravelDistance(@PathVariable Long courierId) {
        return courierService.getTotalTravelDistance(courierId);
    }
}
