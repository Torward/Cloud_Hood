package ru.lomov.cloudhood.server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private static final String UPLOAD = "##upload";
    private static final String DELETE = "##delete";
    private static final String SHARE = "##share";
    private static final String DOWNLOAD = "##sendMeFile";
    private static final String RELOAD = "##sendMeListOfFiles";

    private ServerConnect serverConnect;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private OutputStream fileWriter;
    private byte[] bytes;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");


    private DataInputStream fileMetaReceived;
    private final File file = new File("./serverFiles/");
    private String fileName;
    private String fileNameToClient;
    private Path rootDir = Paths.get("ServerApp/serverFiles");
    private File root = new File("ServerApp/serverFiles");
    private String command;
    private long fileSize;
    private long fileSizeToClient;
    private List<File> filesList = new ArrayList<>();

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
        String msg = in.readUTF();
        System.out.println("From process "+msg);
        if (msg.equals(UPLOAD)) {
            fileName = fileMetaReceived.readUTF();
            fileSize = fileMetaReceived.readLong();
            System.out.println("Получено имя файла: " + fileName);
            System.out.println("Получен размер файла: " + fileSize);
//            Path file = Paths.get(rootDir, fileName);
            try (FileOutputStream fileWriter = new FileOutputStream(rootDir + "/" + fileName)) {
                for (int i = 0; i < (fileSize + 1023) / 1024; i++) {
                    int read = in.read(bytes);
                    System.out.println("Получено " + read + " байт");
                    fileWriter.write(bytes, 0, read);
                    fileWriter.flush();
                }

            }
            System.out.println("Запись закончена!");

        } else if (msg.equals(DOWNLOAD)) {

            fileNameToClient = fileMetaReceived.readUTF();
            Path file = rootDir.resolve(fileNameToClient);
            long size = Files.size(file);
            byte[] buff = Files.readAllBytes(file);
            out.writeLong(size);
            out.write(buff);
            System.out.println("Получено " + size + " байт");
            System.out.println("Получено имя файла: " + fileNameToClient);

        } else if (msg.equals(RELOAD)) {
            reload(root, filesList);
        }else if(msg.equals(DELETE)){
            delete();
        }else if(msg.equals(SHARE)){
            share();
        }

    }

    private void share() {
    }

    private void delete() {
    }

    public void reload(File rootDirFiles, List<File> filesList) throws IOException {
        if(!filesList.isEmpty()){
            filesList.clear();
        }
        if(rootDirFiles.isDirectory()){
            File[] dirFiles = rootDirFiles.listFiles();
            if (dirFiles != null) {
                for (File file : dirFiles) {
                    if (file.isDirectory()) {
                        filesList.add(file);
                        rootDirFiles = file;
                       dirFiles = rootDirFiles.listFiles();
                        if (dirFiles != null) {
                            for (File newFile : dirFiles) {
                                filesList.add(newFile);
                            }
                        }
                    } else {
                        filesList.add(file);
                    }
                }
            }
        }
//         filesList = new ArrayList<>();
//        File[] dirFiles = rootDirFiles.listFiles();
//        filesList.addAll(Arrays.asList(dirFiles));

        out.writeInt(filesList.size());
        for (File eachFile : filesList) {
            String fileName = eachFile.getName();
            out.writeUTF(fileName);
            long fileSizeInByte = eachFile.length();
            out.writeLong(fileSizeInByte);
            String modifiedDate = sdf.format(eachFile.lastModified());
            out.writeUTF(modifiedDate);
            System.out.println("Отправлен файл: " + fileName + ", размером: " + fileSizeInByte + ", изменённый: " + modifiedDate);
            // ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(eachFile);
        }


    }

}
