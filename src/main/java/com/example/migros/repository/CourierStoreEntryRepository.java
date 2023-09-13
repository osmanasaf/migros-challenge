package com.example.migros.repository;

import com.example.migros.model.CourierStoreEntry;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierStoreEntryRepository extends org.springframework.data.jpa.repository.JpaRepository<com.example.migros.model.CourierStoreEntry, java.lang.Long>{
    CourierStoreEntry findTopByCourierIdAndStoreIdOrderByEntryTimeDesc(Long courierId, Long storeId);


}
