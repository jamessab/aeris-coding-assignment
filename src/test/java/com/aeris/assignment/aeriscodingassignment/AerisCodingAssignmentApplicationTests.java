package com.aeris.assignment.aeriscodingassignment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AerisCodingAssignmentApplicationTests {

    @Autowired
    private AerisCodingAssignmentApplication app;

    @Test
    public void contextLoads() throws Exception {
        assertThat(app).isNotNull();
    }

}
