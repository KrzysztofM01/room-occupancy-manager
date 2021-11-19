package com.sanesoft.roomoccupancymanager.service.guest;

import com.sanesoft.roomoccupancymanager.service.guest.model.RoomGuest;

import java.util.List;

/**
 * The task description was not clear whether list of room guests should be supplied within request or will it come
 * from some other place like database, cache or some external service. This is why this interface was implemented so
 * that in future it can easily implemented without changes in other classes.
 */
public interface RoomGuestService {

    List<RoomGuest> getAvailableRoomGuests();
}
