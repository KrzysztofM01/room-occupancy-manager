package com.sanesoft.roomoccupancymanager.integration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoomOccupancyManagerSpec extends Specification {

    @Autowired
    ApplicationContext applicationContext

    def "context should be autowired"() {
        expect:
        applicationContext
    }

}
