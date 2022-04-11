package com.csc380.teame.airbornecpsserver;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class EchoClient{
    private final String ip;
    private final int port;
    private int delay;

    public EchoClient(){
        this("127.0.0.1",1901, TCPHandler.Mode.normal);
    }

    public EchoClient(String ip, int port, TCPHandler.Mode mode) {
        this.ip = ip;
        this.port = port;
        switch (mode) {
            case normal -> this.delay = 0;
            case slower -> this.delay = 100;
            case slowest -> this.delay = 500;
        }
    }

    public void connect(File filename){

        try (Socket socket = new Socket(ip, port)) {
            System.out.println("Connected to " + socket.getInetAddress().getHostName() + " at " + socket.getInetAddress().getHostAddress() + " on port " + socket.getPort());
            Scanner reader = new Scanner(socket.getInputStream());
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            //writer thread
            new Thread(() -> {
                Scanner scanner;
                try {
                    while (true) {
                        scanner = new Scanner(filename);
                        while (scanner.hasNextLine()) {
                            writer.println(scanner.nextLine());
                            sleep(delay);
                        }
                        sleep(10);
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

    public static void main(String[] args){
        EchoClient c1 = new EchoClient("127.0.0.1",1901, TCPHandler.Mode.normal);
        File file = new File("southboundloop.txt");
        c1.connect(file);
    }
}

