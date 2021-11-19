package com.sanesoft.roomoccupancymanager.unit.service.occupancy

import com.sanesoft.roomoccupancymanager.model.request.OccupancyPredictAllocationDto
import com.sanesoft.roomoccupancymanager.model.response.UsedRoomsResponseDto
import com.sanesoft.roomoccupancymanager.service.guest.RoomGuestService
import com.sanesoft.roomoccupancymanager.service.guest.model.RoomGuest
import com.sanesoft.roomoccupancymanager.service.occupancy.OccupancyService
import com.sanesoft.roomoccupancymanager.service.occupancy.PriorityBasedOccupancyService
import com.sanesoft.roomoccupancymanager.service.occupancy.priority.PriorityStrategy
import com.sanesoft.roomoccupancymanager.service.occupancy.validator.DataValidator
import com.sanesoft.roomoccupancymanager.util.RevenueSupport
import spock.lang.Specification

import static com.sanesoft.roomoccupancymanager.model.request.RoomTypeDto.ECONOMY
import static com.sanesoft.roomoccupancymanager.model.request.RoomTypeDto.PREMIUM

class PriorityBasedOccupancyServiceSpec extends Specification implements RevenueSupport {

    DataValidator dataValidator = Mock()
    RoomGuestService roomGuestService = Mock()
    PriorityStrategy priorityStrategy = Mock()
    OccupancyService service = new PriorityBasedOccupancyService(dataValidator, roomGuestService, priorityStrategy)

    def "service should validate input, get available room guests, use strategy predict allocation and aggregate the response"() {
        given:
        def input = new OccupancyPredictAllocationDto([(PREMIUM): 1, (ECONOMY): 1])

        when:
        def result = service.predictAllocation(input)

        then:
        1 * dataValidator.validate(input)
        1 * roomGuestService.getAvailableRoomGuests() >> []
        1 * priorityStrategy.findPreferredGuests([], [(ECONOMY): 1, (PREMIUM): 1]) >> [
                (PREMIUM): [new RoomGuest(bd(1f)), new RoomGuest(bd(2f))],
                (ECONOMY): [new RoomGuest(bd(0.3f))]
        ]

        result.totalRevenue() == bd(3.3f)
        result.usedRoomTypes().toSet() == [
                new UsedRoomsResponseDto(PREMIUM, 2, bd(3f)),
                new UsedRoomsResponseDto(ECONOMY, 1, bd(0.3f))
        ] as Set
    }
}
