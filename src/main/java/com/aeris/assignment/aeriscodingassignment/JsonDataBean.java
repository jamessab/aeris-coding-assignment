package com.aeris.assignment.aeriscodingassignment;

import org.springframework.stereotype.Component;

/**
 * This is used solely to control the JSON output that gets generate from the /get-data request. There is no built-in
 * method inside the netCDF library to do this, at least that I could find.
 *
 * If we dump the entire x, y or concentration object to json we get many extraneous fields and the array we are
 * interested in is called "storageD" which isn't helpful.
 *
 * By creating this custom class we can populate with the data we need in the json and control how it's
 * serialized to json.
 */

@Component("jsonDataBean")
public class JsonDataBean {
    private Object x;
    private Object y;
    private Object concentration;

    public Object getX() {
        return x;
    }

    public Object getY() {
        return y;
    }

    public Object getConcentration() {
        return concentration;
    }

    public void setValues(Object x, Object y, Object concentration) {
        this.x = x;
        this.y = y;
        this.concentration = concentration;
    }
}
