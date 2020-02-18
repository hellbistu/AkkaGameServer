package gnet;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protogen.gate;

import java.util.HashMap;
import java.util.Map;

public class LinkSessionManager extends AbstractActor {

    private static Logger logger = LoggerFactory.getLogger(LinkSessionManager.class);

    private static volatile ActorRef managerRef;

    private Map<Long,ActorRef> linkSessionMap = new HashMap<>();

    public static void init(ActorRef managerRef) {
        LinkSessionManager.managerRef = managerRef;
    }

    public static ActorRef getRef() {
        return LinkSessionManager.managerRef;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(GateProtocol.class, protocol -> {
            //client发来的握手
            //TODO 进行真正的HandleShake
            if (protocol.packet.getPtype() == 1) {
                //HandleShake协议
                gate.HandleShake handleShake = gate.HandleShake.parseFrom(protocol.packet.getPdata());
                logger.info("handle shake.acid={}",handleShake.getAcid());

                //TODO 去gs建立actor,与LinkSession 1:1 bind

                ActorRef sessionActor = getContext().actorOf(LinkSession.props(handleShake.getAcid(),protocol.channel),String.format("link-%d",handleShake.getAcid()));
                linkSessionMap.put(handleShake.getAcid(),sessionActor);

                gate.HandleShakeResult result = gate.HandleShakeResult.newBuilder().setCode(200).build();
                Packet reply = new Packet((short)2,result.toByteArray());
                protocol.channel.writeAndFlush(reply);
            }

        }).build();
    }
}
