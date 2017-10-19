package com.example.epatterson.tiltmenuproto;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by epatterson on 10/18/2017.
 */

public class DataCollectionManager {
    private ArrayList<MenuOptionDataPoint> dataPoints = null;
    private Date startTime, endTime = null;

    public DataCollectionManager()
    {
        dataPoints = new ArrayList<>();
    }

    public void logMenuOptionData(String target, int seconds, boolean success)
    {
        dataPoints.add( new MenuOptionDataPoint(target, seconds, success) );
    }

    public String loggedDataAsCsvString()
    {
        String dataAsCsv = "Time-stamp,Menu Target,Duration,Success\n";
        Iterator<MenuOptionDataPoint> dataIter = dataPoints.iterator();
        while(dataIter.hasNext())
        {
            dataAsCsv += dataIter.next().toCsvString() + "\n";
        }
        return dataAsCsv;
    }

    public void clearLoggedData()
    {
        dataPoints.clear();
    }

    public void startTimer()
    {
        startTime = new Date();
    }

    public void stopTimer()
    {
        endTime = new Date();
    }

    public int getDuration()
    {
        if(startTime != null && endTime != null)
        {
            return (int)((endTime.getTime() - startTime.getTime()) / 1000);
        }
        return 0;
    }
}
