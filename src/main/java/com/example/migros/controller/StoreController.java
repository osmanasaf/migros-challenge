package com.example.migros.controller;

import com.example.migros.model.Store;
import com.example.migros.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/api/v1/stores")
public class StoreController {

    private final StoreService storeService;

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/all")
    public List<Store> getAllStores() {
        return storeService.getAllStores();
    }

    @GetMapping("/nearby")
    public List<Store> getNearbyStores(@RequestParam Double latitude, @RequestParam Double longitude) {
        return storeService.getNearbyStores(latitude, longitude);
    }
}
