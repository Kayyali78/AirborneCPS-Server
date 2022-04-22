package com.csc380.teame.airbornecpsserver;

import java.util.HashSet;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Apply filters to plane list to only planes that are part of the current
 * forwarding rules are being passed to the user, as well as ensuring we're
 * only displaying each plane once.
 */

public class CPSFilter {

    HashSet<Plane> list;
    HashSet<Plane> hashList = new HashSet<>();
    HashMap<Plane, Plane> Hmap = new HashMap<>();
    public CPSFilter(HashSet<Plane> currentList) {
        this.list = currentList;
        this.Hmap = new HashMap<>();
        for(Plane plane : currentList){
            Hmap.put(plane, plane);
        }
    }

    public HashSet<Plane> checkDups(HashSet<Plane> ArrivalList){

        //1. dump everything new into hashmap: check dups instantly.
        //By default, the last to trasverse will be the "Newest One";
        //HashSet<Plane> temp = new HashSet<>(ArrivalList);
        HashMap<Plane, Plane> temp = new HashMap<>();
        for(Plane p : ArrivalList){
            temp.put(p,p);
        }
        //2. for every old plane, calculate the new heading angle if exist and replace.
        for(Plane p: temp.values()){
            if(Hmap.get(p) != null){
                double x = p.lat - Hmap.get(p).lat;
                double y = p.lon - Hmap.get(p).lon;
                double heading = Math.toDegrees(Math.atan2(y,x));
                Plane a = temp.get(p);
                if(heading < 0.0) heading += 360.0;
                a.heading = heading;
                Hmap.put(a, a);
            }else{
                Hmap.put(p,p);
            }
        }
        return new HashSet<Plane>(Hmap.values());
    }

    //check current list for duplicate entries
//    public HashSet<Plane> checkForDuplicates(HashSet<Plane> currentList) {
//        HashSet<Plane> newList = new HashSet<>();
//        for (int i = 0; i < currentList.size(); i++) {
//            Plane currentPlane = currentList.get(i);
//            String currentMacAddr = currentPlane.mac;
//            if (newList.size() == 0) {
//                newList.add(currentPlane);
//            }
//            for (int j = 0; j < newList.size(); j++) {
//                Plane newPlane = newList.get(j);
//                String newPlaneMac = newPlane.mac;
//                if (!currentMacAddr.equalsIgnoreCase(newPlaneMac)) {
//                    newList.add(currentPlane);
//                }
//            }
//        }
//        return newList;
//    }

    //only return planes within a certain distance from current player
    public HashSet<Plane> getLocalPlaneList(HashSet<Plane> currentList) {
        return currentList;
    }
}
