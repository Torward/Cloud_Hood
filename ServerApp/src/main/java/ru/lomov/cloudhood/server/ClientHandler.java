package ru.lomov.cloudhood.server;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientHandler implements Runnable {
    private ServerConnect serverConnect;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private OutputStream fileWriter;
    private byte[] bytes;


    private DataInputStream fileMetaReceived;
    private final File file = new File("./serverFiles/");
    private String fileName;
    private String rootDir = "ServerApp/serverFiles";
    private String command;
    private long fileSize;

    public ClientHandler(ServerConnect serverConnect, Socket socket) {
        this.serverConnect = serverConnect;
        this.socket = socket;
        bytes = new byte[1024];
    }

    @Override
    public void run() {
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            fileMetaReceived = new DataInputStream(socket.getInputStream());
            while (true) {
                processFileData();
           }
        } catch (Exception e) {
            System.err.println("Соединение потеряно...");
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
    private void processFileData() throws IOException {
        fileName = fileMetaReceived.readUTF();
        fileSize = fileMetaReceived.readLong();
        System.out.println("Получено имя файла: " + fileName);
        System.out.println("Получен размер файла: " + fileSize);
        Path file = Paths.get(rootDir, fileName);
        try(FileOutputStream fileWriter = new FileOutputStream(rootDir + "/" + fileName)){
            for (int i = 0; i < (fileSize + 1023) / 1024; i++) {
                int read = in.read(bytes);
                System.out.println("Получено " + read + " байт");
                fileWriter.write(bytes, 0, read);
            }
        }
        System.out.println("Запись закончена!");
    }

}
