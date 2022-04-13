package com.csc380.teame.airbornecpsserver;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class TCPHandler implements Runnable{

    protected enum Mode { normal, slower, slowest }
    private final static String helptext = "Syntax: java -jar tcpbeacons.jar (-server PORT? FILENAME | -client IP PORT FILENAME) slow? slow?";
    private Socket clientSocket = new Socket();

    List<String> readerBuf;
    List<Plane> writerBuf;
    Object rlock = new Object(), wlock = new Object();

    public TCPHandler(Socket socket) throws FileNotFoundException {
        this.clientSocket = socket;
        fillList();
    }

    public void fillList() throws FileNotFoundException {
        readerBuf = new ArrayList<>();
        writerBuf = new ArrayList<>();
        ArrayList<Plane> planes = new ArrayList<>();

        FileInputStream fileInput = new FileInputStream("northboundloop.txt");
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

        while (true) {
            try {
                if ((line = in.readLine()) == null) break;
            } catch (IOException ex) {
                ex.printStackTrace();
            }


            //writing the received message from client
            System.out.println("Sent from the client: "+line+"\n");
            out.println(line);
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

    public void updateSenderBuffer(ArrayList<Plane> in){
        try{
            synchronized(wlock) {
                writerBuf = new ArrayList<>(in);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Plane> getBuffer() {
        updateSenderBuffer((ArrayList<Plane>) writerBuf);
        return writerBuf;
    }

    public void write(List<Plane> arr){
        synchronized (rlock){
            writerBuf.addAll(arr);
        }
        //System.out.println("Size of writerBuf is: "+writerBuf.size());
    }

}


