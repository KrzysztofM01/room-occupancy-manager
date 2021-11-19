package com.sanesoft.roomoccupancymanager.service.occupancy.priority.configuration;

import com.sanesoft.roomoccupancymanager.model.request.RoomTypeDto;
import com.sanesoft.roomoccupancymanager.service.occupancy.priority.PriorityStrategyType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.Map;

@ConfigurationProperties(prefix = "priority.strategy")
@Data
public class PriorityStrategyConfigurationProperty {

    private PriorityStrategyType type;
    private Map<RoomTypeDto, BigDecimal> thresholds;
}
