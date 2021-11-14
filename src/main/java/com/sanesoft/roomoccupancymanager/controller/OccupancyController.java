package com.sanesoft.roomoccupancymanager.controller;

import com.sanesoft.roomoccupancymanager.model.request.OccupancyAllocateDto;
import com.sanesoft.roomoccupancymanager.model.response.OccupancyAllocateResponseDto;
import com.sanesoft.roomoccupancymanager.service.OccupancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/occupancy")
@RequiredArgsConstructor
public class OccupancyController {

    private final OccupancyService occupancyService;

    @PostMapping(value = "allocate", produces = "application/json", consumes = "application/json")
    ResponseEntity<OccupancyAllocateResponseDto> allocate(@RequestBody OccupancyAllocateDto request) {
        return ResponseEntity.ok()
                .body(occupancyService.allocate(request));
    }
}
