package ru.lomov.cloudhood.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

public class OutBoundHandler extends ChannelOutboundHandlerAdapter {

    private int limiter = -1;
    private int condition = -1;
    private int iter = 0;
    private long longLimiter = 0;
    private String fileName;
    private ByteBuf buf = null;

    private Signal signalType = Signal.VOID;


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof String fileName) {
            buf = ByteBufAllocator.DEFAULT.directBuffer(1);
            buf.writeByte((byte) 16);
            ctx.write(buf);
            System.out.println("Отправлен сигнальный байт: " + 16);

            buf = ByteBufAllocator.DEFAULT.directBuffer(4);
            buf.writeInt(fileName.length());
            ctx.write(buf);
            System.out.println("Отправлена длинна имени: " + fileName.length());

            byte[] filenameBytes = fileName.getBytes(StandardCharsets.UTF_8);
            buf = ByteBufAllocator.DEFAULT.directBuffer(filenameBytes.length);
            buf.writeBytes(filenameBytes);
            ctx.write(buf);
            System.out.println("Отправлено имя файла: " + fileName);

            System.out.println("Имя файла отправлен.");
            ctx.flush();
            buf.release();
        }
        if (msg instanceof Signal) {
            if (msg.equals(Signal.GET_FILE_LIST)) {
                buf = ByteBufAllocator.DEFAULT.directBuffer(1);
                buf.writeByte((byte) 52);
                ctx.writeAndFlush(buf);
                System.out.println("Отправлен сигнальный байт: " + 52);
            }
        }

        if (msg instanceof File file) {
            FileRegion region = new DefaultFileRegion(new FileInputStream(file).getChannel(), 0, file.length());

            buf = ByteBufAllocator.DEFAULT.directBuffer(1);
            buf.writeByte((byte) 36);
            ctx.write(buf);
            System.out.println("Отправлен сигнальный байт: " + 36);

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
            System.out.println("Отправлен длинна файла: " + file.length());

            ctx.write(region);
            System.out.println("Файл отправлен.");
            ctx.flush();
            buf.release();
        }
    }


}
