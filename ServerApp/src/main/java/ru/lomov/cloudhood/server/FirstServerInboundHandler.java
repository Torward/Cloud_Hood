package ru.lomov.cloudhood.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FirstServerInboundHandler extends ChannelInboundHandlerAdapter {
    private Signal signalType = Signal.VOID;
    private int limiter = -1;
    private int condition = -1;
    private int iter = 0;
    private String fileName;
    private final Path rootDir = Paths.get("ServerApp/serverFiles");


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Соединение сервера с клиентом установлено..");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;
       // while(buffer.readableBytes()>0) {

            if (signalType.equals(Signal.VOID)) {
                byte command = buffer.readByte();
                signalType = Signal.getSignalByte(command);
                limiter = 4;
                log.debug("Тип сообщения определён как: " + signalType);
                condition = 0;
                iter++;
            }
            if (signalType.equals(Signal.WRITE_FILE_TO_CLOUD)) {
                writeToCloud(buffer);
            }
            if (signalType.equals(Signal.SEND_FILE_TO_CLIENT)) {
                sendToClient(buffer, ctx);
            }
            if (signalType.equals(Signal.SEND_FILE_LIST)) {
                ctx.channel().writeAndFlush(Signal.SEND_FILE_LIST);
                signalType = Signal.VOID;
                }
            if (signalType.equals(Signal.DELETE_FILE)) {
                deleteFile(buffer);
            }
            condition = -1;
            iter = 0;
       // }
    }

    private void deleteFile(ByteBuf buffer) {
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /*
     * Метод вычитывает атрибуты файла
     * и отправляет имя файла методу ServerOutBoundHandler
     * */
    private void sendToClient(ByteBuf buffer, ChannelHandlerContext ctx) {
        int fileNameSize = buffer.readInt();
        byte[] nameInBytes = new byte[fileNameSize];
        buffer.readBytes(nameInBytes);
        String fileToClientName = new String(nameInBytes);
        ctx.channel().writeAndFlush(fileToClientName);
    }

    public void writeToCloud(ByteBuf buf) {
        int localCondition = condition;
        int localLimiter = -1;
        int fileNameSize = 0;
        long fileSize = 0;
        long receivedFileBytes = 0;

        if (localCondition == 0) {
            if (buf.readableBytes() < localLimiter) {
                System.out.println("Сообщение не прошло верификацию. " + iter);
                condition = -1;
                return;
            }
            fileNameSize = buf.readInt();
            System.out.println("Длинна имени: " + fileNameSize);
            localCondition = 1;
            iter++;
        }

        if (localCondition == 1) {
            if (buf.readableBytes() == -1) {
                System.out.println("Сообщение не прошло верификацию. " + iter);
                condition = -1;
                return;
            }

            byte[] nameInBytes = new byte[fileNameSize];
            buf.readBytes(nameInBytes);
            fileName = new String(nameInBytes);
            System.out.println("Получено имя файла: " + fileName);
            localCondition = 2;
            iter++;

        }

        if (localCondition == 2) {
            if (buf.readableBytes() == -1) {
                System.out.println("Сообщение не прошло верификацию. " + iter);
                condition = -1;
                return;
            }
            fileSize = buf.readLong();
            System.out.println("Получена длина файла: " + fileSize);
            localCondition = 3;
            iter++;
        }

        if (localCondition == 3) {
            if (buf.readableBytes() < localLimiter) {
                System.out.println("Сообщение не прошло верификацию. " + iter);
                condition = -1;
                return;
            }
            try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(rootDir + "/" + fileName))) {
                while (buf.readableBytes() > 0) {
                    out.write(buf.readByte());
                    receivedFileBytes++;
                    if(fileSize == receivedFileBytes){
                        signalType = Signal.VOID;
                        System.out.println("Файл записан.");
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
}
