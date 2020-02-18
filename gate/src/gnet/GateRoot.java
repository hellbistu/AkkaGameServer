package gnet;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GateRoot extends AbstractActor {

    private static Logger logger = LoggerFactory.getLogger(GateRoot.class);

    private ActorRef linkSessionManagerRef;

    @Override
    public Receive createReceive() {
        return receiveBuilder().matchEquals("init", s -> {
            logger.info("init LinkSessionManager");
            linkSessionManagerRef = getContext().actorOf(Props.create(LinkSessionManager.class),"LinkSessionManager");
            LinkSessionManager.init(linkSessionManagerRef);
        }).build();
    }
}
