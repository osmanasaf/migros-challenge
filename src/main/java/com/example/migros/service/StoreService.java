package com.example.migros.service;

import com.example.migros.constants.ErrorMessages;
import com.example.migros.exception.BadRequestException;
import com.example.migros.model.Store;
import com.example.migros.repository.StoreRepository;
import com.example.migros.util.LocationUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@CacheConfig(cacheNames = "stores")
public class StoreService {

    private static final double RADIUS = 100.0;
    private final StoreRepository storeRepository;

    @Autowired
    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @PostConstruct
    public void initializeStores() {
        if (storeRepository.count() == 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            ClassPathResource storeResource = new ClassPathResource("store.json");
            try {
                Store[] stores = objectMapper.readValue(storeResource.getInputStream(), Store[].class);
                storeRepository.saveAll(Arrays.asList(stores));
            } catch (Exception e) {
                throw new BadRequestException(String.format(ErrorMessages.STORE_INITIALIZATION_FAILED, e.getMessage()));
            }
        }
    }

    @Cacheable
    public List<Store> getAllStores() {
        List<Store> stores = storeRepository.findAll();
        if(stores.isEmpty()) {
            throw new BadRequestException(ErrorMessages.NO_STORES_FOUND);
        }
        return stores;
    }

    public List<Store> getNearbyStores(Double courierLatitude, Double courierLongitude) {
        List<Store> allStores = getAllStores();
        List<Store> nearbyStores = new ArrayList<>();
        for (Store store : allStores) {
            boolean isInProximity = LocationUtil.isWithinProximity(courierLatitude, courierLongitude, store.getLocation().getLat(), store.getLocation().getLng(), RADIUS);
            if (isInProximity) {
                nearbyStores.add(store);
            }
        }
        return nearbyStores;
    }
}
