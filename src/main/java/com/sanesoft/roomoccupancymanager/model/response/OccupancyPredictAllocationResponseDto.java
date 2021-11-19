package com.sanesoft.roomoccupancymanager.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sanesoft.roomoccupancymanager.model.serializer.RevenueSerializer;

import java.math.BigDecimal;
import java.util.List;

@JsonSerialize
public record OccupancyPredictAllocationResponseDto(List<UsedRoomsResponseDto> usedRoomTypes,
                                                    @JsonSerialize(using = RevenueSerializer.class) BigDecimal totalRevenue) {
}
