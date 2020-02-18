package gnet;

import akka.actor.AbstractActor;
import akka.actor.Props;
import io.netty.channel.Channel;

/***
 * 每个玩家连接对应一个LinkActor,它负责：
 * （1） 从Netty最后一个入站InBoundHandler拿到Packet,并转发给gs
 *  (2) 从gs收到Packet之后，写入到Channel，即给玩家发送协议
 */
public class LinkSession extends AbstractActor {

    private long acid;

    private Channel channel;

    public static Props props(long acid, Channel channel) {
        return Props.create(LinkSession.class, ()-> new LinkSession(acid,channel));
    }

    public LinkSession(long acid, Channel channel) {
        this.acid = acid;
        this.channel = channel;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().build();
    }
}
