package com.sanesoft.roomoccupancymanager.service.occupancy;

import com.sanesoft.roomoccupancymanager.model.request.OccupancyPredictAllocationDto;
import com.sanesoft.roomoccupancymanager.model.response.OccupancyPredictAllocationResponseDto;
import com.sanesoft.roomoccupancymanager.model.request.RoomTypeDto;
import com.sanesoft.roomoccupancymanager.model.response.UsedRoomsResponseDto;
import com.sanesoft.roomoccupancymanager.service.guest.RoomGuestService;
import com.sanesoft.roomoccupancymanager.service.guest.model.RoomGuest;
import com.sanesoft.roomoccupancymanager.service.occupancy.priority.PriorityStrategy;
import com.sanesoft.roomoccupancymanager.service.occupancy.validator.DataValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PriorityBasedOccupancyService implements OccupancyService {

    private final DataValidator dataValidator;
    private final RoomGuestService roomGuestService;
    private final PriorityStrategy priorityStrategy;

    @Override
    public OccupancyPredictAllocationResponseDto predictAllocation(@NonNull OccupancyPredictAllocationDto request) {
        dataValidator.validate(request);

        var preferredGuestsMap = priorityStrategy.findPreferredGuests(
                roomGuestService.getAvailableRoomGuests(), request.availableRooms());


        List<UsedRoomsResponseDto> usedRoomsResponse = new ArrayList<>();
        preferredGuestsMap.forEach((roomType, roomGuestList) -> {
            usedRoomsResponse.add(createUsedRoomsResponse(roomGuestList, roomType));
        });
        return new OccupancyPredictAllocationResponseDto(
                usedRoomsResponse,
                getTotalRevenue(preferredGuestsMap)
        );
    }

    private UsedRoomsResponseDto createUsedRoomsResponse(List<RoomGuest> roomGuestList, RoomTypeDto roomType) {
        return new UsedRoomsResponseDto(
                roomType,
                roomGuestList.size(),
                getRevenue(roomGuestList)
        );
    }

    private BigDecimal getRevenue(List<RoomGuest> roomGuestList) {
        return roomGuestList
                .stream()
                .map(RoomGuest::amountWillingToPay)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getTotalRevenue(Map<RoomTypeDto, List<RoomGuest>> map) {
        return map.entrySet()
                .stream()
                .flatMap(entry -> entry.getValue().stream())
                .map(RoomGuest::amountWillingToPay)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
