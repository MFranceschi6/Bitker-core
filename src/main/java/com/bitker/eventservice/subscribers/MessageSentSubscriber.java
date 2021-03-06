package com.bitker.eventservice.subscribers;

import com.bitker.eventservice.events.Event;
import com.bitker.eventservice.events.MessageSentEvent;

import java.nio.ByteBuffer;

/**
 * Created by machiara on 07/03/17.
 */
public class MessageSentSubscriber extends Subscriber {

    @Override
    public void inform(Event event) {
        if(event instanceof MessageSentEvent)
        {
            MessageSentEvent e = (MessageSentEvent) event;
            if(e.msg.getId() == id)
            {
                ByteBuffer msg = ByteBuffer.allocate(4 + 4 + 8);
                msg.putInt(4 + 8);
                msg.putInt(3);
                msg.putLong(id);
                data.addMsg(msg);
            }
        }
    }
}
