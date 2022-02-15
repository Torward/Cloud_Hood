package ru.lomov.cloudhood.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserConnectionServerHandler extends ChannelInboundHandlerAdapter {
    private final JdbcConnect jdbcConnect = new JdbcConnect();
    private Signal signalType;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            jdbcConnect.connect();
            log.debug("Базу подняли..");
        } finally {
            jdbcConnect.disconnect();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        jdbcConnect.disconnect();
        log.debug("База отключена");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;
        byte command = buffer.readByte();
        signalType = Signal.getSignalByte(command);
        System.out.println("Тип сообщения определён как: " + signalType);
        if (signalType.equals(Signal.AUTH)) {
            authQuery(buffer);
        } else {
            ctx.fireChannelRead(buffer);
        }
    }

    private void authQuery(ByteBuf buf) {
        log.debug("Пользователь авторизован!");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
