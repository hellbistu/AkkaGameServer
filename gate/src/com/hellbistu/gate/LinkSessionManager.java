package com.hellbistu.gate;

import akka.actor.AbstractActor;

import java.util.HashMap;
import java.util.Map;

public class LinkSessionManager extends AbstractActor {

    private Map<Long,LinkSession> links = new HashMap<Long,LinkSession>();


    @Override
    public Receive createReceive() {
        return null;
    }
}
