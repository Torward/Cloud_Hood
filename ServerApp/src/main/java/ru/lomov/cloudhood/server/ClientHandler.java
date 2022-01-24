package ru.lomov.cloudhood.server;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class ClientHandler implements Runnable{
    private ServerConnect server;
    private Socket socket;
    private DataInputStream fileNameStream;
    private InputStream in;
    private FileOutputStream fOut;

    private String fileName;


    public ClientHandler(ServerConnect server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            in = socket.getInputStream();
            fileNameStream = new DataInputStream(socket.getInputStream());
            fileName = fileNameStream.readUTF();
            fOut = new FileOutputStream("C:\\Users\\Gorlum\\Desktop\\Cloud Hood App\\ServerApp\\serverFiles\\" + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try{
        while (true) {
            byte[] chunk = new byte[fileName.length()];
            new Thread(() -> {
                try {

                    while ((in.read(chunk)) > 0) {
                        System.out.println(chunk.length);
                        fOut.write(chunk);

                    }
                    fOut.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }).start();
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        }

}
