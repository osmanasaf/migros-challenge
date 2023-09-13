package com.example.migros;

import com.example.migros.exception.BadRequestException;
import com.example.migros.model.Courier;
import com.example.migros.model.CourierLocationLog;
import com.example.migros.model.GeoLocation;
import com.example.migros.model.Store;
import com.example.migros.repository.CourierRepository;
import com.example.migros.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CourierServiceTest {

    @InjectMocks
    private CourierService courierService;

    @Mock
    private CourierRepository courierRepo;

    @Mock
    private StoreService storeService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private KafkaConsumerService kafkaConsumerService;

    @Mock
    private CourierStoreEntryService courierStoreEntryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testAddCourierWhenNotExist() {
        Courier courier = new Courier();
        courier.setId(1L);

        when(courierRepo.findById(1L)).thenReturn(Optional.empty());
        when(courierRepo.save(any(Courier.class))).thenReturn(courier);

        courierService.addCourier(courier);

        verify(courierRepo, times(1)).save(courier);
        verify(kafkaProducerService, times(1)).sendMessage(any());
    }

    @Test
    void testAddCourierWhenExist() {
        Courier courier = new Courier();
        courier.setId(1L);

        when(courierRepo.findById(1L)).thenReturn(Optional.of(courier));

        try {
            courierService.addCourier(courier);
        } catch (BadRequestException e) {

        }

        verify(courierRepo, never()).save(courier);
        verify(kafkaProducerService, never()).sendMessage(any());
    }

    @Test
    void testUpdateCourierLocation() {
        Long courierId = 1L;
        GeoLocation location = new GeoLocation();
        Courier courier = new Courier();
        courier.setId(courierId);
        courier.setLocation(new GeoLocation());

        when(courierRepo.findById(courierId)).thenReturn(Optional.of(courier));
        when(courierRepo.save(courier)).thenReturn(courier);

        courierService.updateCourierLocation(courierId, location);

        verify(courierRepo, times(1)).save(courier);
        verify(kafkaProducerService, times(1)).sendMessage(any());
    }
    @Test
    void testGetCourierByIdWhenExist() {
        Long courierId = 1L;
        Courier courier = new Courier();
        courier.setId(courierId);

        when(courierRepo.findById(courierId)).thenReturn(Optional.of(courier));

        Courier result = courierService.getCourierById(courierId);

        assertEquals(courierId, result.getId());
    }

    @Test
     void testGetCourierByIdWhenNotExist() {
        Long courierId = 1L;

        when(courierRepo.findById(courierId)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            courierService.getCourierById(courierId);
        });
    }

    @Test
    void testCheckLocationIsNearToStoreAndSaveWithNearbyStores() {
        Courier mockCourier = new Courier();
        mockCourier.setId(1L);
        mockCourier.setLocation(new GeoLocation());

        CourierLocationLog courierLocationLog = new CourierLocationLog();
        courierLocationLog.setLocation(new GeoLocation());
        courierLocationLog.setCourierId(1L);

        when(storeService.getNearbyStores(anyDouble(), anyDouble())).thenReturn(Arrays.asList(new Store()));
        when(courierRepo.findById(1L)).thenReturn(Optional.of(mockCourier));

        courierService.checkLocationIsNearToStoreAndSave(courierLocationLog);

        verify(courierStoreEntryService, times(1)).addCourierStoreEntry(any());
    }

    @Test
    void testCheckLocationIsNearToStoreAndSaveWithoutNearbyStores() {
        CourierLocationLog courierLocationLog = new CourierLocationLog();
        courierLocationLog.setLocation(new GeoLocation());
        courierLocationLog.setCourierId(1L);

        when(storeService.getNearbyStores(anyDouble(), anyDouble())).thenReturn(Collections.emptyList());

        assertThrows(BadRequestException.class, () -> {
            courierService.checkLocationIsNearToStoreAndSave(courierLocationLog);
        });
    }

    @Test
    void testConvertCourierToCourierLocationLog() {
        Courier courier = new Courier();
        courier.setId(1L);
        courier.setLocation(new GeoLocation());

        CourierLocationLog log = courierService.convertCourierToCourierLocationLog(courier);

        assertEquals(courier.getId(), log.getCourierId());
        assertEquals(courier.getLocation(), log.getLocation());
    }
}
