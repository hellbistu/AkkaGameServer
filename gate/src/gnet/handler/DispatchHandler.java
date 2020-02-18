package gnet.handler;

import akka.actor.ActorRef;
import gnet.GateProtocol;
import gnet.LinkSessionManager;
import gnet.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispatchHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(DispatchHandler.class);

    private volatile ActorRef linkSessionActor;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Packet) {
            Packet packet = (Packet) msg;

            logger.debug("DispatchHandler ptype= {}",packet.getPtype());

            if (packet.getPtype() <= 100) {
                //协议号不大于100的，代表gate处理的协议，例如 HandleShake,KeepAlive等等
                if (linkSessionActor != null) {
                    //已经HandleShake成功，发给LinkSessionActor来处理
                    linkSessionActor.tell(packet,ActorRef.noSender());
                } else {
                    //没有HandleShake,由LinkSessionManager处理
                    GateProtocol gateProtocol = new GateProtocol(packet,ctx.channel());
                    LinkSessionManager.getRef().tell(gateProtocol,ActorRef.noSender());
                }

//                protogen.gate.HandleShake handleShake = protogen.gate.HandleShake.newBuilder().setAcid(1001).build();
//                protogen.gate.HandleShake.parseFrom()  unmarshal
//                handleShake.toByteArray();  marshal
            } else {
                logger.error("DispatchHandler read wrong data type.{}",msg.getClass());
                ctx.close();
            }



        }
    }
}
