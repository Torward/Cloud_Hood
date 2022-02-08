package ru.lomov.cloudhood.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FirstInboundHandler extends ChannelInboundHandlerAdapter {
    private Signal signalType = Signal.VOID;
    private int limiter = -1;
    private int condition = -1;
    private int iter = 0;
    private String fileName;

    private final Path rootDir = Paths.get("ServerApp/serverFiles");


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Соединение установлено..");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;
        if (condition == -1) {
            byte command = buffer.readByte();
            signalType = Signal.getSignalByte(command);
            limiter = 4;
            System.out.println("Тип сообщения определён как: " + signalType);
            condition = 0;
            iter++;
        }
        if (signalType.equals(Signal.WRITE_FILE_TO_CLOUD)) {
            writeToCloud(buffer);
        }
        if (signalType.equals(Signal.SEND_FILE_TO_CLIENT)) {
            sendToClient(buffer, ctx);
        }
        if(signalType.equals(Signal.SEND_FILE_LIST)){
            ctx.channel().writeAndFlush(Signal.SEND_FILE_LIST);
        }
        condition = -1;
        iter = 0;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

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

        if (localCondition == 0) {
            if (buf.readableBytes() < localLimiter) {
                System.out.println("Сообщение не прошло верификацию. " + iter);
                condition = -1;
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
                condition = -1;
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
                condition = -1;
                return;
            }
            long longLimiter = buf.readLong();
            System.out.println("Получина длинна файла: " + longLimiter);
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
                }
                System.out.println("Файл записан.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (buf.readableBytes() == 0) {
            buf.release();
        }
    }
}
