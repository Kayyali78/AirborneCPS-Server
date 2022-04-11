package com.csc380.teame.airbornecpsserver;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TCPHandlerTest extends TestCase {
    @Test
    public void testBlankTCP() throws InterruptedException {

        TCPHandler tcpHandler = new TCPHandler();
        tcpHandler.serve();
    }

    public void testGetTCPBeacon() {

    }

    public void testWrite() {

    }
}