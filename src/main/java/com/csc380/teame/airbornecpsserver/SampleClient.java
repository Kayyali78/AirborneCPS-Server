package com.csc380.teame.airbornecpsserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import java.io.*;
import java.net.*;
import java.util.*;

import static java.lang.Thread.sleep;

// Client class
class SampleClient {

    private static long delay = 10;

    public SampleClient(){
        try (Socket socket = new Socket("localhost", 1901)) {

            // writing to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
                String path = UDPHandler.class.getResource("northboundloop.txt").toString();
                path = path.substring(6, path.length());
                Scanner scanner = new Scanner(new File(path));
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
                    System.out.println("Server replied "
                            + in.readLine());
                }

                // closing the scanner object
                sc.close();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // driver code
    public static void main(String[] args) {
        // establish a connection by providing host and port
        // number
        try (Socket socket = new Socket("localhost", 1901)) {

            // writing to server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
                String path = UDPHandler.class.getResource("northboundloop.txt").toString();
                path = path.substring(6, path.length());
                Scanner scanner = new Scanner(new File(path));
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
                    System.out.println("Server replied "
                            + in.readLine());
                }

                // closing the scanner object
                sc.close();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
