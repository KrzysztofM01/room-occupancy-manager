package com.sanesoft.roomoccupancymanager.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public enum RoomTypeDto {
    PREMIUM, ECONOMY
}
