package com.csc380.teame.airbornecpsserver;

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.net.DatagramSocket;
import java.util.ArrayList;

import com.dlsc.gmapsfx.javascript.object.LatLong;
import com.dlsc.gmapsfx.javascript.object.MVCArray;
import com.dlsc.gmapsfx.service.elevation.ElevationService;
import com.dlsc.gmapsfx.service.elevation.PathElevationRequest;
import com.dlsc.gmapsfx.shapes.Polyline;
import com.dlsc.gmapsfx.shapes.PolylineOptions;

import org.opensky.api.OpenSkyApi;
import org.opensky.api.OpenSkyApi.BoundingBox;
import org.opensky.model.OpenSkyStates;
import org.opensky.model.StateVector;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.LocalDateTime;

public class ServerController {
    DatagramSocket socket;
    ArrayList<Plane> ListTCP;
    ArrayList<Plane> ListUDP;
    ArrayList<Plane> ListADSB;
    static final String USERNAME = null;
    static final String PASSWORD = null;
    OpenSkyApi api = new OpenSkyApi(USERNAME, PASSWORD);
    BoundingBox bbox = new BoundingBox(30.8389, 50.8229, -100.9962, -40.5226);
    public static final Object openskyLock = new Object();
    UDPHandler udpHandler;
    Server server;

    ServerController() throws FileNotFoundException {
        ListUDP = new ArrayList<>();
        ListADSB = new ArrayList<>();
        ListTCP = new ArrayList<>();
        ListUDP = Plane.getExamplePlanes();
        udpHandler = new UDPHandler();
        server = new Server();
        try{
            new Thread(){
                @Override
                public void run() {
                    try{
                        udpHandler.serve();
                        server.serve();
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }
            }.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public ArrayList<Plane> getUDPList(){
        ArrayList<Plane> temp = new ArrayList<>();
        for(String s :udpHandler.getBuffer()){
            try{
                temp.add(new Plane(s));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        CPSFilter cpsFilter = new CPSFilter(ListUDP);
        ListUDP = cpsFilter.checkForDuplicates(temp);
        return ListUDP;
        //return ListUDP;
    }
    
    public ArrayList<Plane> getTCPList() {
        ArrayList<Plane> temp = new ArrayList<>();
        TCPHandler tcpHandler = server.returnTCPHandler();
        for(Plane s : tcpHandler.getBuffer()) {
            try {
                temp.add(s);
        } catch (Exception e) {
                e.printStackTrace();
            }
            CPSFilter cpsFilter = new CPSFilter(ListTCP);
            ListTCP = cpsFilter.checkForDuplicates(temp);
            return ListTCP;
    }
    
    public ArrayList<Plane> getADSBList() {
        return ListADSB;
    }
    public void getOpensky(){
        getOpensky(bbox);
    }
    public void getOpensky(BoundingBox bbox) {
        ArrayList<Plane> list = new ArrayList<>();
        long t1 = System.currentTimeMillis();
        synchronized(openskyLock){
            try{
                int count = 0;
                OpenSkyStates os = api.getStates(0, null, bbox);
                for(StateVector sv : os.getStates()){
                    if(count <= 100){
                        if(sv.getHeading() != null && sv.getHeading() != 0){
                            list.add(new Plane(sv));
                            count ++;
                        }
                    }else{
                        break;
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally {
                long t2 = System.currentTimeMillis();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                System.out.print(dtf.format(now));
                System.out.println(" ADSB update took: " + (t2-t1) + " ms" );
                if(list.size()>0){
                    this.ListADSB = list;
                }
            }
        }
        //return ListUDP;
    }
    public LatLong[] getHistoryOpensky(Plane p){
        if(p.isADSB == false){
            throw new IllegalStateException("Not an Opensky plane");
        }else{
            //get some LatLong[]
            long t = Instant.now().getEpochSecond();
            ArrayList<LatLong> l = new ArrayList<LatLong>();
            String[] s = {p.mac};
            for(int i = (int)t - 60*30; i <(int) t;i = (int)t + 60 ){
                try{
                    OpenSkyStates os = api.getStates((int)t, s, bbox);
                    for (StateVector sv : os.getStates()) {
                        if (sv.getIcao24() == p.mac) {
                            l.add(new LatLong(sv.getLatitude(), sv.getLongitude()));
                        }
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                
                
            }
            return (LatLong[])l.toArray();
        }
    }

    public ArrayList<Plane>getSomePlane(Plane p){
        ArrayList<Plane> arr= new ArrayList<>();
        if(p.isADSB == false){
            throw new IllegalStateException("Not an Opensky plane");
        }
        int t = (int) (Instant.now().getEpochSecond());
        String[] s = {p.mac};
        for(int i = (int)t - 60*242; i <= (int) t;i+= 60*30 ){
            synchronized(openskyLock){
                try{
                    Thread.sleep(5000);
                    t = (int) (Instant.now().getEpochSecond());
                    OpenSkyStates oss = api.getStates((i-i%5), s);
                    if(oss == null){
                        System.err.println("Request Limit reached:4900ms");
                    }
                    else if(oss.getStates() == null){
                        System.err.println("Get nothing");
                        continue;
                    }
                    for (StateVector sv : oss.getStates()) {
                        if (sv.getIcao24().compareToIgnoreCase(p.mac) == 0) {
                            arr.add(new Plane(sv));
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        arr.add(p);
        int t2 = (int) (Instant.now().getEpochSecond());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.print(dtf.format(now));
        System.out.println(Thread.currentThread().getName() +" ADSB gethistory took: " + (t2-t) + " ms" );
        return arr;
    }

    public static void main(String[] args){
        ServerController controller = new ServerController();
        
    }
}
