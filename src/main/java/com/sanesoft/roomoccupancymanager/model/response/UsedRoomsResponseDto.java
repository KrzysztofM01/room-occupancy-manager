package com.sanesoft.roomoccupancymanager.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sanesoft.roomoccupancymanager.model.request.RoomTypeDto;
import com.sanesoft.roomoccupancymanager.model.serializer.RevenueSerializer;

import java.math.BigDecimal;

@JsonSerialize
public record UsedRoomsResponseDto(RoomTypeDto type, int count,
                                   @JsonSerialize(using = RevenueSerializer.class) BigDecimal revenue) {
}
