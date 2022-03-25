package com.csc380.teame.airbornecpsserver;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TCPHandler {

    protected enum Mode { normal, slower, slowest }
    private final static String helptext = "Syntax: java -jar tcpbeacons.jar (-server PORT? FILENAME | -client IP PORT FILENAME) slow? slow?";

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

    public class Server {
        int port;
        int delay;

        public Server(){
            this(1901, TCPHandler.Mode.normal);
        }
        public Server(int port, TCPHandler.Mode mode){
            this.port = port;
            switch (mode) {
                case normal: this.delay = 0; break;
                case slower: this.delay = 100; break;
                case slowest: this.delay = 500; break;
            }
        }

        public void serve(String file){
                try (ServerSocket serverSocket = new ServerSocket(port)) {
                    System.out.println("Server is listening on port " + port);
                    while (true) {
                        Socket socket = serverSocket.accept();
                        System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());
                        //reader threat for this client
                        new Thread(() -> {
                            Scanner reader = null;
                            try {
                                reader = new Scanner(socket.getInputStream());
                                while (reader.hasNext()) {
                                    System.out.println(socket.getInetAddress().getHostAddress() + ": " + reader.nextLine());
                                }
                                System.out.println("Client " + socket.getInetAddress().getHostAddress() + " disconnected.");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).start();
                        //writer thread for this client
                        new Thread(() -> {
                            PrintWriter writer = null;
                            Scanner scanner = null;
                            try {
                                writer = new PrintWriter(socket.getOutputStream(), true);
                                while (true) {
                                    scanner = new Scanner(new File(file));
                                    while (scanner.hasNextLine()) {
                                        writer.println(scanner.nextLine());
                                        Thread.sleep(this.delay);
                                    }
                                    Thread.sleep(10);
                                }
                            } catch (IOException | InterruptedException e) {
                                System.out.println("Exception while writing to client: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }).start();
                    }
                } catch (IOException e) {
                    System.out.println("Server exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

    public class Client {
        private String ip;
        private int port;
        private int delay;

        public Client(){
            this("127.0.0.1",1901, Mode.normal);
        }

        public Client(String ip, int port, Mode mode) {
            this.ip = ip;
            this.port = port;
            switch (mode) {
                case normal: this.delay = 0; break;
                case slower: this.delay = 100; break;
                case slowest: this.delay = 500; break;
            }
        }

        public void connect (String file){
            try (Socket socket = new Socket(this.ip, this.port)) {
                System.out.println("Connected to " + socket.getInetAddress().getHostName() + " at " + socket.getInetAddress().getHostAddress() + " on port " + socket.getPort());
                Scanner reader = new Scanner(socket.getInputStream());
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                //writer thread
                new Thread(() -> {
                    Scanner scanner = null;
                    try {
                        while (true) {
                            scanner = new Scanner(new File(file));
                            while (scanner.hasNextLine()) {
                                writer.println(scanner.nextLine());
                                Thread.sleep(this.delay);
                            }
                            Thread.sleep(10);
                        }
                    } catch (IOException | InterruptedException e) {
                        System.out.println("Exception while writing to server: " + e.getMessage());
                        e.printStackTrace();
                    }
                }).start();
                //read using this thread
                while (reader.hasNext()) {
                    System.out.println(reader.nextLine());
                }
                System.out.println("Server closed the connection.");
            } catch (IOException e) {
                System.out.println("Client exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}


