package com.bitker.protocol;


import com.bitker.network.Peer;
import com.bitkermessage.client.messages.messages.Inventory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

/**
 * Created by Matteo on 26/10/2016.
 */
public class InventoryProtocol {

    public static void sendInventory(Inventory msg, SocketChannel skt, Peer p) throws InterruptedException, ClosedChannelException {
        ByteBuffer header = ProtocolUtil.writeHeader(msg);
        ByteBuffer payload = null;
        try
        {
            payload = ProtocolUtil.writePayload(msg);
            header.put(ProtocolUtil.getChecksum(payload));
            ProtocolUtil.sendMessage(header,payload,skt,p);

        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
