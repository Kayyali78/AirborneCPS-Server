package com.csc380.teame.airbornecpsserver;

import java.util.ArrayList;
import java.util.Objects;

import org.opensky.model.StateVector;

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
    double heading = 0;
    boolean isADSB = false;
    String message = null;
    StateVector SV;
    //sample input
    //n00:00:00:59:53:2En192.168.0.2n47.519961n10.698863n3050.078383

    Plane(String beacon){
        message = beacon;
        String[] arr = beacon.split("n");
        mac = arr[1];
        ip = arr[2];
        lat = Double.parseDouble(arr[3]);
        lon = Double.parseDouble(arr[4]);
        alt = Double.parseDouble(arr[5]);
        speed = 0;
    }
    Plane(StateVector sv){
        this.SV = sv;
        mac = sv.getIcao24();
        ip = sv.getCallsign(); 
        lon = sv.getLongitude() == null ? 0 : sv.getLongitude();
        lat = sv.getLatitude() == null ? 0 : sv.getLatitude();
        alt = sv.getGeoAltitude() == null ? -1 : sv.getGeoAltitude();
        speed = sv.getVelocity() == null ? 0 : sv.getVelocity();
        heading = sv.getHeading() == null ? 0 : sv.getHeading();
        isADSB = true;
    }

    @Override
    public String toString() {
        return this.ip;
    }

    @Override
    public int hashCode(){
        return this.mac.hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if(obj == null || obj.getClass()!= this.getClass())
            return false;
        Plane o = (Plane)obj;
        return Objects.equals(o.mac, this.mac);

    }

    public static ArrayList<Plane> getExamplePlanes() {
        ArrayList<Plane> result = new ArrayList<Plane>();
        result.add(new Plane("n00:00:00:59:53:2En192.168.0.2n47.519961n10.698863n3050.078383"));
        //result.add(new Plane("n4C:ED:FB:3C:D3:7bn192.168.2.50n47.496872n10.668766n3164.002499"));
        return result;
    }   

    /**
     * @provides String in CPS becon format
     */
    public String getBeacon(){
        return "n" + mac + "n" + ip + lat + "n" + lon + "n" + alt;
    }

    public static void main(String[] args){
        Plane a = new Plane("n00:00:00:59:53:2En192.168.0.2n47.519961n10.698863n3050.078383");
    }
}
