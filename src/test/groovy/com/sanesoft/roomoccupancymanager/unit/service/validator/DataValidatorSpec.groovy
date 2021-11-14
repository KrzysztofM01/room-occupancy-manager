package com.sanesoft.roomoccupancymanager.unit.service.validator

import com.sanesoft.roomoccupancymanager.exception.InvalidRequestException
import com.sanesoft.roomoccupancymanager.model.request.OccupancyAllocateDto
import com.sanesoft.roomoccupancymanager.service.validator.DataValidator
import spock.lang.Specification

class DataValidatorSpec extends Specification {

    DataValidator dataValidator = new DataValidator()

    def "valid data should not throw any exception"() {
        when:
        dataValidator.validate(new OccupancyAllocateDto(1, 1))

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
        new OccupancyAllocateDto(null, 1) || "No premium rooms count was provided."
        new OccupancyAllocateDto(1, null) || "No economy rooms count was provided."
        new OccupancyAllocateDto(-1, 0) || "Count of rooms cannot be negative."
        new OccupancyAllocateDto(0, -1) || "Count of rooms cannot be negative."
    }
}
