package com.example.migros.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CourierLocationLog {

    private Long courierId;
    @JsonProperty("location")
    private GeoLocation location;
}
