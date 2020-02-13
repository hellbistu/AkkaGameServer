package com.hellbistu.gate.handler;

import com.hellbistu.gate.LinkSession;
import com.hellbistu.gate.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispatchHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(DispatchHandler.class);

    private LinkSession linkActor;



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Packet) {
            Packet packet = (Packet) msg;

            logger.debug("ptype= {}",packet.getPtype());
            logger.debug("pdata={}", new String(packet.getPdata()));

            //TODO 这里应该把协议转发到game server
            //可以通过grpc来转发
            if (packet.getPtype() <= 100) {
                //协议号不大于100的，代表gate处理的协议，例如 HandleShake,KeepAlive等等

            } else {
                //转发给gs

            }



        }
    }
}
