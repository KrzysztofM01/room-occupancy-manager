package com.sanesoft.roomoccupancymanager.integration

import com.sanesoft.roomoccupancymanager.integration.util.AbstractIntegrationSpec
import com.sanesoft.roomoccupancymanager.util.RestClientSupport
import com.sanesoft.roomoccupancymanager.service.guest.RoomGuestService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import spock.lang.Unroll

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasSize

@ActiveProfiles("test")
class OccupationPredictAllocationSpec extends AbstractIntegrationSpec implements RestClientSupport {

    @Autowired
    ApplicationContext applicationContext

    @SpringBean
    RoomGuestService roomGuestService = Mock() {
        getAvailableRoomGuests() >> [
                rg(374),
                rg(209),
                rg(155),
                rg(115),
                rg(101),
                rg(100),
                rg(99.99),
                rg(45),
                rg(23),
                rg(22)
        ]
    }

    def "context should be autowired"() {
        expect:
        applicationContext
    }

    @Unroll
    def "invalid request should return bad request response"() {
        expect:
        expectPost("occupancy/predict-allocation", body)
                .statusCode(HttpStatus.BAD_REQUEST.value())

        where:
        body << [
                """{}""",
                "",
                """{"availableRooms": {"ECONOMY": -1}""",
                """{"availableRooms": {"ECONOMY": 0.5}""",
                """{"availableRooms": {"ECONOMY": 1.0}""",
                """{"availableRooms": {"ECONOMY": "1"}""",
                """{"availableRooms": {"ECONOMY": "null"}""",
                """{"availableRooms": {"ECONOMY": null}"""
        ]
    }

    @Unroll
    def "request with enough room types for each room guest bids should be processed"() {
        given:
        String requestBody = """
{
  "availableRooms": {
    "ECONOMY": $economyRooms,
    "PREMIUM": $premiumRooms
  }
}
"""

        expect:
        expectPost("occupancy/predict-allocation", requestBody)
                .statusCode(HttpStatus.OK.value())
                .body("usedRoomTypes", hasSize(usedRoomsTotal(usedPremiumRooms, usedEconomyRooms)))
                .body("totalRevenue", equalTo(totalRevenue(premiumRevenue, economyRevenue)))
                .body("usedRoomTypes.find { it.type == 'ECONOMY' }.count", equalTo(usedEconomyRooms))
                .body("usedRoomTypes.find { it.type == 'ECONOMY' }.revenue", equalTo(economyRevenue))
                .body("usedRoomTypes.find { it.type == 'PREMIUM' }.count", equalTo(usedPremiumRooms))
                .body("usedRoomTypes.find { it.type == 'PREMIUM' }.revenue", equalTo(premiumRevenue))

        where:
        premiumRooms | economyRooms || usedPremiumRooms | usedEconomyRooms | premiumRevenue | economyRevenue
        3            | 3            || 3                | 3                | 738f           | 167.99f
        7            | 5            || 6                | 4                | 1054f          | 189.99f
        2            | 7            || 2                | 4                | 583f           | 189.99f
    }

    def "request with not enough ECONOMY rooms should allocate people with lower bids into PREMIUM rooms"() {
        given:
        String requestBody = """
{
  "availableRooms": {
    "ECONOMY": 1,
    "PREMIUM": 7
  }
}
"""

        expect:
        expectPost("occupancy/predict-allocation", requestBody)
                .statusCode(HttpStatus.OK.value())
                .body("usedRoomTypes", hasSize(usedRoomsTotal(7, 1)))
                .body("totalRevenue", equalTo(totalRevenue(1153.99f, 45f)))
                .body("usedRoomTypes.find { it.type == 'ECONOMY' }.count", equalTo(1))
                .body("usedRoomTypes.find { it.type == 'ECONOMY' }.revenue", equalTo(45f))
                .body("usedRoomTypes.find { it.type == 'PREMIUM' }.count", equalTo(7))
                .body("usedRoomTypes.find { it.type == 'PREMIUM' }.revenue", equalTo(1153.99f))
    }

    def "request with zeroes or non specified room types should be processed returning the correct revenue"() {
        expect:
        expectPost("occupancy/predict-allocation", requestBody)
                .statusCode(HttpStatus.OK.value())
                .body("usedRoomTypes", hasSize(usedRoomsTotal(usedPremiumRooms, usedEconomyRooms)))
                .body("totalRevenue", equalTo(totalRevenue(premiumRevenue, economyRevenue)))
                .body("usedRoomTypes.find { it.type == 'ECONOMY' }.count", equalTo(usedEconomyRooms))
                .body("usedRoomTypes.find { it.type == 'ECONOMY' }.revenue", equalTo(economyRevenue))
                .body("usedRoomTypes.find { it.type == 'PREMIUM' }.count", equalTo(usedPremiumRooms))
                .body("usedRoomTypes.find { it.type == 'PREMIUM' }.revenue", equalTo(premiumRevenue))

        where:
        requestBody                                          || usedPremiumRooms | usedEconomyRooms | premiumRevenue | economyRevenue
        """{"availableRooms":{"ECONOMY": 0,"PREMIUM": 1}}""" || 1                | null             | 374f           | null
        """{"availableRooms":{"PREMIUM": 1}}"""              || 1                | null             | 374f           | null
        """{"availableRooms":{"ECONOMY": 1,"PREMIUM": 0}}""" || null             | 1                | null           | 99.99f
        """{"availableRooms":{"ECONOMY": 1}}"""              || null             | 1                | null           | 99.99f
        """{"availableRooms":{"ECONOMY": 0,"PREMIUM": 0}}""" || null             | null             | null           | null
        """{"availableRooms":{"ECONOMY": 0}}"""              || null             | null             | null           | null
        """{"availableRooms":{}}"""                          || null             | null             | null           | null
    }


    float totalRevenue(Float premiumRevenue, Float economyRevenue) {
        bd((premiumRevenue ? premiumRevenue : 0) + (economyRevenue ? economyRevenue : 0))
                .toFloat()
    }

    static int usedRoomsTotal(Integer usedPremiumRooms, Integer usedEconomyRooms) {
        (usedPremiumRooms ? 1 : 0) + (usedEconomyRooms ? 1 : 0)
    }
}
