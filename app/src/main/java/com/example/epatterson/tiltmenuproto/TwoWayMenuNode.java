package com.example.epatterson.tiltmenuproto;

import java.util.ArrayList;

/**
 * Created by epatterson on 10/14/2017.
 */

public class TwoWayMenuNode {

    protected ArrayList<TwoWayMenuNode> subNodes;
//    protected TwoWayMenuNode subNodeB = null;
    protected String label;

    public enum SubNodeId { A, B }

    public TwoWayMenuNode()
    {
        subNodes = new ArrayList<>();
        label = "N/A";
    }

    public void appendSubNode(TwoWayMenuNode subNode)
    {
        subNodes.add(subNode);
    }

    public String getLabel()
    {
        return label;
    }

    public String getSubLabel(SubNodeId subId)
    {
        if(subNodes.size() != 2)
        {
            return "";
        }
        return subNodes.get(subId == SubNodeId.A ? 0 : 1).getLabel();
    }

    public boolean hasSubNodes()
    {
        return subNodes.size() == 2;
    }

    public TwoWayMenuNode getSubNode(SubNodeId subId)
    {
        if(subNodes.size() != 2)
        {
            return null;
        }
        return subNodes.get(subId == SubNodeId.A ? 0 : 1);
    }
}
