package com.sanesoft.roomoccupancymanager.unit.service.occupancy.priority

import com.sanesoft.roomoccupancymanager.service.occupancy.priority.HighestBidderWithThresholdsStrategy
import com.sanesoft.roomoccupancymanager.service.occupancy.priority.configuration.PriorityStrategyConfigurationProperty
import com.sanesoft.roomoccupancymanager.util.RevenueSupport
import spock.lang.Specification
import spock.lang.Unroll

import static com.sanesoft.roomoccupancymanager.model.request.RoomTypeDto.ECONOMY
import static com.sanesoft.roomoccupancymanager.model.request.RoomTypeDto.PREMIUM

class HighestBidderWithThresholdsStrategySpec extends Specification implements RevenueSupport {

    PriorityStrategyConfigurationProperty thresholdsConfig = Mock()

    def "only the highest bidders inside thresholds should be allocated to rooms"() {
        given:
        thresholdsConfig.getThresholds() >> [(PREMIUM): bd(100)]
        def strategy = new HighestBidderWithThresholdsStrategy(thresholdsConfig)

        when:
        def result = strategy.findPreferredGuests([rg(20), rg(30), rg(110), rg(100)], [(PREMIUM): 1, (ECONOMY): 1])

        then:
        result == [(PREMIUM): [rg(110)], (ECONOMY): [rg(30)]]
    }

    def "guests willing to pay more than threshold should not be assigned to lower level room type"() {
        given:
        thresholdsConfig.getThresholds() >> [(PREMIUM): bd(100)]
        def strategy = new HighestBidderWithThresholdsStrategy(thresholdsConfig)

        when:
        def result = strategy.findPreferredGuests([rg(110)], [(PREMIUM): 0, (ECONOMY): 1])

        then:
        result == [:]
    }

    def "guests from lower room type should be upgraded to upper level if there are not enough rooms on their bet level"() {
        given:
        thresholdsConfig.getThresholds() >> [(PREMIUM): bd(100)]
        def strategy = new HighestBidderWithThresholdsStrategy(thresholdsConfig)

        when:
        def result = strategy.findPreferredGuests([rg(100), rg(55), rg(50)], [(ECONOMY): 1, (PREMIUM): 2])

        then:
        result == [(PREMIUM): [rg(100), rg(55)], (ECONOMY): [rg(50)]]
    }

    def """guests from lower room type should not be upgraded to upper level if there are not enough rooms on their
           bet level and there are no rooms left on upper level"""() {
        given:
        thresholdsConfig.getThresholds() >> [(PREMIUM): bd(100)]
        def strategy = new HighestBidderWithThresholdsStrategy(thresholdsConfig)

        when:
        def result = strategy.findPreferredGuests([rg(100), rg(55), rg(50)], [(PREMIUM): 1, (ECONOMY): 1])

        then:
        result == [(PREMIUM): [rg(100)], (ECONOMY): [rg(55)]]
    }

    def "guest with bid lower than any of room thresholds should be assigned to room type of lowest possible quality"() {
        given:
        thresholdsConfig.getThresholds() >> [(ECONOMY): bd(50), (PREMIUM): bd(100)]
        def strategy = new HighestBidderWithThresholdsStrategy(thresholdsConfig)

        when:
        def result = strategy.findPreferredGuests([rg(20)], [(PREMIUM): 1, (ECONOMY): 1])

        then:
        result == [(ECONOMY): [rg(20)]]
    }

    @Unroll
    def "#testDescription should return empty map"() {
        given:
        thresholdsConfig.getThresholds() >> [(PREMIUM): bd(100), (ECONOMY): bd(50)]
        def strategy = new HighestBidderWithThresholdsStrategy(thresholdsConfig)

        when:
        def result = strategy.findPreferredGuests(roomGuests, roomMap)

        then:
        result == [:]

        where:
        testDescription      | roomGuests | roomMap
        "no room guests"     | []         | [(PREMIUM): 1, (ECONOMY): 1]
        "no available rooms" | [rg(20)]   | [(PREMIUM): 0, (ECONOMY): 0]
        "no available rooms" | [rg(20)]   | [:]
    }
}
