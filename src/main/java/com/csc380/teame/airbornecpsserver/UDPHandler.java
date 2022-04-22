package com.csc380.teame.airbornecpsserver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Scanner;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusData;
public class UDPHandler {
    public static final Object recvlock = new Object();
    public static final Object sendlock = new Object();

    protected enum mode {
        normal, slower, slowest
    }

    private int port;
    public int delay = 10;
    private DatagramSocket inSocket;
    private DatagramSocket outSocket;
    // inSocket buf.
    private DatagramPacket datagramPacket;
    private final int MAX_RECEIVE_BUFFER_SIZE = 4096;

    Thread readerThread = null;
    Thread writerThread = null;

    HashSet<String> receivedBuffer = new HashSet<String>();
    HashSet<Plane> senderBuffer = new HashSet<Plane>();
    private static final Logger logger = LogManager.getLogger(UDPHandler.class);
    public UDPHandler() {
        this(21221, mode.normal);
    }

    public void fillList() throws FileNotFoundException {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        String path = UDPHandler.class.getResource("southboundloop.txt").toString();
        path = path.substring(6, path.length());
        FileInputStream fileInput = new FileInputStream(path);
        Scanner s1 = new Scanner(fileInput);
        HashSet<Plane> planes = new HashSet<>();
        while (s1.hasNextLine()) {
            String beacon = s1.nextLine();
            try {
                Plane p = new Plane(beacon);
                planes.add(p);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        synchronized (senderBuffer) {
            senderBuffer = new HashSet<>(planes);
        }
    }

    public synchronized HashSet<String> getBuffer() {
        HashSet<String> buffer = new HashSet<String>();
        synchronized (receivedBuffer) {
            buffer = new HashSet<String>(receivedBuffer);
            logger.info("rbuffer has " + receivedBuffer.size());
            receivedBuffer.clear();
            logger.info("cbuffer has " + buffer.size());
        }
        return buffer;
    }

    public synchronized void updateSenderBuffer(HashSet<Plane> in) {
        try {
            synchronized (senderBuffer) {
                senderBuffer = new HashSet<>(in);
            }
            logger.info("UDPSenderBuffer Updated");
        } catch (Exception e) {
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
        createSocket(port, "0.0.0.0");
    }

    public void close() {
        //this.readerThread.interrupt();
        this.readerThread.stop();
        this.writerThread.stop();
        this.inSocket.close();
        this.outSocket.close();
    }

    public void closeReaderThread() {
        this.readerThread.interrupt();
    }

    public UDPHandler(int port, mode mode, String host) {
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
        createSocket(port,host);
    }
    public void createSocket(int port, String host){
        try {
            inSocket = new DatagramSocket(null);
            inSocket.setReuseAddress(true);
            inSocket.setBroadcast(true);
            inSocket.bind(new InetSocketAddress(host, port));
            outSocket = new DatagramSocket();
            outSocket.setReuseAddress(true);
            outSocket.setBroadcast(true);
            System.out.println(outSocket.getLocalPort());

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void resetSocket(int port){
        this.close();
        this.createSocket(port,"0.0.0.0");
    }

    public void renewThread(){
        readerThread = new Thread() {
            // Receiver.
            @Override
            public void run() {
                try {
                    while (true) {
                        byte[] buf = new byte[MAX_RECEIVE_BUFFER_SIZE];
                        datagramPacket = new DatagramPacket(buf, buf.length);
                        inSocket.receive(datagramPacket);
                        // System.out.println("New client connected: " +
                        // datagramPacket.getAddress().getHostAddress());
                        ByteBuffer bb = ByteBuffer.wrap(buf);
                        String data = StandardCharsets.UTF_8.decode(bb).toString();
                        synchronized (receivedBuffer) {
                            receivedBuffer.add(data);
                        }
                        // System.out.println(data);
                        // new Thread(() -> {
                        // try {
                        // byte[] bb = new byte[MAX_RECEIVE_BUFFER_SIZE];
                        // DatagramPacket clientPack = new DatagramPacket(bb, bb.length);
                        // inSocket.receive(clientPack);
                        // } catch (IOException e) {
                        // System.out.println("Error receiving client packet.");
                        // e.printStackTrace();
                        // }
                        // });
                    }

                } catch (Exception e) {
                    System.out.println("Error initializing server communication.");
                    e.printStackTrace();
                } finally {
                    close();
                }
            }
        };
        writerThread = new Thread() {
            // sender
            @Override
            public void run() {
                try {
                    while (true) {
                        try {
                            synchronized (senderBuffer) {
                                for (Plane p : senderBuffer) {
                                    if(getName().equals("UDPInjector")){
                                        logger.info("UDP Inject : {}", p.getBeacon());
                                    }else{
                                        logger.debug("UDP broad : {}",p.getBeacon());
                                    }
                        
                                    byte[] buf = p.getBeacon().getBytes(StandardCharsets.UTF_8);
                                    
                                    DatagramPacket sendpkt = new DatagramPacket(buf, buf.length,
                                            InetAddress.getByName("255.255.255.255"), 21221);
                                    outSocket.send(sendpkt);
                                    Thread.sleep(delay);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("ThreadInterrupted");
                } finally {
                    close();
                }
            }
        };
    }

    public void serve(){
        readerThread.setName("UDPReader");
        readerThread.start();
        writerThread.setName("UDPWriter");
        writerThread.start();
    }

    public void serveWriterThread(){
        writerThread.setName("UDPInjector");
        writerThread.start();
    }

    public static void main(String[] args) {
        UDPHandler uh = new UDPHandler();
        try {
            uh.fillList();
            uh.serve();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
