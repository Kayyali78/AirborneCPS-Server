package com.csc380.teame.airbornecpsserver;

import java.util.HashSet;
import java.util.Objects;

import org.opensky.model.StateVector;

import static java.lang.Double.parseDouble;

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
    double lat, lon, alt, speed = 0;
    double heading = 0;
    boolean isADSB = false;
    boolean isTCP = false;
    String message = null;
    StateVector SV;
    // sample input
    // n00:00:00:59:53:2En192.168.0.2n47.519961n10.698863n3050.078383



    Plane(String beacon) {
        message = beacon;
        String[] arr = beacon.split("n");
        mac = arr[1];
        ip = arr[2];
        lat = parseDouble(arr[3]);
        lon = parseDouble(arr[4]);
        alt = parseDouble(arr[5]);
        speed = 0;
    }

    Plane(String beacon, boolean _isTCP) {
        message = beacon;
        String[] arr = beacon.split("n");
        mac = arr[1];
        ip = arr[2];
        lat = ParseDouble(arr[3]);
        lon = ParseDouble(arr[4]);
        alt = ParseDouble(arr[5]);
        speed = 0;
        isTCP = _isTCP;
    }

    Plane(StateVector sv) {
        this.SV = sv;
        mac = sv.getIcao24().trim();
        ip = sv.getCallsign().trim();
        lon = sv.getLongitude() == null ? 0 : sv.getLongitude();
        lat = sv.getLatitude() == null ? 0 : sv.getLatitude();
        alt = sv.getGeoAltitude() == null ? 0 : sv.getGeoAltitude();
        speed = sv.getVelocity() == null ? 0 : sv.getVelocity();
        heading = sv.getHeading() == null ? 0 : sv.getHeading();
        isADSB = true;
    }

    public static String getIP(String str){
        return str.split("n")[2];
    }

    @Override
    public String toString() {
        return (this.ip + " " + this.speed);
    }

    @Override
    public int hashCode() {
        return this.ip.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        Plane o = (Plane) obj;
        return Objects.equals(o.ip, this.ip);

    }

    double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch (Exception e) {
                return 0; // or some value to mark this field is wrong. or make a function validates field
                          // first ...
            }
        } else return 0;
    }

    public static HashSet<Plane> getExamplePlanes() {
        HashSet<Plane> result = new HashSet<Plane>();
        result.add(new Plane("n00:00:00:59:53:2En192.168.0.2n47.519961n10.698863n3050.078383"));
        // result.add(new
        // Plane("n4C:ED:FB:3C:D3:7bn192.168.2.50n47.496872n10.668766n3164.002499"));
        return result;
    }

    /**
     * @provides String in CPS becon format
     * @TODO: fix every ica024 or callsign with 'n' into 'N'
     */
    public String getBeacon() {
        return "n" + mac.replaceAll("n", "N") + "n" + ip.replaceAll("n", "N") + "n" + lat + "n" + lon + "n" + alt;
    }

    public static void main(String[] args) {
        Plane a = new Plane("n00:00:00:59:53:2En192.168.0.2n47.519961n10.698863n3050.078383");
    }
}
