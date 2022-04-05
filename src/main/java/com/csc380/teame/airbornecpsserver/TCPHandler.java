package com.csc380.teame.airbornecpsserver;

import javafx.print.Printer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class TCPHandler {

    protected enum Mode { normal, slower, slowest }
    private final static String helptext = "Syntax: java -jar tcpbeacons.jar (-server PORT? FILENAME | -client IP PORT FILENAME) slow? slow?";
    int port, delay;

    List<String> readerBuf;
    List<Plane> writerBuf;
    Object rlock = new Object(), wlock = new Object();

    public TCPHandler() throws InterruptedException{
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            do {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());
                //reader threat for this client
                new Thread(() -> {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        while (true) {
                            synchronized (rlock) {
                                readerBuf.add(reader.readLine());
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                //writer thread for this client
                new Thread(() -> {
                    try {
                        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                        while (true) {
                            synchronized (wlock) {
                                while (writerBuf.size() > 0) {
                                    for (Plane p : writerBuf) {
                                        writer.write(p.getBeacon());
                                    }
                                }
                            }
                            Thread.sleep(10);
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
        } finally {
            Thread.sleep(500);
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
    }
}


