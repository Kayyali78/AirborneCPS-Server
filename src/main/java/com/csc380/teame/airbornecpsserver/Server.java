package com.csc380.teame.airbornecpsserver;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusData;

import static java.lang.Thread.sleep;

public class Server {
    int port,delay;
    List<String> readerBuf;
    List<Plane> writerBuf;
    Object rlock = new Object(), wlock = new Object();
    TCPHandler clientSock;
    private static final Logger logger = LogManager.getLogger(Server.class);
    public void setTCPList(HashSet<Plane>list){
        TCPHandler.updateSenderBuffer(list);
    }

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
            server = new ServerSocket(19010);
            logger.info("Server started {}",server);
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
                clientSock = new TCPHandler(client);

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

    public void serve(String addr,int port) throws InterruptedException {
        ServerSocket server = null;

        try {
            // server is listening on port 1901
            server = new ServerSocket(port,50, InetAddress.getByName(addr));
            logger.info("Server started {}",server);
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
                clientSock = new TCPHandler(client);

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

    public TCPHandler returnTCPHandler(){
        return clientSock;
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
