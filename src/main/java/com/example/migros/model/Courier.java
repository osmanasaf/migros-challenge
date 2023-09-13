package com.example.migros.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Courier extends BaseEntity {

    private String name;

    @Embedded
    private GeoLocation location;

    private Double totalTravelDistance = 0.0;

}