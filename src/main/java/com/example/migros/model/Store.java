package com.example.migros.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
public class Store extends BaseEntity implements Serializable {

    private String name;

    @Embedded
    @JsonProperty("location")
    private GeoLocation location;

}
