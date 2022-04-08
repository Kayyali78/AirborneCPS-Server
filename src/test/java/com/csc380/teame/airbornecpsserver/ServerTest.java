package com.csc380.teame.airbornecpsserver;

import junit.framework.TestCase;

public class ServerTest extends TestCase {
String test1 = "java -jar tcpbeacons.jar -server";
String test2 = "java -jar tcpbeacons.jar -server 1901 northbound.txt slow";
String test3 = "java -jar tcpbeacons.jar -server 1901 southbound.txt slow slow";
String test4 = "java -jar tcpbeacons.jar -server 1901 northbound.txt";
String test5 = "java -jar tcpbeacons.jar -server 1901";

String test6 = "java -jar tcpbeacons.jar -client 127.0.0.1 1901 northbound.txt";
String test7 = "java -jar tcpbeacons.jar -client 127.0.0.1 1901 southbound.txt slow";
String test8 = "java -jar tcpbeacons.jar -client 127.0.0.1 1901 northbound.txt slow slow";

    public void testServe() {
        TCPHandler tcp1 = new TCPHandler(test3.split(" "));
        TCPHandler tcp2 = new TCPHandler(test7.split(" "));
        UDPHandler udp1 = new UDPHandler();
    }
}