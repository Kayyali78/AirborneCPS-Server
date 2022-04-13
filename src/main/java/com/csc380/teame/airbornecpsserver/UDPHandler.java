package com.csc380.teame.airbornecpsserver;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class UDPHandler {
    public static final Object recvlock = new Object();
    public static final Object sendlock = new Object();
    protected enum mode {normal, slower, slowest}
    private int port;
    private int delay;
    private DatagramSocket inSocket;
    private DatagramSocket outSocket;
    //inSocket buf.
    private DatagramPacket datagramPacket;
    private final int MAX_RECEIVE_BUFFER_SIZE = 4096;

    ArrayList<String> receivedBuffer = new ArrayList<String>();
    ArrayList<Plane> senderBuffer = new ArrayList<Plane>();

    public UDPHandler() {
        this(21221, mode.normal);
    }

    public ArrayList<String> getBuffer(){
        ArrayList<String> buffer = new ArrayList<String>();
        synchronized(recvlock){
            buffer = new ArrayList<String>(receivedBuffer);
            System.out.println("UDPHandler: rbuffer has " + receivedBuffer.size());
            receivedBuffer.clear();
            System.out.println("UDPHandler: cbuffer has " + buffer.size());
        }
        return buffer;
    }
    public void updateSenderBuffer(ArrayList<Plane> in){
        try{
            synchronized(sendlock){
                senderBuffer = new ArrayList<>(in);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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
        try{
            inSocket = new DatagramSocket(null);
            inSocket.setReuseAddress(true);
            inSocket.setBroadcast(true);
            inSocket.bind(new InetSocketAddress("0.0.0.0",21221));
            outSocket = new DatagramSocket();
            outSocket.setReuseAddress(true);
            outSocket.setBroadcast(true);

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    public void serve() throws SocketException {
        new Thread(){
            //Receiver.
            @Override
            public void run() {
                try {
                    while (true) {
                        byte[] buf = new byte[MAX_RECEIVE_BUFFER_SIZE];
                        datagramPacket = new DatagramPacket(buf, buf.length);
                        inSocket.receive(datagramPacket);
                        //System.out.println("New client connected: " + datagramPacket.getAddress().getHostAddress());
                        ByteBuffer bb = ByteBuffer.wrap(buf);
                        String data = StandardCharsets.UTF_8.decode(bb).toString();
                        synchronized(recvlock){
                            receivedBuffer.add(data);
                        }
                        //System.out.println(data);
                        // new Thread(() -> {
                        //     try {
                        //         byte[] bb = new byte[MAX_RECEIVE_BUFFER_SIZE];
                        //         DatagramPacket clientPack = new DatagramPacket(bb, bb.length);
                        //         inSocket.receive(clientPack);
                        //     } catch (IOException e) {
                        //         System.out.println("Error receiving client packet.");
                        //         e.printStackTrace();
                        //     }
                        // });
                    }

                } catch (IOException e) {
                    System.out.println("Error initializing server communication.");
                    e.printStackTrace();
                }
            }
        }.start();
        new Thread(){
            //sender
            @Override
            public void run() {
                while(true){
                    try{
                        synchronized(sendlock){
                            for(Plane p : senderBuffer){
                                byte[] buf = p.toString().getBytes(StandardCharsets.UTF_8);
                                DatagramPacket sendpkt = new DatagramPacket(buf,buf.length, InetAddress.getByName("255.255.255.255"),21221);
                                outSocket.send(sendpkt);
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally{
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }
    public static void main(String[] args){
        UDPHandler uh = new UDPHandler();
        try {
            uh.serve();
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
