package com.sanesoft.roomoccupancymanager.util

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification

trait RestClientSupport {

    RequestSpecification emptyRequest() {
        RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
    }

    ValidatableResponse expectPost(String endpointPath, String body) {
        emptyRequest().body(body).post(endpointPath).then()
    }
}