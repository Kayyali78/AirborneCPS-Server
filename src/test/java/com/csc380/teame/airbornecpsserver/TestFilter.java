package com.csc380.teame.airbornecpsserver;

import org.junit.Test;

import junit.framework.TestCase;
import static org.junit.Assert.*;
import org.junit.Ignore;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class TestFilter {

    @Test
    public void dupPlanes() throws Exception {

        try {
            URL classpath = TestFilter.class.getResource("northboundloop.txt");
            ArrayList<Plane> TestPlanes = new ArrayList<>();
            File file = new File(classpath.toURI());
            String absolutePath = file.getAbsolutePath();
            int count = 0;
            try (Scanner in = new Scanner(file)) {
                while (in.hasNextLine() && count++ < 10) {
                    TestPlanes.add(new Plane(in.nextLine()));
                }
                CPSFilter udpf = new CPSFilter(TestPlanes);
                TestPlanes.clear();
                count = 0;
                while (in.hasNextLine() && count++ < 10) {
                    TestPlanes.add(new Plane(in.nextLine()));
                }
                ArrayList<Plane> newList = udpf.checkDups(TestPlanes);
                assertTrue(TestPlanes.size() == 1);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // Then init cpsfilter

        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // System.out.println(loader.getResource("northboundloop.txt"));

    }

    @Test
    public void filter_test() {
        Integer a = null;
        a = new Integer(0);
        assertNotNull(a);
    }

}
