package com.example.migros.service;

import com.example.migros.constants.ErrorMessages;
import com.example.migros.exception.BadRequestException;
import com.example.migros.model.*;
import com.example.migros.repository.CourierRepository;
import com.example.migros.util.LocationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourierService {

    private static final String TOPIC_NAME = "courierLocations";

    private final CourierRepository courierRepo;
    private final StoreService storeService;
    private final KafkaProducerService kafkaProducerService;
    private final CourierStoreEntryService courierStoreEntryService;

    @Autowired
    public CourierService(CourierRepository courierRepo,
                          StoreService storeService,
                          KafkaProducerService kafkaProducerService,
                          CourierStoreEntryService courierStoreEntryService) {
        this.courierRepo = courierRepo;
        this.storeService = storeService;
        this.kafkaProducerService = kafkaProducerService;
        this.courierStoreEntryService = courierStoreEntryService;
    }

    public Courier addCourier(Courier courier) {
        checkCourierExistence(courier.getId());
        Courier savedCourier = courierRepo.save(courier);
        sendCourierLocationToKafka(savedCourier);
        return savedCourier;
    }

    private void checkCourierExistence(Long courierId) {
        courierRepo.findById(courierId).ifPresent(c -> {
            throw new BadRequestException(String.format(ErrorMessages.COURIER_ALREADY_EXISTS, c.getId()));
        });
    }

    private void sendCourierLocationToKafka(Courier courier) {
        CourierLocationLog courierLocationLog = convertCourierToCourierLocationLog(courier);
        kafkaProducerService.sendMessage(courierLocationLog);
    }

    public Courier updateCourierLocation(Long courierId, GeoLocation location) {
        Courier courier = getCourierById(courierId);
        updateCourierWithNewLocation(courier, location);
        sendCourierLocationToKafka(courier);
        return courierRepo.save(courier);
    }

    private void updateCourierWithNewLocation(Courier courier, GeoLocation location) {
        double distanceBetweenNewAndOldPoint = LocationUtil.calculateDistance(
                courier.getLocation().getLat(), courier.getLocation().getLng(),
                location.getLat(), location.getLng()
        );
        double distanceInKm = distanceBetweenNewAndOldPoint / 1000.0;
        courier.setTotalTravelDistance(courier.getTotalTravelDistance() + distanceInKm);

        courier.setLocation(location);
    }

    public Courier getCourierById(Long id) {
        return courierRepo.findById(id).orElseThrow(() -> new BadRequestException(String.format(ErrorMessages.COURIER_NOT_FOUND, id)));
    }


    public void checkLocationIsNearToStoreAndSave(CourierLocationLog courierLocationLog) {
        Long courierId = courierLocationLog.getCourierId();
        GeoLocation location = courierLocationLog.getLocation();
        List<Store> nearbyStores = storeService.getNearbyStores(location.getLat(), location.getLng());

        if (!nearbyStores.isEmpty()) {
            saveEntriesForNearbyStores(courierId, nearbyStores);
        }
    }

    private void saveEntriesForNearbyStores(Long courierId, List<Store> nearbyStores) {
        Courier courier = getCourierById(courierId);
        for (Store store : nearbyStores) {
            LocalDateTime lastEntryTime = courierStoreEntryService.getLastEntryTime(courierId, store.getId());
            LocalDateTime currentTime = LocalDateTime.now();

            if (lastEntryTime == null || Duration.between(lastEntryTime, currentTime).toMinutes() >= 1) {
                CourierStoreEntry entry = new CourierStoreEntry();
                entry.setCourier(courier);
                entry.setStore(store);
                entry.setEntryTime(currentTime);
                courierStoreEntryService.addCourierStoreEntry(entry);
            }
        }
    }


    public CourierLocationLog convertCourierToCourierLocationLog(Courier courier) {
        CourierLocationLog courierLocationLog = new CourierLocationLog();
        courierLocationLog.setCourierId(courier.getId());
        courierLocationLog.setLocation(courier.getLocation());
        return courierLocationLog;
    }

    public double getTotalTravelDistance(Long courierId) {
        return getCourierById(courierId).getTotalTravelDistance();
    }
}
