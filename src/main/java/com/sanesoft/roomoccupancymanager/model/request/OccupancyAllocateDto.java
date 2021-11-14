package com.sanesoft.roomoccupancymanager.model.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public record OccupancyAllocateDto(int premiumRooms, int economyRooms) {
}