package com.sanesoft.roomoccupancymanager.service.occupancy.priority.configuration;

import com.sanesoft.roomoccupancymanager.service.occupancy.priority.PriorityStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class PriorityStrategyConfiguration {

    private final List<PriorityStrategy> priorityStrategies;
    private final PriorityStrategyConfigurationProperty configurationProperty;

    @Bean
    PriorityStrategy priorityStrategy() {
        return priorityStrategies.stream()
                .filter(s -> s.getPriorityStrategyType() == configurationProperty.getType())
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("There is no proper interface implementation for " +
                        "priority strategy type: " + configurationProperty.getType())
                );
    }
}
