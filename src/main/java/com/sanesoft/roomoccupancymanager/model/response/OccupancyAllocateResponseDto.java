package com.sanesoft.roomoccupancymanager.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sanesoft.roomoccupancymanager.serializer.RevenueSerializer;

import java.math.BigDecimal;
import java.util.List;

@JsonSerialize
public record OccupancyAllocateResponseDto(List<UsedRoomsResponseDto> usedRooms, @JsonSerialize(using = RevenueSerializer.class) BigDecimal totalRevenue) {
}
