package com.csc380.teame.airbornecpsserver;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class TCPHandler {

    protected enum Mode { normal, slower, slowest }
    private final static String helptext = "Syntax: java -jar tcpbeacons.jar (-server PORT? FILENAME | -client IP PORT FILENAME) slow? slow?";
    int port, delay;

    List<String> readerBuf;
    List<Plane> writerBuf;
    Object rlock = new Object(), wlock = new Object();

    public TCPHandler() throws FileNotFoundException {
        this.port = 1901;
        this.delay = 0;
        fillList();
    }

    public void fillList() throws FileNotFoundException {
        readerBuf = new ArrayList<>();
        writerBuf = new ArrayList<>();
        ArrayList<Plane> planes = new ArrayList<>();

        FileInputStream fileInput = new FileInputStream("C:\\Users\\scarl\\projects\\AirborneCPS-Server\\northboundloop.txt");
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

    public void serve() throws InterruptedException {
        port = 1901;
        Socket socket;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            do {
                socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());

                //reader threat for this client

                Socket finalSocket = socket;
                new Thread(() -> {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(finalSocket.getInputStream()));

                        // TODO: 4/13/2022 changed while(true) to (reader.readLine() != null) to prevent infinite loop over null values when stream empties-line 69

                        while (reader.readLine() != null) {
                            synchronized (rlock) {
                                //readerBuf.add(reader.readLine());
                                System.out.println(reader.readLine());
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                //writer thread for this client
                Socket finalSocket1 = socket;
                new Thread(() -> {
                    try {
                        PrintWriter writer = new PrintWriter(finalSocket1.getOutputStream(), true);
                        while (true) {
                            synchronized (wlock) {
                                while (writerBuf.size() > 0) {
                                    for (Plane p : writerBuf) {
                                        writer.write(p.getBeacon());
                                        //System.out.println(p.getBeacon());
                                    }
                                }
                            }
                            sleep(10);
                        }
                    } catch (IOException | InterruptedException e) {
                        System.out.println("Exception while writing to client: " + e.getMessage());
                        e.printStackTrace();
                    }
                }).start();

            } while (true);
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
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

    public List<String> getTCPBeacon() {
        List arr;
        synchronized (rlock) {
            arr = List.copyOf(readerBuf);
        }
        return arr;
    }

    public void write(List<Plane> arr){
        synchronized (rlock){
            writerBuf.addAll(arr);
        }
        System.out.println("Size of writerBuf is: "+writerBuf.size());
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        TCPHandler tcp = new TCPHandler();

    }
}


