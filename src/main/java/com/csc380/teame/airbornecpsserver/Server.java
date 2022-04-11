package com.csc380.teame.airbornecpsserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Server {
    int port,delay;
    List<String> readerBuf;
    List<Plane> writerBuf;
    Object rlock = new Object(), wlock = new Object();

    public Server(){
        this(1901, TCPHandler.Mode.normal);
    }

    public Server(int port, TCPHandler.Mode mode){
        this.port = port;
        switch (mode) {
            case normal -> this.delay = 0;
            case slower -> this.delay = 100;
            case slowest -> this.delay = 500;
        }
    }

    public void serve() throws InterruptedException {
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
                Socket finalSocket1 = socket;
                new Thread(() -> {
                    try {
                        PrintWriter writer = new PrintWriter(finalSocket1.getOutputStream(), true);
                        while (true) {
                            synchronized (wlock) {
                                while (writerBuf.size() > 0) {
                                    for (Plane p : writerBuf) {
                                        writer.write(p.getBeacon());
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
        } finally {
            sleep(500);
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
                    Scanner reader;
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
                    PrintWriter writer;
                    Scanner scanner;
                    try {
                        writer = new PrintWriter(socket.getOutputStream(), true);
                        while (true) {
                            scanner = new Scanner(new File(file));
                            while (scanner.hasNextLine()) {
                                writer.println(scanner.nextLine());
                                sleep(this.delay);
                            }
                            sleep(10);
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
