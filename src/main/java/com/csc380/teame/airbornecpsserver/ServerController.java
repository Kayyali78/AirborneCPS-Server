package com.csc380.teame.airbornecpsserver;

import java.net.DatagramSocket;
import java.util.ArrayList;
import org.opensky.api.OpenSkyApi;
import org.opensky.api.OpenSkyApi.BoundingBox;
import org.opensky.model.OpenSkyStates;
import org.opensky.model.StateVector;
import java.time.format.DateTimeFormatter;
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
    ServerController(){
        ListUDP = new ArrayList<>();
        ListADSB = new ArrayList<>();
        ListTCP = new ArrayList<>();
        ListUDP = Plane.getExamplePlanes();
    }
    
    public ArrayList<Plane> getUDPList(){
        return ListUDP;
    }
    
    public ArrayList<Plane> getTCPList() {
        return ListUDP;
    }
    
    public ArrayList<Plane> getADSBList() {
        return ListADSB;
    }
    public void getOpensky(){
        getOpensky(bbox);
    }
    public void getOpensky(BoundingBox bbox) {
        try{
            long t1 = System.currentTimeMillis();
            ArrayList<Plane> list = new ArrayList<>();
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
            long t2 = System.currentTimeMillis();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            System.out.print(dtf.format(now));
            System.out.println(" ADSB update took: " + (t2-t1) + " ms" );
            if(list.size()>0){
                this.ListADSB = list;
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        
        //return ListUDP;
    }

    public static void main(String[] args){
        ServerController controller = new ServerController();
        
    }
}
