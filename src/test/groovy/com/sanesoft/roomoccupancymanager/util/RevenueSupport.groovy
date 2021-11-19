package com.sanesoft.roomoccupancymanager.util

import com.sanesoft.roomoccupancymanager.service.guest.model.RoomGuest

import java.math.RoundingMode

trait RevenueSupport {

    BigDecimal bd(double aDouble) {
        new BigDecimal(aDouble).setScale(2, RoundingMode.HALF_UP)
    }


    RoomGuest rg(double aDouble) {
        new RoomGuest(bd(aDouble))
    }
}
