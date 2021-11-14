package com.sanesoft.roomoccupancymanager.integration

import com.sanesoft.roomoccupancymanager.integration.util.AbstractIntegrationSpec
import com.sanesoft.roomoccupancymanager.integration.util.RestClientSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import spock.lang.Unroll

import java.math.RoundingMode

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasSize

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoomOccupancyManagerSpec extends AbstractIntegrationSpec implements RestClientSupport {

    @Autowired
    ApplicationContext applicationContext

    def "context should be autowired"() {
        expect:
        applicationContext
    }

    @Unroll
    def "invalid request should return 404"() {
        expect:
        expectPost("occupancy/allocate", body)
                .statusCode(HttpStatus.BAD_REQUEST.value())

        where:
        body << [
                """{}""",
                "",
                """{"premiumRooms": 1}""",
                """{"economyRooms": 1}""",
                """{"economyRooms": null, "premiumRooms": 1}""",
                """{"economyRooms": -1, "premiumRooms": 1}""",
                """{"economyRooms": 0.5, "premiumRooms": 1}"""
        ]
    }

    @Unroll
    def "occupancy allocate endpoint should return number of rooms occupied and their revenue"() {
        given:
        String requestBody = """{
  "premiumRooms": $premiumRooms,
  "economyRooms": $economyRooms
}"""

        expect:
        expectPost("occupancy/allocate", requestBody)
                .statusCode(HttpStatus.OK.value())
                .body("usedRooms", hasSize(usedRoomsTotal(usedPremiumRooms, usedEconomyRooms)))
                .body("totalRevenue", equalTo(totalRevenue(premiumRevenue, economyRevenue)))
                .body("usedRooms.find { it.type == 'ECONOMY' }.count", equalTo(usedEconomyRooms))
                .body("usedRooms.find { it.type == 'ECONOMY' }.revenue", equalTo(economyRevenue))
                .body("usedRooms.find { it.type == 'PREMIUM' }.count", equalTo(usedPremiumRooms))
                .body("usedRooms.find { it.type == 'PREMIUM' }.revenue", equalTo(premiumRevenue))

        where:
        premiumRooms | economyRooms || usedPremiumRooms | usedEconomyRooms | premiumRevenue | economyRevenue
        3            | 3            || 3                | 3                | 738f           | 167.99f
        7            | 5            || 6                | 4                | 1054f          | 189.99f
        2            | 7            || 2                | 4                | 583f           | 189.99f
        7            | 1            || 7                | 1                | 1153f          | 45.99f
        0            | 1            || 0                | 1                | 0f             | 99.99f
        1            | 0            || 1                | 0                | 374f           | 0f
        0            | 0            || 0                | 0                | 0f             | 0f
    }

    static int usedRoomsTotal(int usedPremiumRooms, int usedEconomyRooms) {
        ((usedPremiumRooms > 0) ? 1 : 0) + ((usedEconomyRooms > 0) ? 1 : 0)
    }

    static float totalRevenue(float premiumRevenue, float economyRevenue) {
        new BigDecimal(premiumRevenue + economyRevenue).setScale(2, RoundingMode.HALF_UP).toFloat()
    }
}
