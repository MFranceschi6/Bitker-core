package com.bitker.eventservice.events;

import com.bitker.network.Peer;
import com.bitker.network.PeerState;

/**
 * Created by machiara on 03/03/17.
 */
public class PeerStateChangedEvent implements Event {

    public Peer p;
    public PeerState oldState;

    public PeerStateChangedEvent(Peer peer, PeerState oldState) {
        p = peer;
        this.oldState= oldState;
    }
}
