package com.sanesoft.roomoccupancymanager.controller;

import com.sanesoft.roomoccupancymanager.model.request.OccupancyPredictAllocationDto;
import com.sanesoft.roomoccupancymanager.model.response.OccupancyPredictAllocationResponseDto;
import com.sanesoft.roomoccupancymanager.service.occupancy.OccupancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/occupancy")
@RequiredArgsConstructor
public class OccupancyController {

    private final OccupancyService occupancyService;

    @PostMapping(value = "predict-allocation", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<OccupancyPredictAllocationResponseDto> predictAllocation(@RequestBody OccupancyPredictAllocationDto request) {
        return ResponseEntity.ok()
                .body(occupancyService.predictAllocation(request));
    }
}
