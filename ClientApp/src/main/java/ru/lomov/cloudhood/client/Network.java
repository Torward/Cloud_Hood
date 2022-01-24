package ru.lomov.cloudhood.client;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.io.*;

public class Network{
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public static final int PORT = 8787;
    public static final String HOST = "localhost";

    private byte[]inputMSG;

    void connect() {
        if (socket != null && !socket.isClosed()) {
            return;
        }
        try {
            socket = new Socket(HOST, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            new Thread(()->{
                try {
                while (true){
                        inputMSG = in.readAllBytes();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Socket getSocket() {
        return socket;
    }
}
