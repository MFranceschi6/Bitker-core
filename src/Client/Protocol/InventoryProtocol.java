package Client.Protocol;

import Client.messages.Inventory;
import Client.messages.SerializedMessage;
import Client.network.Peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

/**
 * Created by Matteo on 26/10/2016.
 */
public class InventoryProtocol {

    public static void sendInventory(Inventory msg,SocketChannel skt, Peer p) throws InterruptedException, ClosedChannelException {
        ByteBuffer header = ProtocolUtil.writeHeader(msg);
        ByteBuffer[] payload = new ByteBuffer[0];
        try
        {
            payload = ProtocolUtil.writePayload(msg);
        } catch (IOException e)
        {
            SerializedMessage.returnHeader(header);
        }
        header.put(ProtocolUtil.getChecksum(payload));

        ProtocolUtil.sendMessage(header,payload,skt,p);

    }
}