package com.sanesoft.roomoccupancymanager.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

@JsonDeserialize
public record OccupancyPredictAllocationDto(@JsonProperty(required = true) Map<RoomTypeDto, Integer> availableRooms) {
}
