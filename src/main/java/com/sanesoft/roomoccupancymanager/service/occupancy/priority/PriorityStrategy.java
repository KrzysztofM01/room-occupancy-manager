package com.sanesoft.roomoccupancymanager.service.occupancy.priority;

import com.sanesoft.roomoccupancymanager.model.request.RoomTypeDto;
import com.sanesoft.roomoccupancymanager.service.guest.model.RoomGuest;

import java.util.List;
import java.util.Map;

public interface PriorityStrategy {

    Map<RoomTypeDto, List<RoomGuest>> findPreferredGuests(List<RoomGuest> availableRoomGuests,
                                                          Map<RoomTypeDto, Integer> availableRoomsCount);

    PriorityStrategyType getPriorityStrategyType();
}
