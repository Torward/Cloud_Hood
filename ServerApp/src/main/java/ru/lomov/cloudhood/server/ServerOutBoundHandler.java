package ru.lomov.cloudhood.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ServerOutBoundHandler extends ChannelOutboundHandlerAdapter {
    private File root = new File("ServerApp/serverFiles");

    //private Path path = Path.of("ServerApp/serverFiles");
    private final Path path = Paths.get("ServerApp/serverFiles");
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
    private List<File> filesList = new ArrayList<>();
    private ByteBuf buf = null;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
       log.debug(msg.toString());
        if (msg instanceof Signal) {
            if (msg.equals(Signal.SEND_FILE_LIST)) {
                refreshClientList(ctx, root, filesList);
            }
        }
        if (msg instanceof String filename) {
            sendFileToClient(filename, ctx);
        }
    }

    /*
     * Метод получает имя требуемого файла и контекст.
     * Отправляет набор атрибутов файла и содержимое вычитанное через FileRegion
     * Контекст сбрасывается в конце метода.
     * */
    private void sendFileToClient(String fileReseivedName, ChannelHandlerContext ctx) throws IOException {
        System.out.println("Получено имя файла " + fileReseivedName);
        Path getFile = path.resolve(fileReseivedName);
        File file = getFile.toFile();
//
//        FileRegion region = new DefaultFileRegion(
//                new FileInputStream(file).getChannel(), 0, Files.size(getFile));
        try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {

            buf = ByteBufAllocator.DEFAULT.directBuffer(1);
            buf.writeByte((byte) 16);
            ctx.write(buf);
            System.out.println("Отправлен сигнальный байт: " + 16);

            buf = ByteBufAllocator.DEFAULT.directBuffer(4);
            buf.writeInt(file.getName().length());
            ctx.write(buf);
            System.out.println("Отправлена длинна имени: " + file.getName().length());

            byte[] filenameBytes = file.getName().getBytes(StandardCharsets.UTF_8);
            buf = ByteBufAllocator.DEFAULT.directBuffer(filenameBytes.length);
            buf.writeBytes(filenameBytes);
            ctx.write(buf);
            System.out.println("Отправлено имя файла: " + file.getName());

            buf = ByteBufAllocator.DEFAULT.directBuffer(8);
            buf.writeLong(file.length());
            ctx.write(buf);
            System.out.println("Отправлена длинна файла: " + file.length());

            byte[]fileBytes = in.readAllBytes();
            buf = ByteBufAllocator.DEFAULT.directBuffer();
            buf.writeBytes(fileBytes);
            ctx.write(buf);

            ctx.flush();
            System.out.println("Файл отправлен.");
            buf.release();
        }
    }

    private void refreshClientList(ChannelHandlerContext ctx, File rootDirFiles, List<File> filesList) {
        if (!filesList.isEmpty()) {
            filesList.clear();
        }
        if (root.isDirectory()) {
            File[] dirFiles = root.listFiles();
            if (dirFiles != null) {
                for (File file : dirFiles) {
                    if (file.isDirectory()) {
                        filesList.add(file);
                        root = file;
                        dirFiles = root.listFiles();
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
        ByteBuf buf;
        // while (buf.readableBytes()>0) {
        buf = ByteBufAllocator.DEFAULT.directBuffer(1);
        buf.writeByte((byte) 52);
        ctx.write(buf);
        System.out.println("Отправлен сигнальный байт: " + 52);

        buf = ByteBufAllocator.DEFAULT.directBuffer(8);
        int fileListSize = filesList.size();
        buf.writeInt(fileListSize);
        ctx.write(buf);

        for (File eachFile : filesList) {
            buf = ByteBufAllocator.DEFAULT.directBuffer(4);// Длинна имени
            buf.writeInt(eachFile.getName().length());
            ctx.write(buf);
            System.out.println("Передана длина имени:  " + eachFile.getName().length());

            byte[] filenameBytes = eachFile.getName().getBytes(StandardCharsets.UTF_8);//Передача имени
            buf = ByteBufAllocator.DEFAULT.directBuffer(filenameBytes.length);
            buf.writeBytes(filenameBytes);
            ctx.write(buf);
            System.out.println("Отправлено имя файла: " + eachFile.getName());

            buf = ByteBufAllocator.DEFAULT.directBuffer(16);
            buf.writeLong(eachFile.length());
            ctx.write(buf);
            System.out.println("Отправленна длина файла: " + eachFile.length());

            String modifiedDate = sdf.format(eachFile.lastModified());

            buf = ByteBufAllocator.DEFAULT.directBuffer(8);
            buf.writeInt(modifiedDate.length());
            ctx.write(buf);
            System.out.println("Отправленна длина даты изменения: " + modifiedDate.length());


            byte[] modifiedDateInBytes = modifiedDate.getBytes(StandardCharsets.UTF_8);
            buf = ByteBufAllocator.DEFAULT.directBuffer(modifiedDateInBytes.length);
            buf.writeBytes(modifiedDateInBytes);
            ctx.write(buf);
            System.out.println("Отправлен файл: " + eachFile.getName() + ", размером: " + eachFile.length() + ", изменённый: " + modifiedDate);
            // ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(eachFile);
        }
        ctx.flush();
        // }
        buf.release();
    }

}
