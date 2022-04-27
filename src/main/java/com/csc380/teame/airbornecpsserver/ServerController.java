package com.csc380.teame.airbornecpsserver;

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.net.DatagramSocket;
import java.util.HashSet;
import java.util.regex.*;

import com.dlsc.gmapsfx.javascript.object.LatLong;
import com.dlsc.gmapsfx.javascript.object.MVCArray;
import com.dlsc.gmapsfx.service.elevation.ElevationService;
import com.dlsc.gmapsfx.service.elevation.PathElevationRequest;
import com.dlsc.gmapsfx.shapes.Polyline;
import com.dlsc.gmapsfx.shapes.PolylineOptions;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusData;

import org.opensky.api.OpenSkyApi;
import org.opensky.api.OpenSkyApi.BoundingBox;
import org.opensky.model.OpenSkyStates;
import org.opensky.model.StateVector;

import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.LocalDateTime;

public class ServerController {
    DatagramSocket socket;
    HashSet<Plane> ListTCP;
    HashSet<Plane> ListUDP;
    HashSet<Plane> ListADSB;
    static final String USERNAME = null;
    static final String PASSWORD = null;
    OpenSkyApi api = new OpenSkyApi(USERNAME, PASSWORD);
    BoundingBox bbox = new BoundingBox(30.8389, 50.8229, -100.9962, -40.5226);
    public static final Object openskyLock = new Object();
    UDPHandler udpHandler;
    Server server;
    public String zeroTo255 = "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])";
    public String IPregex = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;
    Pattern IPPattern = Pattern.compile(IPregex);
    private static final Logger logger = LogManager.getLogger(ServerController.class);
    ServerController(){
        ListUDP = new HashSet<>();
        ListADSB = new HashSet<>();
        ListTCP = new HashSet<>();
        ListUDP = new HashSet<>();
        udpHandler = new UDPHandler();
        server = new Server();

    }

    public void startHandler(String addr,int udpport,int tcpport){
        try {
            new Thread() {
                @Override
                public void run() {
                    try {
                        udpHandler.createSocket(udpport,addr);
                        udpHandler.renewThread();
                        udpHandler.serve();
                        server.serve("0.0.0.0",tcpport);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public HashSet<Plane> getUDPList() {
        HashSet<Plane> temp = new HashSet<>();
        HashSet<String> udpBuf = udpHandler.getBuffer();
        for (String s : udpBuf) {
            try {
                //double check if ip field is valid
                String ip = s.split("n")[2];
                //Matcher m = IPPattern.matcher(ip);
                if(IPPattern.matcher(ip).matches()){
                    temp.add(new Plane(s));
                    logger.debug(s);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        CPSFilter cpsFilter = new CPSFilter(ListUDP);
        ListUDP = cpsFilter.checkDups(temp);
        /**
         * @TODO Fix Issue #14
         */
        ListUDP.removeAll(this.getTCPwoFilter());
        return ListUDP;
        //return ListUDP;
    }

    public void setUDPList(HashSet<Plane> list) {
        this.udpHandler.updateSenderBuffer(list);
    }

    public void setTCPList(HashSet<Plane> list){
        this.server.setTCPList(list);
    }
    
    public HashSet<Plane> getUDPwoFilter(){
        return ListUDP;
    }

    public HashSet<Plane> getTCPwoFilter(){
        return ListTCP;
    }

    public HashSet<Plane> getTCPList() {
        try{
            HashSet<Plane> temp = new HashSet<>();
            //TCPHandler tcpHandler = server.returnTCPHandler();
            HashSet<String> tcptemp = TCPHandler.getReaderBuffer();
            for (String s : tcptemp) {
                try {
                    temp.add(new Plane(s));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            CPSFilter cpsFilter = new CPSFilter(ListTCP);
            ListTCP = cpsFilter.checkDups(temp);
            return ListTCP;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new HashSet<Plane>();

    }

    public HashSet<Plane> getADSBList() {
        return ListADSB;
    }

    public void getOpensky() {
        getOpensky(bbox);
    }

    public void getOpensky(BoundingBox bbox) {
        HashSet<Plane> list = new HashSet<>();
        long t1 = System.currentTimeMillis();
        synchronized (openskyLock) {
            try {
                int count = 0;
                OpenSkyStates os = api.getStates(0, null, bbox);
                for (StateVector sv : os.getStates()) {
                    if (count <= 50) {
                        try{
                            if (sv.getHeading() != null && sv.getHeading() != 0 && sv.getVelocity() >= 10 && !(sv.getIcao24() == null || sv.getIcao24().length() == 0) && !(sv.getCallsign() == null || sv.getCallsign().length() == 0)) {
                                list.add(new Plane(sv));
                                count++;
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                long t2 = System.currentTimeMillis();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                //System.out.print(dtf.format(now));
                logger.info("ADSB update took: " + (t2 - t1) + " ms " + bbox.getMinLatitude() + "°N " + bbox.getMaxLatitude()+ "°N " +bbox.getMinLongitude()+ "°E " +bbox.getMaxLongitude() + "°E");
                if (list.size() > 0) {
                    this.ListADSB = list;
                }
            }
        }
        //return ListUDP;
    }

    public LatLong[] getHistoryOpensky(Plane p) {
        if (p.isADSB == false) {
            throw new IllegalStateException("Not an Opensky plane");
        } else {
            //get some LatLong[]
            long t = Instant.now().getEpochSecond();
            HashSet<LatLong> l = new HashSet<LatLong>();
            String[] s = {p.mac};
            for (int i = (int) t - 60 * 30; i < (int) t; i = (int) t + 60) {
                try {
                    OpenSkyStates os = api.getStates((int) t, s, bbox);
                    for (StateVector sv : os.getStates()) {
                        if (sv.getIcao24() == p.mac) {
                            l.add(new LatLong(sv.getLatitude(), sv.getLongitude()));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
            return (LatLong[]) l.toArray();
        }
    }

    public HashSet<Plane> getSomePlane(Plane p) {
        HashSet<Plane> arr = new HashSet<>();
        if (p.isADSB == false) {
            throw new IllegalStateException("Not an Opensky plane");
        }
        int t = (int) (Instant.now().getEpochSecond());
        String[] s = {p.mac};
        for (int i = (int) t - 60 * 242; i <= (int) t; i += 60 * 30) {
            synchronized (openskyLock) {
                try {
                    Thread.sleep(5000);
                    t = (int) (Instant.now().getEpochSecond());
                    OpenSkyStates oss = api.getStates((i - i % 5), s);
                    if (oss == null) {
                        System.err.println("Request Limit reached:4900ms");
                    } else if (oss.getStates() == null) {
                        System.err.println("Get nothing");
                        continue;
                    }
                    for (StateVector sv : oss.getStates()) {
                        if (sv.getIcao24().compareToIgnoreCase(p.mac) == 0) {
                            arr.add(new Plane(sv));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        arr.add(p);
        int t2 = (int) (Instant.now().getEpochSecond());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.print(dtf.format(now));
        logger.info(Thread.currentThread().getName() + " ADSB gethistory took: " + (t2 - t) + " ms");
        return arr;
    }

    public static void main(String[] args) {
        try {
            ServerController controller = new ServerController();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

