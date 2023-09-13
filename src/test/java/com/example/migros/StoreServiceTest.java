package com.example.migros;

import com.example.migros.exception.BadRequestException;
import com.example.migros.model.GeoLocation;
import com.example.migros.model.Store;
import com.example.migros.repository.StoreRepository;
import com.example.migros.service.StoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StoreServiceTest {
    private StoreRepository storeRepository;
    private StoreService storeService;

    @BeforeEach
    public void setUp() {
        storeRepository = mock(StoreRepository.class);
        storeService = new StoreService(storeRepository);
    }

    @Test
    void initializeStores_withEmptyRepo_initializesStores() throws Exception {
        when(storeRepository.count()).thenReturn(0L);

        ObjectMapper mapper = new ObjectMapper();
        Resource resource = new ClassPathResource("store.json");
        Store[] stores = mapper.readValue(resource.getInputStream(), Store[].class);

        storeService.initializeStores();

        Mockito.verify(storeRepository).saveAll(Mockito.anyList());
    }

    @Test
    void initializeStores_withExistingStores_doesNotInitialize() {
        when(storeRepository.count()).thenReturn(1L);

        storeService.initializeStores();

        verify(storeRepository, never()).saveAll(any());
    }

    @Test
    void initializeStores_withError_throwsBadRequestException() {
        when(storeRepository.count()).thenReturn(0L);

        doThrow(new RuntimeException()).when(storeRepository).saveAll(any());

        assertThrows(BadRequestException.class, () -> storeService.initializeStores());
    }

    @Test
    void getAllStores_whenStoresExist_returnsStores() {
        List<Store> stores = new ArrayList<>();
        stores.add(new Store());

        when(storeRepository.findAll()).thenReturn(stores);

        List<Store> resultStores = storeService.getAllStores();

        assertEquals(stores, resultStores);
    }

    @Test
    void getAllStores_whenNoStores_throwsBadRequestException() {
        when(storeRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(BadRequestException.class, () -> storeService.getAllStores());
    }

    @Test
    void getNearbyStores_whenStoresNearby_returnsStores() {
        List<Store> allStores = new ArrayList<>();
        Store store = new Store();
        store.setLocation(new GeoLocation(40.0, 29.1));
        allStores.add(store);

        when(storeRepository.findAll()).thenReturn(allStores);

        List<Store> resultStores = storeService.getNearbyStores(40.00000002, 29.1);

        assertFalse(resultStores.isEmpty());
        assertEquals(store, resultStores.get(0));
    }

    @Test
    void getNearbyStores_whenNoStoresNearby_throwsBadRequestException() {
        List<Store> allStores = new ArrayList<>();
        Store store = new Store();
        store.setLocation(new GeoLocation(40.0, 29.0));
        allStores.add(store);

        when(storeRepository.findAll()).thenReturn(allStores);

        assertThrows(BadRequestException.class, () -> storeService.getNearbyStores(41.0, 30.0));
    }
}
