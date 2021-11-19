package com.sanesoft.roomoccupancymanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.sanesoft")
public class RoomOccupancyManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoomOccupancyManagerApplication.class, args);
	}

}
