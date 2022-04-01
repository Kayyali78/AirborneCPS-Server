package com.csc380.teame.airbornecpsserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPHandler {

    protected enum mode {normal, slower, slowest}
    private int port;
    private int delay;
    private DatagramSocket outSocket;
    private DatagramPacket datagramPacket;
    private final int MAX_RECEIVE_BUFFER_SIZE = 4096;


    public UDPHandler() {
        this(1901, mode.normal);
    }

    public UDPHandler(int port, mode mode) {
        this.port = port;
        switch (mode) {
            case normal:
                this.delay = 0;
                break;
            case slower:
                this.delay = 100;
                break;
            case slowest:
                this.delay = 500;
                break;
        }
    }
    public void serve() throws SocketException {
        try {
            outSocket = new DatagramSocket(port);
            while (true) {
                byte[] buf = new byte[MAX_RECEIVE_BUFFER_SIZE];
                datagramPacket = new DatagramPacket(buf, buf.length);
                outSocket.receive(datagramPacket);
                System.out.println("New client connected: " + datagramPacket.getAddress().getHostAddress());
                new Thread(() -> {
                    try {
                        byte[] bb = new byte[MAX_RECEIVE_BUFFER_SIZE];
                        DatagramPacket clientPack = new DatagramPacket(bb, bb.length);
                        outSocket.receive(clientPack);
                    } catch (IOException e) {
                        System.out.println("Error receiving client packet.");
                        e.printStackTrace();
                    }
                });
            }

        } catch (IOException e) {
            System.out.println("Error initializing server communication.");
            e.printStackTrace();
        }
    }
}
