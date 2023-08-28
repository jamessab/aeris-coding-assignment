package com.aeris.assignment.aeriscodingassignment;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Value;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AerisCodingAssignmentHttpTests {

    @Value(value="${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * /get-info tests
     */
    @Test
    public void getInfoHappyPath() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/get-info",
                String.class)).contains("NetcdfFile location=");
    }

    /**
     * /get-data tests
     */
    @Test
    public void getDataHappyPath() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/get-data?time-index=0&z-index=0",
                String.class)).contains("\"concentration\" : [ ");
    }

    @Test
    public void getDataInvalidTimeIndex() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/get-data?time-index=999&z-index=0",
                String.class)).contains("Invalid parameter. timeIndex must be between");
    }

    @Test
    public void getDataInvalidZIndex() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/get-data?time-index=0&z-index=1",
                String.class)).contains("Invalid parameter. zIndex must be between");
    }

    /**
     * /get-image tests
     */
    @Test
    public void getImageHappyPath() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/get-image?time-index=0&z-index=0",
                String.class)).hasSize(77);
    }

    @Test
    public void getImageInvalidTimeIndex() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/get-data?time-index=999&z-index=0",
                String.class)).contains("Invalid parameter. timeIndex must be between");
    }

    @Test
    public void getImageInvalidZIndex() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/get-data?time-index=0&z-index=0",
                String.class)).contains("Invalid parameter. zIndex must be between");
    }

    /**
     * Startup tests
     *
     * Not doing it for this project, but here we should mock out the NetCDFBean and make sure the startup
     * fails when the concentration can't be found, or it doesn't contain the required variable.
     *
     */

}