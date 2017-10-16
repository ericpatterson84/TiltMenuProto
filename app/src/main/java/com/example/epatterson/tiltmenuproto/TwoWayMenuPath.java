package com.example.epatterson.tiltmenuproto;

/**
 * Created by epatterson on 10/16/2017.
 */

public class TwoWayMenuPath {

    private String destination = "";
    private boolean[] path;

    public TwoWayMenuPath()
    {

    }

    public void setDestination(String dest)
    {
        destination = dest;
    }

    public String getDestination()
    {
        return destination;
    }

    public void setPath(boolean[] menuPath)
    {
        path = menuPath;
    }

    public boolean isPathMatch(boolean[] otherPath)
    {
        if(otherPath.length != path.length)
        {
            return false;
        }

        for(int i = 0; i < otherPath.length; i++)
        {
            if(otherPath[i] != path[i])
            {
                return false;
            }
        }

        return true;
    }

    public int getPathLength()
    {
        return path.length;
    }
}
