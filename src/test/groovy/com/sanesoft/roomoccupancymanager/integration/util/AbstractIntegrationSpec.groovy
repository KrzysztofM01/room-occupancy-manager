package com.sanesoft.roomoccupancymanager.integration.util

import io.restassured.RestAssured
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AbstractIntegrationSpec extends Specification {

    @LocalServerPort
    int servicePort

    def setup() {
        RestAssured.port = servicePort
    }

}