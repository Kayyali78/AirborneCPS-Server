package com.csc380.teame.airbornecpsserver;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    int port,delay;

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
