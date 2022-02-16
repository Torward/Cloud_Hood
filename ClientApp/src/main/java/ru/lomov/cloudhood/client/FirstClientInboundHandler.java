package ru.lomov.cloudhood.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FirstClientInboundHandler extends ChannelInboundHandlerAdapter {

    private static ObservableList<FileInfo> list = FXCollections.observableArrayList();
    private TableColumn<FileInfo, String> name;
    private TableColumn<FileInfo, String> date;
    private TableColumn<FileInfo, String> size;
    private TableView<FileInfo> serverView;
    private String fileName;
    private static ChannelHandlerContext ctx;
    private final Path rootDir = Paths.get("ClientApp/clientFiles");
    private Signal signalType = Signal.VOID;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Соединение с сервером установленно..");
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;
       // while (buffer.readableBytes()>0) {
            if(signalType.equals(Signal.VOID)) {
                byte command = buffer.readByte();
                signalType = Signal.getSignalByte(command);
                log.debug("Тип сообщения определён как: " + signalType);

            }
            if (signalType.equals(Signal.GET_FILE_LIST)) {
                refreshFileList(buffer);
            }
            if (signalType.equals(Signal.GET_FILE_TO_CLIENT)) {
                download(buffer);
            }

       // }


    }

    private void download(ByteBuf buf) {
        int localCondition = 0;
        int localLimiter = -1;
        int iter = 0;
        long fileSize = 0;
        long receivedFileBytes = 0;

        if (localCondition == 0) {
            if (buf.readableBytes() < localLimiter) {
                log.debug("Сообщение не прошло верификацию. " + iter);
                return;
            }
            localLimiter = buf.readInt();
            log.debug("Длинна имени: " + localLimiter);
            localCondition = 1;
            iter++;
        }

        if (localCondition == 1) {
            if (buf.readableBytes() < localLimiter) {
                log.debug("Сообщение не прошло верификацию. " + iter);
                return;
            }
            byte[] nameInBytes = new byte[localLimiter];
            buf.readBytes(nameInBytes);
            fileName = new String(nameInBytes);
            log.debug("Получено имя файла: " + fileName);
            localCondition = 2;
            iter++;
            localLimiter = -1;
        }

        if (localCondition == 2) {
            if (buf.readableBytes() < localLimiter) {
                log.debug("Сообщение не прошло верификацию. " + iter);
                return;
            }
            fileSize = buf.readLong();
            log.debug("Получина длинна файла: " + fileSize);
            localCondition = 3;
            iter++;
        }

        if (localCondition     == 3) {
            if (buf.readableBytes() < fileSize) {
                log.debug("Сообщение не прошло верификацию. " + iter);
                return;
            }
            try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(rootDir + "/" + fileName))) {
                while (buf.readableBytes() > 0) {
                    out.write(buf.readByte());
                    receivedFileBytes++;
                    if (fileSize == receivedFileBytes){
                        log.debug("Файл записан.");
                        signalType = Signal.VOID;
                        break;
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (buf.readableBytes() == 0) {
            buf.release();
        }
    }

    public ObservableList<FileInfo> refreshFileList(ByteBuf buffer) {

        int fileCount;
        String fileSizeOfList = null;
        if (!list.isEmpty()) {
            list.clear();
        }

        fileCount = buffer.readInt();
        log.debug("Получено количество файлов: " + fileCount);

        for (int i = 0; i < fileCount; i++) {
            int fileNameLength = buffer.readInt();
            log.debug("Получена длинна имени файла: " + fileNameLength);
            String fileNameOfList = null;


            byte[] nameInBytes = new byte[fileNameLength];
            buffer.readBytes(nameInBytes);
            fileNameOfList = new String(nameInBytes);
            log.debug("Получено имя файла: " + fileNameOfList);

            long fileSizeInByte;

            fileSizeInByte = buffer.readLong();
            log.debug("Получена длина файла в байтах: " + fileSizeInByte);

            String fileDateOfList = null;

            int modifiedDateLength = buffer.readInt(); // лечить здесь!!!
            log.debug("Получена длина даты изменения: " + modifiedDateLength);

            byte[] modifiedDateInBytes = new byte[modifiedDateLength];
            buffer.readBytes(modifiedDateInBytes);
            fileDateOfList = new String(modifiedDateInBytes);
            log.debug("Получена дата изменения: " + fileDateOfList);
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
        signalType = Signal.VOID;
        return list;
    }

    public ObservableList<FileInfo> getList() {
        signalType = Signal.VOID;
        return list;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
