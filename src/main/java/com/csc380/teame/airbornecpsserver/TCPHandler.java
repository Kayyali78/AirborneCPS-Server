package com.csc380.teame.airbornecpsserver;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import static java.lang.Thread.sleep;

public class TCPHandler implements Runnable{

    protected enum Mode { normal, slower, slowest }
    private final static String helptext = "Syntax: java -jar tcpbeacons.jar (-server PORT? FILENAME | -client IP PORT FILENAME) slow? slow?";
    private Socket clientSocket = new Socket();

    static HashSet<String> readerBuf = new HashSet<String>();
    static HashSet<Plane> writerBuf = new HashSet<Plane>();
    // Object rlock = new Object(), wlock = new Object();

    public TCPHandler(Socket socket) throws FileNotFoundException {
        this.clientSocket = socket;
        // fillList();
    }

    private static synchronized void readtoBuffer(String str){
        synchronized (readerBuf){
            readerBuf.add(str);
        }
    }

    public static synchronized HashSet<String> getReaderBuffer(){
        synchronized (readerBuf){
            return readerBuf;
        }
    }
    public static void updateSenderBuffer(HashSet<Plane> in) {
        try {
            synchronized (writerBuf) {
                writerBuf = new HashSet<>(in);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // public List<Plane> getBuffer() {
    //     updateSenderBuffer((HashSet<Plane>) writerBuf);
    //     return writerBuf;
    // }

    public void write(Set<Plane> arr) {
        synchronized (writerBuf) {
            writerBuf.addAll(arr);
        }
        // System.out.println("Size of writerBuf is: "+writerBuf.size());
    }


    public void fillList() throws FileNotFoundException {
        readerBuf = new HashSet<>();
        writerBuf = new HashSet<>();
        HashSet<Plane> planes = new HashSet<>();
        String path = UDPHandler.class.getResource("northboundloop.txt").toString();
        path = path.substring(6, path.length());
        FileInputStream fileInput = new FileInputStream(path);
        Scanner s1 = new Scanner(fileInput);
        while (s1.hasNextLine()){
            String beacon = s1.nextLine();
            try {
                Plane p = new Plane(beacon);
                planes.add(p);
            } catch (Exception e) {
                Plane p = new Plane("n00:00:00:59:53:2En192.168.0.2n47.519961n10.698863n3050.078383");
                System.out.println("Beacon used is exception");
                planes.add(p);
            }
        }
        System.out.println("Size of list is: "+planes.size());
        s1.close();
        write(planes);
    }
    @Override
    public void run() {
        PrintWriter out = null;
        BufferedReader in = null;

        //get outputstream of client
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //get inputstream of client
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String line = "";
        long t1 = System.currentTimeMillis();
        int counter=0;
        while (true) {
            //timer base update
            
            try {
                if(System.currentTimeMillis() - t1 >= 1000){
                    synchronized(writerBuf){
                        for(Plane p:writerBuf){
                            //out.println(p.getBeacon());
                            System.out.println("Write to the client: " + p.getBeacon() + "\n");
                            out.println(p.getBeacon());
                            Thread.sleep(50);
                        }
                    }
                    Thread.sleep(10);
                    counter = 0;
                    t1 = System.currentTimeMillis();
                }
                else if (counter++ < 100 && (line = in.readLine()) != null ) {
                    //if socketbreaks, exception happen first before break.
                    System.out.println("Sent from the client: " + line + "\n");
                    readtoBuffer(line);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                break;
            }
            
            
            //out.println(line);
            
        }

    }

    public TCPHandler(String[] args){
        if (args.length == 0) System.out.println(helptext);
        else {
            switch (args[0]) {
                case "-server": {
                    if (args.length == 2) {
                        new Server().serve(args[1]);
                    } else if (args.length == 3) {
                        new Server(Integer.parseInt(args[1]), Mode.normal).serve(args[2]);
                    } else if (args.length == 4 && args[3].equals("slow")) {
                        new Server(Integer.parseInt(args[1]), Mode.slower).serve(args[2]);
                    } else if (args.length == 5  && args[3].equals("slow") && args[4].equals("slow")) {
                        new Server(Integer.parseInt(args[1]), Mode.slowest).serve(args[2]);
                    }
                    else {
                        System.out.println("Syntax error.\n   " + helptext);
                    }
                    break;
                }
                case "-client": {
                    if (args.length == 4) {
                        new Client(args[1], Integer.parseInt(args[2]), Mode.normal).connect(args[3]);
                    } else if (args.length == 5 && args[4].equals("slow")) {
                        new Client(args[1], Integer.parseInt(args[2]), Mode.slower).connect(args[3]);
                    } else if (args.length == 6 && args[4].equals("slow") && args[5].equals("slow")) {
                        new Client(args[1], Integer.parseInt(args[2]), Mode.slowest).connect(args[3]);
                    }  else {
                        System.out.println("Syntax error.\n   " + helptext);
                    }
                    break;
                }
                default:
                    System.out.println("Syntax error.\n   " + helptext);
                    break;
            }
        }
    }

    

    public static void main(String[] args){
        
    }

}


