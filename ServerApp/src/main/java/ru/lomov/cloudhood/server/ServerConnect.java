package ru.lomov.cloudhood.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnect  {
    ServerSocket serverSocket;
    public static final int PORT = 8788;

    public ServerConnect() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Сервер запущен...");
            while (true) {
                Socket server = serverSocket.accept();
                System.out.println("Подключение создано...");
                new Thread( new ClientHandler(this,server)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
