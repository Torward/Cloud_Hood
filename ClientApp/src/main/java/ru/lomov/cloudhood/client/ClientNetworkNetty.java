package ru.lomov.cloudhood.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.collections.ObservableList;

import java.io.*;

public class ClientNetworkNetty {
    private static final int PORT = 8788;
    private static final String HOST = "localhost";
    private static final String SEND = "##sendFileToServer";
    private static final String GET = "##getFileFromServer";
    private static final String DELETE = "##deleteFileFromServer";
    private static final String SHARE = "##shareMyFileWithFrend";
    private static final String RENAME = "##renameFileFromServer";
    //  private Signal signal = Signal.VOID;
    private ObservableList<FileInfo> list;
    private ByteBuf buf;

    SocketChannel channel;

    public ClientNetworkNetty() {
        new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                channel = socketChannel;
                                socketChannel.pipeline().addLast(new ClientOutBoundHandler(), new FirstClientInboundHandler());

                            }
                        });
                ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
                System.out.println("Соединение найдено..");
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }

        }).start();
    }

    public void sendMessage(File file) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        channel.writeAndFlush(file);
    }

    /*
    * Метод скачивания файла с сервера
    * Отправляем ClientOutBoundHandler имя файла который хотим скачать.
    * */
    public void getFile(String fileName) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        channel.writeAndFlush(fileName);
    }

    public void refreshFileList() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        channel.writeAndFlush(Signal.GET_FILE_LIST);

    }

    public void deleteFile(String fileName){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        channel.writeAndFlush(fileName);
    }

    public ObservableList<FileInfo> getList() {
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        list = FirstClientInboundHandler.getList();
        return list;
    }
    //    void receiveMsg(String fileReseivedName, TextArea textArea) throws IOException {
//        out.writeUTF(DOWNLOAD);
//        out.writeUTF(fileReseivedName);
//        long fileSize = in.readLong();
//        byte[] buff = new byte[1024];
//        try (FileOutputStream fileWriter = new FileOutputStream(rootDir + "/" + fileReseivedName)) {
//            for (int i = 0; i < (fileSize + 1023) / 1024; i++) {
//                int read = in.read(buff);
//                textArea.appendText("Получено " + read + "байт\n");
//                fileWriter.write(buff, 0, read);
//            }
//            System.out.println("Запись закончена!");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


}
