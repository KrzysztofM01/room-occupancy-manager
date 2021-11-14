package com.sanesoft.roomoccupancymanager.service;

import com.sanesoft.roomoccupancymanager.model.request.OccupancyAllocateDto;
import com.sanesoft.roomoccupancymanager.model.response.OccupancyAllocateResponseDto;
import com.sanesoft.roomoccupancymanager.service.validator.DataValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriorityBasedOccupancyService implements OccupancyService {

    private final DataValidator dataValidator;

    @Override
    public OccupancyAllocateResponseDto allocate(@NonNull OccupancyAllocateDto request) {
        dataValidator.validate(request);
        return null;
    }
}
