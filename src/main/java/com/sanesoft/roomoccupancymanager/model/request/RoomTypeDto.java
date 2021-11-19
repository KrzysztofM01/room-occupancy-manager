package com.sanesoft.roomoccupancymanager.model.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public enum RoomTypeDto {
    PREMIUM, ECONOMY
}
