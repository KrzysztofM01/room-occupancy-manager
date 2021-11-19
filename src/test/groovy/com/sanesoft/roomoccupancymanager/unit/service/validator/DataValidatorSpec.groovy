package com.sanesoft.roomoccupancymanager.unit.service.validator

import com.sanesoft.roomoccupancymanager.controller.exception.InvalidRequestException
import com.sanesoft.roomoccupancymanager.model.request.OccupancyPredictAllocationDto
import com.sanesoft.roomoccupancymanager.service.occupancy.validator.DataValidator
import spock.lang.Specification

import static com.sanesoft.roomoccupancymanager.model.request.RoomTypeDto.ECONOMY
import static com.sanesoft.roomoccupancymanager.model.request.RoomTypeDto.PREMIUM

class DataValidatorSpec extends Specification {

    DataValidator dataValidator = new DataValidator()

    def "valid data should not throw any exception"() {
        when:
        dataValidator.validate(new OccupancyPredictAllocationDto([(PREMIUM): 1, (ECONOMY): 1]))

        then:
        noExceptionThrown()
    }

    def "invalid data should throw InvalidRequestException"() {
        when:
        dataValidator.validate(data)

        then:
        def exception = thrown(InvalidRequestException)
        exception.message == expectedMsg

        where:
        data || expectedMsg
        new OccupancyPredictAllocationDto([(PREMIUM): null, (ECONOMY): 1]) || "Invalid PREMIUM rooms count was provided."
        new OccupancyPredictAllocationDto([(PREMIUM): 1, (ECONOMY): null]) || "Invalid ECONOMY rooms count was provided."
        new OccupancyPredictAllocationDto([(PREMIUM): -1, (ECONOMY): 1])   || "Invalid PREMIUM rooms count was provided."
        new OccupancyPredictAllocationDto([(PREMIUM): 1, (ECONOMY): -1])   || "Invalid ECONOMY rooms count was provided."
    }
}
