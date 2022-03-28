package com.csc380.teame.airbornecpsserver;

import java.util.ArrayList;

/**
 * Apply filters to plane list to only planes that are part of the current
 * forwarding rules are being passed to the user, as well as ensuring we're
 * only displaying each plane once.
 */

public class CPSFilter {

    ArrayList<Plane> list;

    public CPSFilter(ArrayList<Plane> currentList) {
        this.list = currentList;
    }

    //check current list for duplicate entries
    public ArrayList<Plane> checkForDuplicates(ArrayList<Plane> currentList) {
        ArrayList<Plane> newList = new ArrayList<>();
        for (int i = 0; i < currentList.size(); i++) {
            Plane currentPlane = currentList.get(i);
            String currentMacAddr = currentPlane.mac;
            if (newList.size() == 0) {
                newList.add(currentPlane);
            }
            for (int j = 0; j < newList.size(); j++) {
                Plane newPlane = newList.get(j);
                String newPlaneMac = newPlane.mac;
                if (!currentMacAddr.equalsIgnoreCase(newPlaneMac)) {
                    newList.add(currentPlane);
                }
            }
        }
        return newList;
    }
}
