package com.sanesoft.roomoccupancymanager.service.occupancy.validator;

import com.sanesoft.roomoccupancymanager.controller.exception.InvalidRequestException;
import com.sanesoft.roomoccupancymanager.model.request.OccupancyPredictAllocationDto;
import org.springframework.stereotype.Component;

@Component
public class DataValidator {

    public void validate(OccupancyPredictAllocationDto request) {
        request.availableRooms().forEach((key, value) -> {
            if (value == null || value < 0) {
                throw new InvalidRequestException("Invalid %s rooms count was provided.".formatted(key));
            }
        });
    }
}
