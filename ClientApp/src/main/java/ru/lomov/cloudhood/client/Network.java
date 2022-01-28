package ru.lomov.cloudhood.client;


import java.io.*;
import java.net.Socket;

public class Network {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private byte[] bytes;
    private File file;
    private String fileName;

    public static final int PORT = 8788;
    public static final String HOST = "localhost";

    void connect() {
        if (socket != null && !socket.isClosed()) {
            return;
        } else {
            try {
                socket = new Socket(HOST, PORT);
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                Thread readThread = new Thread(() -> {
                    try {
                        while (true) {
                            if (file != null) {
                                sendFile(file);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                readThread.setDaemon(true);
                readThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void sendFile(File file) throws IOException {
        bytes = new byte[1024];
        try (FileInputStream fis = new FileInputStream(file)) {
            OutputStream sendChannel = new BufferedOutputStream(socket.getOutputStream());
            DataOutputStream fileMetaSent = new DataOutputStream(socket.getOutputStream());
            int read;
            fileMetaSent.writeUTF(file.getName());
            fileMetaSent.writeLong(file.length());
            System.out.println("Отправлено имя файла: " + file.getName());
            while ((read = fis.read(bytes)) > 0) {
                sendChannel.write(bytes, 0, read);
                System.out.println("Отправлено " + read + " байт");
            }
            sendChannel.flush();
            fileMetaSent.flush();
            System.out.println("File send!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
// Заготовка. Не реализовано.
    public void receiveMsg() throws IOException {
        fileName = in.readUTF();
        try (OutputStream fileWrite = new FileOutputStream("C:\\Users\\Gorlum\\Desktop\\Cloud Hood App\\ClientApp\\clientFiles\\" + fileName)) {
            int read = 0;

            System.out.println("Получено имя файла: " + fileName);
            byte[] inBytes = new byte[1024];
            while ((read = in.read(inBytes)) > 0) {
                fileWrite.write(inBytes, 0, read);
                System.out.println("Получено " + read + " байт");
            }
            System.out.println("Запись закончена!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
