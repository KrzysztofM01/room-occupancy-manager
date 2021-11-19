package com.sanesoft.roomoccupancymanager.service.occupancy.priority;

import com.sanesoft.roomoccupancymanager.model.request.RoomTypeDto;
import com.sanesoft.roomoccupancymanager.service.guest.model.RoomGuest;
import com.sanesoft.roomoccupancymanager.service.occupancy.priority.configuration.PriorityStrategyConfigurationProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class HighestBidderWithThresholdsStrategy implements PriorityStrategy {

    private final List<RoomWithPrice> sortedRoomsWithPriceThresholds;

    public HighestBidderWithThresholdsStrategy(PriorityStrategyConfigurationProperty thresholdsConfig) {
        var thresholdsCopy = new HashMap<>(thresholdsConfig.getThresholds());
        Arrays.stream(RoomTypeDto.values()).forEach(roomType -> thresholdsCopy.putIfAbsent(roomType, null));
        this.sortedRoomsWithPriceThresholds = sortRoomTypesByThresholdsDescending(thresholdsCopy);
    }

    private List<RoomWithPrice> sortRoomTypesByThresholdsDescending(Map<RoomTypeDto, BigDecimal> roomTypeThresholds) {
        return roomTypeThresholds.entrySet()
                .stream()
                .map(e -> new RoomWithPrice(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(RoomWithPrice::priceThreshold, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    @Override
    public Map<RoomTypeDto, List<RoomGuest>> findPreferredGuests(List<RoomGuest> availableRoomGuests,
                                                                 Map<RoomTypeDto, Integer> availableRoomsCount) {

        var roomOccupancies = new LinkedList<RoomOccupancy>();
        var bidSortedRoomGuests = sortRoomGuestsByBidDescending(availableRoomGuests);

        for (RoomWithPrice roomWithPrice : sortedRoomsWithPriceThresholds) {
            var roomAssignResult = assignRoomGuestsToRoomType(
                    bidSortedRoomGuests,
                    roomWithPrice,
                    availableRoomsCount.getOrDefault(roomWithPrice.roomType(), 0)
            );

            roomOccupancies.addAll(roomAssignResult.roomOccupancies());
            bidSortedRoomGuests.removeAll(roomAssignResult.guestsWithRooms());
            bidSortedRoomGuests.removeAll(roomAssignResult.guestsWithoutRooms());

            int freedRoomsCount = removeUnoccupiedRoomsToMakePlaceForGuestsWithoutRooms(roomOccupancies,
                    roomAssignResult.guestsWithoutRooms().size());

            var roomOccupanciesToAdd = roomAssignResult.guestsWithoutRooms()
                    .subList(0, freedRoomsCount)
                    .stream()
                    .map(RoomOccupancy::occupied)
                    .collect(Collectors.toList());
            roomOccupancies.addAll(roomOccupanciesToAdd);
        }

        return mapToRoomTypeWithGuests(availableRoomsCount, roomOccupancies);
    }

    private List<RoomGuest> sortRoomGuestsByBidDescending(List<RoomGuest> availableRoomGuests) {
        return availableRoomGuests.stream()
                .sorted(Comparator.comparing(RoomGuest::amountWillingToPay, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    private RoomAssignResult assignRoomGuestsToRoomType(List<RoomGuest> bidSortedRoomGuests,
                                                        RoomWithPrice roomWithPrice,
                                                        int availableRoomCount) {

        var roomOccupancies = new LinkedList<RoomOccupancy>();
        var guestsWithRooms = new LinkedList<RoomGuest>();
        var guestsWithoutRooms = new LinkedList<RoomGuest>();
        int unoccupiedRoomsCount = availableRoomCount;

        for (RoomGuest roomGuest : bidSortedRoomGuests) {

            if (roomWithPrice.priceThreshold() == null
                    || roomGuestIsWillingToPayMoreThanThreshold(roomWithPrice, roomGuest)
                    || isCheapestOption(roomWithPrice)) {
                guestsWithRooms.add(roomGuest);

                if (unoccupiedRoomsCount > 0) {
                    roomOccupancies.add(RoomOccupancy.occupied(roomGuest));
                    unoccupiedRoomsCount--;
                } else {
                    guestsWithoutRooms.add(roomGuest);
                }

            } else {
                break;
            }
        }

        roomOccupancies.addAll(Collections.nCopies(unoccupiedRoomsCount, RoomOccupancy.empty()));

        return new RoomAssignResult(roomOccupancies, guestsWithRooms, guestsWithoutRooms);
    }

    private boolean isCheapestOption(RoomWithPrice roomWithPrice) {
        return roomWithPrice == sortedRoomsWithPriceThresholds.get(sortedRoomsWithPriceThresholds.size() - 1);
    }

    private boolean roomGuestIsWillingToPayMoreThanThreshold(RoomWithPrice roomWithPrice, RoomGuest roomGuest) {
        return roomWithPrice.priceThreshold().compareTo(roomGuest.amountWillingToPay()) <= 0;
    }

    private int removeUnoccupiedRoomsToMakePlaceForGuestsWithoutRooms(List<RoomOccupancy> roomOccupancies,
                                                                      int guestsWithoutRoomCount) {

        ListIterator<RoomOccupancy> iterator = roomOccupancies.listIterator(roomOccupancies.size());
        int freedRoomsCount = 0;

        while (iterator.hasPrevious() && freedRoomsCount < guestsWithoutRoomCount) {
            if (iterator.previous().isEmpty()) {
                iterator.remove();
                freedRoomsCount++;
            }
        }

        return freedRoomsCount;
    }

    private Map<RoomTypeDto, List<RoomGuest>> mapToRoomTypeWithGuests(Map<RoomTypeDto, Integer> availableRoomsCount,
                                                                      List<RoomOccupancy> roomOccupancies) {
        Map<RoomTypeDto, List<RoomGuest>> result = new HashMap<>();
        sortedRoomsWithPriceThresholds.forEach(rwp -> {
            for (int i = 0; i < availableRoomsCount.getOrDefault(rwp.roomType(), 0); i++) {
                RoomOccupancy roomOccupancy = roomOccupancies.remove(0);
                if (!roomOccupancy.isEmpty()) {
                    result.computeIfAbsent(rwp.roomType(), rt -> new ArrayList<>()).add(roomOccupancy.occupant());
                }
            }
        });
        return result;
    }


    @Override
    public PriorityStrategyType getPriorityStrategyType() {
        return PriorityStrategyType.HIGHEST_BIDDER_WITH_THRESHOLDS;
    }

    private record RoomWithPrice(RoomTypeDto roomType,
                                 BigDecimal priceThreshold) {
    }

    private record RoomAssignResult(List<RoomOccupancy> roomOccupancies,
                                    List<RoomGuest> guestsWithRooms,
                                    List<RoomGuest> guestsWithoutRooms) {
    }

    private record RoomOccupancy(RoomGuest occupant,
                                 boolean isEmpty) {

        private static RoomOccupancy occupied(RoomGuest roomGuest) {
            return new RoomOccupancy(roomGuest, false);
        }

        private static RoomOccupancy empty() {
            return new RoomOccupancy(null, true);
        }
    }
}
