package com.bitker.network;

import com.bitker.utils.BitConstants;
import com.bitker.Main;
import com.bitker.protocol.Connect;
import com.bitker.protocol.KeepAlive;
import com.bitkermessage.client.messages.messages.*;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

/**
 * Created by Matteo on 12/10/2016.
 *
 */
public class ComputeTask extends Task {

    private SocketChannel skt;
    private Message m;

    ComputeTask(SocketChannel skt, Peer p, Message m){
        this.skt = skt;
        this.p = p;
        this.m = m;
        Main.listener.computeNumber.incrementAndGet();
    }

    @Override
    protected void clean() {
        Main.listener.computeNumber.decrementAndGet();
    }

    @Override
    protected void closeResources() {
        Main.listener.computeNumber.decrementAndGet();
    }

    @Override
    protected void doTask() {
        p.setTimestamp((int) (System.currentTimeMillis()/BitConstants.TIME));
        if(m instanceof VerAck)
            verackResponse();
        else if(m instanceof Version)
            versionResponse((Version) m);
        else if(m instanceof Ping)
            pingResponse((Ping) m);
        else if(m instanceof Address)
            saveAddressees((Address) m);
        else if(m instanceof Inventory)
            inventoryStat((Inventory) m);
        else if(m instanceof GetAddress)
            sendAddress();

    }

    private void sendAddress() {
        try
        {
            Connect.sendAddresses(skt,p);
        } catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private void inventoryStat(Inventory m) {
    	if(m.getInventoryVectors().isEmpty())
    		Main.invStat.emptyInv.incrementAndGet();
    	else
        for(InventoryVector v : m.getInventoryVectors())
            try
            {
                switch (v.getType())
                {
                    case ERROR:
                        Main.invStat.error.incrementAndGet();
                        break;
                    case MSG_TX:
                        Main.invStat.transiction.incrementAndGet();
                        break;
                    case MSG_BLOCK:
                        Main.invStat.block.incrementAndGet();
                        break;
                    case MSG_CMPCT_BLOCK:
                        Main.invStat.cmpct_block.incrementAndGet();
                        break;
                    case MSG_FILTERED_BLOCK:
                        Main.invStat.filtered_block.incrementAndGet();
                        break;
					case MSG_WITNESS_BLOCK:
						Main.invStat.msg_witness_block.incrementAndGet();
						break;
					case MSG_WITNESS_TX:
						Main.invStat.msg_witness_tx.incrementAndGet();
						break;
					case MSG_FILTERED_WITNESS_BLOCK:
						Main.invStat.msg_filtered_witness_block.incrementAndGet();
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
    }

    private void verackResponse() {
        p.setPeerState(PeerState.OPEN);
    }

    private void saveAddressees(Address m) {
        for(PeerAddress p : m.getAddresses())
        {
            if(!Main.peers.containsKey(p.getAddress().getHostAddress()))
            {
                Peer peer = new Peer(p.getAddress(),p.getPort());
                peer.setTimestamp(p.getTime());
                peer.setService(p.getService());
                Main.peers.put(p.getAddress().getHostAddress(),peer);
            }
            else
            {
                Peer peer = Main.peers.get(p.getAddress().getHostAddress());
                if(peer.getTimestamp() < p.getTime())
                {
                    peer.setTimestamp(p.getTime());
                    if(peer.getPeerState() == PeerState.CLOSE)
                    {
                        peer.resetAttempt();
                    }
                }
            }

        }
    }

    private void pingResponse(Ping m){
        try
        {
            KeepAlive.sendPong(m,skt,p);
        } catch (InterruptedException | ClosedChannelException ignored)
        {}
    }

    private void versionResponse(Version m) {
        VerAck ack = new VerAck();
        p.setPeerState(PeerState.OPEN);
        p.setService(m.getService());
        p.setTimestamp((int) (System.currentTimeMillis() / BitConstants.TIME));
        p.setPort(m.getYourAddress().getPort());
        p.setAgent(m.getUserAgent());
        p.setTheirVersion(m.getVersion());
        try
        {
            Connect.sendVerAck(ack,skt,p);
        } catch (ClosedChannelException | InterruptedException ignored)
        {}
        try
        {
            Connect.sendGetAddress(skt,p);
        } catch (ClosedChannelException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }






}
