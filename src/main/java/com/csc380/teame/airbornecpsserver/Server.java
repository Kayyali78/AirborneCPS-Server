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
        ServerSocket server = null;

        try {

            // server is listening on port 1901
            server = new ServerSocket(1901);
            server.setReuseAddress(true);

            // running infinite loop for getting
            // client request
            while (true) {

                // socket object to receive incoming client
                // requests
                Socket client = server.accept();

                // Displaying that new client is connected
                // to server
                System.out.println("New client connected"
                        + client.getInetAddress()
                        .getHostAddress());

                // create a new thread object
                TCPHandler clientSock = new TCPHandler(client);

                // This thread will handle the client
                // separately
                new Thread((Runnable) clientSock).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (server != null) {
                try {
                    server.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
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

    public static void main(String[] args){
        Server server = new Server();
        try {
            server.serve();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
