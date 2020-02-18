package gnet;

import io.netty.channel.Channel;

/***
 * 由gate来处理的协议
 */
public class GateProtocol {

    public Packet packet;

    public Channel channel;

    public GateProtocol(Packet packet,Channel channel) {
        this.packet = packet;
        this.channel = channel;
    }
}
