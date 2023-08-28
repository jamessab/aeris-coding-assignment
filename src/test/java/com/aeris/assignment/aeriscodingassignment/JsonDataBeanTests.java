package com.aeris.assignment.aeriscodingassignment;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class JsonDataBeanTests {

    /**
     * Test the json data bean initialization
     */
    @Test
    public void getJsonDataBeanHappyPath() {
        JsonDataBean jsonDataBean = new JsonDataBean();
        jsonDataBean.setValues(1, 2, 3);

        assertThat(jsonDataBean.getX()).isEqualTo(1);
        assertThat(jsonDataBean.getY()).isEqualTo(2);
        assertThat(jsonDataBean.getConcentration()).isEqualTo(3);
    }
}