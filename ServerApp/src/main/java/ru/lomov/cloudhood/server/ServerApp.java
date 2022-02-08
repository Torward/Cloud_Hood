package ru.lomov.cloudhood.server;

public class ServerApp {

    public static void main(String[] args) {
        //new ServerConnect();
        try {
            new NettyConnect().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
