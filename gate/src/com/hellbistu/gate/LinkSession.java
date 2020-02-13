package com.hellbistu.gate;

import akka.actor.AbstractActor;

/***
 * 每个玩家连接对应一个LinkActor,它负责：
 * （1） 从Netty最后一个入站InBoundHandler拿到Packet,并转发给gs
 *  (2) 从gs收到Packet之后，写入到Channel，即给玩家发送协议
 */
public class LinkActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return null;
    }
}
