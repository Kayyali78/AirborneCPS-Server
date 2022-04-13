package com.csc380.teame.airbornecpsserver;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Client {
    private final String ip;
    private final int port;
    private int delay;
    Object rlock = new Object();

    public Client(){
        this("127.0.0.1",1901, TCPHandler.Mode.normal);
    }

    public Client(String ip, int port, TCPHandler.Mode mode) {
        this.ip = ip;
        this.port = port;
        switch (mode) {
            case normal -> this.delay = 0;
            case slower -> this.delay = 100;
            case slowest -> this.delay = 500;
        }
    }

    public void connect() {
        try (Socket socket = new Socket(this.ip, this.port)) {
            System.out.println("Connected to " + socket.getInetAddress().getHostName() + " at " +
                    socket.getInetAddress().getHostAddress() + " on port " + socket.getPort());
            // writing to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
                Scanner scanner = new Scanner(System.in);
                while (scanner.hasNextLine()) {
                    out.println(scanner.nextLine());
                    System.out.println(scanner.nextLine());
                    sleep(delay);
                }

                // reading from server
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // object of scanner class
                Scanner sc = new Scanner(System.in);
                String line = null;

                while (!"exit".equalsIgnoreCase(line)) {

                    // reading from user
                    line = sc.nextLine();

                    // sending the user input to server
                    out.println(line);
                    out.flush();

                    // displaying server reply
                    System.out.println("Server replied " + in.readLine());
                }

                // closing the scanner object
                sc.close();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void connect (String file){
        try (Socket socket = new Socket(this.ip, this.port)) {
            System.out.println("Connected to " + socket.getInetAddress().getHostName() + " at " + socket.getInetAddress().getHostAddress() + " on port " + socket.getPort());

            //BufferedReader reader = new BufferedReader();
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            //writer thread
            new Thread(() -> {
                Scanner scanner;
                try {
                    while (true) {
                        scanner = new Scanner(new File(file));
                        while (scanner.hasNextLine()) {
                            writer.println(scanner.nextLine());
                            System.out.println(scanner.nextLine());
                            sleep(this.delay);
                        }
                        sleep(10);
                        writer.flush();
                    }
                } catch (IOException | InterruptedException e) {
                    System.out.println("Exception while writing to server: " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();

            //read using this thread
            Socket finalSocket = socket;
            new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(finalSocket.getInputStream()));
                    while (true) {
                        synchronized (rlock) {
                            //readerBuf.add(reader.readLine());
                            System.out.println(reader.readLine());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            System.out.println("Server closed the connection.");
        } catch (IOException e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        Client c1 = new Client();
        File file = new File("southboundloop.txt");
        c1.connect();

    }
}

