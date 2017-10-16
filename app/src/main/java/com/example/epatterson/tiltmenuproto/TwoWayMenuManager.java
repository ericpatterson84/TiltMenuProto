package com.example.epatterson.tiltmenuproto;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by epatterson on 10/16/2017.
 */

public class TwoWayMenuManager {

    private ArrayList<TwoWayMenuPath> menuPaths = null;
    private TwoWayMenuPath correctPath = null;
    private TwoWayMenuNode rootNode = null;
    private TwoWayMenuNode currentNode = null;
    private ArrayList<Integer> randomPathOrder = null;
    private boolean[] userPath = null;
    private int menuTreeDepth = 0;

    public TwoWayMenuManager(ArrayList<TwoWayMenuPath> paths, TwoWayMenuNode root) {
        menuPaths = paths;
        rootNode = root;
        randomPathOrder = new ArrayList<>();
    }

    public boolean determineNextMenuPath()
    {
        if(menuPaths.size() == 0 || rootNode == null)
        {
            return false;
        }

        randomizePaths();

        int pathIdx = randomPathOrder.get(0);
        randomPathOrder.remove(0);

        correctPath = menuPaths.get(pathIdx);
        userPath = new boolean[correctPath.getPathLength()];

        currentNode = rootNode;

        menuTreeDepth = -1;

        return true;
    }

    public String[] getNextMenuOptions()
    {
        String[] menuOptions = new String[2];

        menuOptions[0] = currentNode.getSubLabel(TwoWayMenuNode.SubNodeId.A);
        menuOptions[1] = currentNode.getSubLabel(TwoWayMenuNode.SubNodeId.B);

        menuTreeDepth++;

        return menuOptions;
    }

    private void randomizePaths()
    {
        randomPathOrder.clear();
        for(int i = 0; i < menuPaths.size(); i++) {
            randomPathOrder.add(i);
        }
        Collections.shuffle(randomPathOrder);
    }

    public boolean recordMenuPath(TwoWayMenuNode.SubNodeId id)
    {
        if(menuTreeDepth >= 0 && menuTreeDepth < userPath.length) {
            userPath[menuTreeDepth] = (id == TwoWayMenuNode.SubNodeId.A);
            if(currentNode.hasSubNodes()) {
                currentNode = currentNode.getSubNode(id);
                return true;
            }
            return false;
        }

        return false;
    }
}
