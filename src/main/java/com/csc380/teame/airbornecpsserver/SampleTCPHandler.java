package com.csc380.teame.airbornecpsserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class SampleTCPHandler implements Runnable {
    private final Socket clientSocket;

    //constructor
    public SampleTCPHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        PrintWriter out = null;
        BufferedReader in = null;

        //get outputstream of client
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //get inputstream of client
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String line = "";

            while (true) {
                try {
                    if (!((line = in.readLine()) != null)) break;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }


                //writing the received message from client
                    System.out.println("Sent from the client: "+line+"\n");
                    out.println(line);
                }

        }
    }

