package com.csc380.teame.airbornecpsserver;

/**
 * A Plane Class
 * Members:
 * <blockquote>
 * 
 * <pre>
 *String mac
 *String IP
 *double lat,lon,alt
 * </pre>
 * 
 * </blockquote>
 * <p>
 * Constructors:
 * 
 * <pre>
 *          
 *Plane(String UDPBeacon)
 *Plane(String ADSB)
 * 
 * </pre>
 */
public class Plane {
    
    String mac;
    String ip;
    double lat,lon,alt,speed = 0;
    String message;
    //sample input
    //n00:00:00:59:53:2En192.168.0.2n47.519961n10.698863n3050.078383

    Plane(String beacon){
        message = beacon;
        String[] arr = beacon.split("n");
    }

    /**
     * @provides string in CPS becon format
     */
    public String getBeacon(){
        return "n" + mac + "n" + ip + lat + "n" + lon + "n" + alt;
    }
}
