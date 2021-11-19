package com.sanesoft.roomoccupancymanager.service.occupancy;

import com.sanesoft.roomoccupancymanager.model.request.OccupancyPredictAllocationDto;
import com.sanesoft.roomoccupancymanager.model.response.OccupancyPredictAllocationResponseDto;

public interface OccupancyService {

    OccupancyPredictAllocationResponseDto predictAllocation(OccupancyPredictAllocationDto request);
}
