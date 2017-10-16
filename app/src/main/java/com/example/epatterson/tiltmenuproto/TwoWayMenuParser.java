package com.example.epatterson.tiltmenuproto;

import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by epatterson on 10/14/2017.
 */

public class TwoWayMenuParser {

    public TwoWayMenuParser()
    {

    }

    public TwoWayMenuNode parseFromJson(String rawMenuJson)
    {
        try {
            JSONObject rootJsonObj = new JSONObject(rawMenuJson);
            TwoWayMenuNode rootMenuNode = new TwoWayMenuNode();
            rootMenuNode.label = "Root";
//            if(rootJsonObj.has("menu")) {
                parseMenuLevel(rootJsonObj, rootMenuNode);
                return rootMenuNode.;
//            }
//            return null;

        } catch(JSONException je) {
            return null;
        }
    }

    private void parseMenuLevel(JSONObject nodeJson, TwoWayMenuNode parentNode)
    {
        TwoWayMenuNode menuNode = new TwoWayMenuNode();
        try{
            menuNode.label = nodeJson.getString("label");
            parentNode.appendSubNode(menuNode);
        } catch(JSONException je) {
            return;
        }

        if(nodeJson.has("sub"))
        {
            try {
                JSONArray subNodes = nodeJson.getJSONArray("sub");
                if(subNodes.length() != 2)
                {
                    return;
                }
                for(int i=0; i < 2; i++) {
                    parseMenuLevel(subNodes.getJSONObject(i), menuNode);
                }

            } catch(JSONException je) {
                return;
            }
        }
    }
}
