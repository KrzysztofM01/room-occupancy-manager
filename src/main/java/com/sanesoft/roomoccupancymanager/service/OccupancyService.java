package com.sanesoft.roomoccupancymanager.service;

import com.sanesoft.roomoccupancymanager.model.request.OccupancyAllocateDto;
import com.sanesoft.roomoccupancymanager.model.response.OccupancyAllocateResponseDto;

public interface OccupancyService {

    OccupancyAllocateResponseDto allocate(OccupancyAllocateDto request);
}
