package com.csc380.teame.airbornecpsserver;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private String ip;
    private int port;
    private int delay;

    public Client(){
        this("127.0.0.1",1901, TCPHandler.Mode.normal);
    }

    public Client(String ip, int port, TCPHandler.Mode mode) {
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

