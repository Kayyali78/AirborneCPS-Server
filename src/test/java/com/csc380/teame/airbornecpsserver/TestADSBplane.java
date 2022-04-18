package com.csc380.teame.airbornecpsserver;
import org.junit.Test;
import org.junit.Ignore;
//import org.opensky.api.OpenSkyApi;
//import org.opensky.api.OpenSkyApi.BoundingBox;
//import org.opensky.model.OpenSkyStates;
//import org.opensky.model.StateVector;

import java.io.IOException;
import static org.junit.Assert.*;
public class TestADSBplane {
    static final String USERNAME = null;
    static final String PASSWORD = null;
    @Test
    public void testsomthing(){
        System.out.println("good");
    }

//    @Test
//    public void getsomeplane() throws IOException,InterruptedException{
//        OpenSkyApi api = new OpenSkyApi(USERNAME, PASSWORD);
//        BoundingBox bbox = new BoundingBox(30.8389, 50.8229, -100.9962, -40.5226);
//        // now we can retrieve states again
//        long t0 = System.nanoTime();
//        OpenSkyStates os = api.getStates(0,null, bbox);
//        long t1 = System.nanoTime();
//        System.out.println("Request authStates time = " + ((t1 - t0) / 1000000) + "ms");
//        System.out.println("Request authInstances amount = " + os.getStates().size());
//        assertNotNull(os);
//        assertTrue("More than 1 state vector for second valid request", os.getStates().size() > 1);
//
//
//    }
}
