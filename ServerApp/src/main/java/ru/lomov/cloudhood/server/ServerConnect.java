package ru.lomov.cloudhood.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnect  {
    ServerSocket serverSocket;

    public ServerConnect() {
        try {
            serverSocket = new ServerSocket(8787);
            System.out.println("Сервер запущен...");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Подключение создано...");
                new Thread( new ClientHandler(this,socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
