package com.example.migros.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Data
public class CourierStoreEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Courier courier;
    
    @ManyToOne
    private Store store;

    @Column
    private LocalDateTime entryTime;
}

