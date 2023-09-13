package com.example.migros.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class GeoLocation implements Serializable {

    @JsonProperty("lat")
    private double lat;

    @JsonProperty("lng")
    private double lng;

    public GeoLocation(double v, double v1) {
        this.lat = v;
        this.lng = v1;
    }

    public GeoLocation() {
    }
}