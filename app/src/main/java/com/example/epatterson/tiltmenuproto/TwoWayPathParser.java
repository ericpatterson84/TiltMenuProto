package com.example.epatterson.tiltmenuproto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by epatterson on 10/16/2017.
 */

public class TwoWayPathParser {
    public TwoWayPathParser()
    {

    }

    public ArrayList<TwoWayMenuPath> parsePathsJson(String rawPathsJson)
    {
        ArrayList<TwoWayMenuPath> menuPaths = new ArrayList<>();
        try{
            JSONObject rootPaths = new JSONObject(rawPathsJson);
            if(!rootPaths.has("paths"))
            {
                return menuPaths;
            }

            JSONArray pathsJsonArray = rootPaths.getJSONArray("paths");
            for(int i = 0; i < pathsJsonArray.length(); i++)
            {
                TwoWayMenuPath menuPath = parseMenuPath(pathsJsonArray.getJSONObject(i));
                if(menuPath != null) {
                    menuPaths.add(menuPath);
                }
            }
        } catch (JSONException je) {
            return menuPaths;
        }

        return menuPaths;
    }

    private TwoWayMenuPath parseMenuPath(JSONObject pathObj)
    {
        TwoWayMenuPath menuPath = new TwoWayMenuPath();
        if(!pathObj.has("description"))
        {
            return null;
        }
        try {
            menuPath.setDestination(pathObj.getString("description"));
            JSONArray correctPathJson = pathObj.getJSONArray("correctPath");
            boolean[] correctPath = new boolean[correctPathJson.length()];
            for(int i = 0; i < correctPathJson.length(); i++)
            {
                correctPath[i] = correctPathJson.getBoolean(i);
            }
            menuPath.setPath(correctPath);

        } catch (JSONException je) {
            return null;
        }
        return menuPath;
    }
}
