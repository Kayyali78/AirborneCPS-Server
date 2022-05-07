package com.csc380.teame.airbornecpsserver;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TCPHandlerTest extends TestCase {

    public void testBlankTCP() throws InterruptedException, IOException {
            //TCPHandler tcpHandler = new TCPHandler();

            Client client = new Client();
            client.connect("southboundloop.txt");
            //TCPHandler tcpHandler1 = new TCPHandler();
            //TCPHandler tcpHandler2 = new TCPHandler("java -jar tcpbeacons.jar -client 127.0.0.1 1901 southbound.txt slow".split(" "));
            Client client1 = new Client();
            client1.connect();
    }

    public void testGetTCPBeacon() {

    }

    public void testWrite() {

    }
}