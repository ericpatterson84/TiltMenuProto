package com.example.epatterson.tiltmenuproto;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by epatterson on 10/18/2017.
 */

public class DataCollectionManager {
    private Date startTime, endTime = null;
    private Tracker mTracker = null;

    public DataCollectionManager()
    {
    }

    public void logMenuOptionData(String target, int seconds, boolean success)
    {
        if(mTracker != null)
        {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("MenuTraverse")
                    .setAction(target)
                    .setValue(seconds)
                    .set("Success", success ? "Yes" : "No")
                    .build());
        }
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

    public void setTracker(Tracker dataTracker)
    {
        mTracker = dataTracker;
    }
}
