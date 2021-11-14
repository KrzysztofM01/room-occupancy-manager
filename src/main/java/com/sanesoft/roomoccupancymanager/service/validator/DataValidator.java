package com.sanesoft.roomoccupancymanager.service.validator;

import com.sanesoft.roomoccupancymanager.exception.InvalidRequestException;
import com.sanesoft.roomoccupancymanager.model.request.OccupancyAllocateDto;
import org.springframework.stereotype.Component;

@Component
public class DataValidator {

    public void validate(OccupancyAllocateDto request) {
        if (request.economyRooms() == null) {
            throw new InvalidRequestException("No economy rooms count was provided.");
        }
        if (request.premiumRooms() == null) {
            throw new InvalidRequestException("No premium rooms count was provided.");
        }
        if (request.economyRooms() < 0 || request.premiumRooms() < 0) {
            throw new InvalidRequestException("Count of rooms cannot be negative.");
        }
    }
}
