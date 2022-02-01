package ru.lomov.cloudhood.client;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Network {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private byte[] bytes;
    private File file;
    private String fileName;
    private Path rootDir = Paths.get("ClientApp/clientFiles");
    private String fileReseivedName;
    private TextArea textArea;
    private ObservableList<FileInfo> list = FXCollections.observableArrayList();
    private TableColumn<FileInfo, String> name;
    private TableColumn<FileInfo, String> date;
    private TableColumn<FileInfo, String> size;
    private TableView<FileInfo> serverView;


    public static final int PORT = 8788;
    public static final String HOST = "localhost";
    private static final String UPLOAD = "##upload";
    private static final String DOWNLOAD = "##sendMeFile";
    private static final String RELOAD = "##sendMeListOfFiles";
    private static final String DELETE = "##delete";
    private static final String SHARE = "##share";

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
                                Platform.runLater(() -> {
                                    try {
                                        receiveMsg(fileReseivedName, textArea);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
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
// В проекте
    public void processFileData() throws IOException {
        String msg = in.readUTF();
        System.out.println("From process " + msg);
        if (msg.equals(UPLOAD)) {
            sendFile(file);
        } else if (msg.equals(DOWNLOAD)) {
            Platform.runLater(() -> {
                try {
                    receiveMsg(fileReseivedName, textArea);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else if (msg.equals(RELOAD)) {
            reload();
        } else if (msg.equals(DELETE)) {

        } else if (msg.equals(SHARE)) {

        }

    }

    void sendFile(File file) throws IOException {
        bytes = new byte[1024];
        try (FileInputStream fis = new FileInputStream(file)) {
            OutputStream sendChannel = new BufferedOutputStream(socket.getOutputStream());
            DataOutputStream fileMetaSent = new DataOutputStream(socket.getOutputStream());
            int read;
            fileMetaSent.writeUTF(UPLOAD);
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

    public void sendTextMsg(String msg) throws IOException {
        out.writeUTF(msg);
        out.flush();
    }

    void receiveMsg(String fileReseivedName, TextArea textArea) throws IOException {
        out.writeUTF(DOWNLOAD);
        out.writeUTF(fileReseivedName);
        long fileSize = in.readLong();
        byte[] buff = new byte[1024];
        try (FileOutputStream fileWriter = new FileOutputStream(rootDir + "/" + fileReseivedName)) {
            for (int i = 0; i < (fileSize + 1023) / 1024; i++) {
                int read = in.read(buff);
                textArea.appendText("Получено " + read + "байт\n");
                fileWriter.write(buff, 0, read);
            }
            System.out.println("Запись закончена!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ObservableList<FileInfo> reload() throws IOException {
        out.writeUTF(RELOAD);
        if(!list.isEmpty()){
            list.clear();
        }
        String fileSizeOfList = null;
        int fileCount = in.readInt();
        for (int i = 0; i < fileCount; i++) {
            String fileNameOfList = in.readUTF();
            long fileSizeInByte = in.readLong();
            String fileDateOfList = in.readUTF();
            if (fileSizeInByte<=0){
                fileSizeOfList = "папка";
            }else if (fileSizeInByte < (1024)) {
               fileSizeOfList = fileSizeInByte + " B";
            } else if (fileSizeInByte >= (1024) && fileSizeInByte < (1024 * 1024)) {
                long sizeInKb = fileSizeInByte / 1024;
                fileSizeOfList = sizeInKb + " KB";
            } else if (fileSizeInByte >= (1024 * 1024) && fileSizeInByte < (1024 * 1024 * 1024)) {
                long sizeInMb = fileSizeInByte / (1024 * 1024);
                fileSizeOfList = sizeInMb + " MB";
            } else if (fileSizeInByte >= (1024 * 1024 * 1024)) {
                long sizeInGb = fileSizeInByte / (1024 * 1024 * 1024);
                fileSizeOfList = sizeInGb + " GB";
            }
            list.add(new FileInfo(fileNameOfList, fileSizeOfList, fileDateOfList));
        }
        out.flush();
        return list;
    }


    public Socket getSocket() {
        return socket;
    }
}
