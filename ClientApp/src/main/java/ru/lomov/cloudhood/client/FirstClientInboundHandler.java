package ru.lomov.cloudhood.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FirstClientInboundHandler extends ChannelInboundHandlerAdapter {

    private static ObservableList<FileInfo> list = FXCollections.observableArrayList();
    private TableColumn<FileInfo, String> name;
    private TableColumn<FileInfo, String> date;
    private TableColumn<FileInfo, String> size;
    private TableView<FileInfo> serverView;
    private String fileName;
    private static ChannelHandlerContext ctx;
    private final Path rootDir = Paths.get("ClientApp/clientFiles");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Соединение с сервером установленно..");
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;
        byte command = buffer.readByte();
        Signal signalType = Signal.getSignalByte(command);
        System.out.println("Тип сообщения определён как: " + signalType);
        if (signalType.equals(Signal.GET_FILE_LIST)) {
            refreshFileList(buffer);
        }
        if (signalType.equals(Signal.GET_FILE_TO_CLIENT)){
            download(buffer);
        }


    }

    private void download(ByteBuf buf) {
        int localCondition = 0;
        int localLimiter = -1;
        int iter = 0;
        long longLimiter = 0;

        if (localCondition == 0) {
            if (buf.readableBytes() < localLimiter) {
                System.out.println("Сообщение не прошло верификацию. " + iter);
                return;
            }
            localLimiter = buf.readInt();
            System.out.println("Длинна имени: " + localLimiter);
            localCondition = 1;
            iter++;
        }

        if (localCondition == 1) {
            if (buf.readableBytes() < localLimiter) {
                System.out.println("Сообщение не прошло верификацию. " + iter);
                return;
            }
            byte[] nameInBytes = new byte[localLimiter];
            buf.readBytes(nameInBytes);
            fileName = new String(nameInBytes);
            System.out.println("Получено имя файла: " + fileName);
            localCondition = 2;
            iter++;
            localLimiter = -1;
        }

        if (localCondition == 2) {
            if (buf.readableBytes() < localLimiter) {
                System.out.println("Сообщение не прошло верификацию. " + iter);
                return;
            }
            longLimiter = buf.readLong();
            System.out.println("Получина длинна файла: " + longLimiter);
            localCondition = 3;
            iter++;
        }

        if (localCondition == 3) {
            if (buf.readableBytes() < localLimiter) {
                System.out.println("Сообщение не прошло верификацию. " + iter);
                return;
            }
            try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(rootDir + "/" + fileName))) {
                while (buf.readableBytes() > longLimiter){
                    out.write(buf.readByte());
                }
            //    System.out.println("Файл записан.");
            } catch (IOException e) {
                e.printStackTrace();
                buf.release();
            }
        }
        if (buf.readableBytes() == 0) {
            buf.release();
        }
    }

    public static ObservableList<FileInfo> refreshFileList(ByteBuf buffer) {

        int fileCount;
        String fileSizeOfList = null;
        if (!list.isEmpty()) {
            list.clear();
        }

        fileCount = buffer.readInt();
        System.out.println("Получено количество файлов: " + fileCount);

        for (int i = 0; i < fileCount; i++) {
            int fileNameLength = buffer.readInt();
            System.out.println("Получена длинна имени файла: " + fileNameLength);
            String fileNameOfList = null;


            byte[] nameInBytes = new byte[fileNameLength];
            buffer.readBytes(nameInBytes);
            fileNameOfList = new String(nameInBytes);
            System.out.println("Получено имя файла: " + fileNameOfList);

            long fileSizeInByte;

            fileSizeInByte = buffer.readLong();
            System.out.println("Получена длина файла в байтах: " + fileSizeInByte);

            String fileDateOfList = null;

            int modifiedDateLength = buffer.readInt(); // лечить здесь!!!
            System.out.println("Получена длина даты изменения: " + modifiedDateLength);

            byte[] modifiedDateInBytes = new byte[modifiedDateLength];
            buffer.readBytes(modifiedDateInBytes);
            fileDateOfList = new String(modifiedDateInBytes);
            System.out.println("Получена дата изменения: " + fileDateOfList);
            if (fileSizeInByte == 0) {
                fileSizeOfList = "папка";
            } else if (fileSizeInByte < (1024)) {
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
        if (buffer.readableBytes() == 0) {
            buffer.release();
        }
        return list;
    }

    public static ObservableList<FileInfo> getList() {
        return list;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
