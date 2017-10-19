package com.example.epatterson.tiltmenuproto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by epatterson on 10/18/2017.
 */

public class MenuOptionDataPoint {

    private String dateStamp;
    private String targetOption;
    private int timeToFind;
    private boolean userSuccess;

    public MenuOptionDataPoint(String target, int seconds, boolean success)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CANADA);
        Date date = new Date();
        dateStamp = dateFormat.format(date);
        targetOption = target;
        timeToFind = seconds;
        userSuccess = success;
    }

    public String toCsvString()
    {
        return dateStamp + "," + targetOption + "," + timeToFind + "," + userSuccess;
    }
}
