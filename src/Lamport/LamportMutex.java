package Lamport;

import Utils.Client;
import Utils.Constants;

import java.io.IOException;
import java.net.DatagramPacket;


public class LamportMutex extends Thread {
    private DirectClock v;
    private int[] q;
    private int id;
    private Client client;

    public LamportMutex(int id, Client client) {
        this.id = id;
        this.client = client;
        this.v = new DirectClock(client.getPorts().length, this.id);
        this.q = new int[client.getPorts().length];
        for (int j = 0; j < client.getPorts().length; j++) this.q[j] = Integer.MAX_VALUE;
    }

    public void myWait() {
        try {
            DatagramPacket datagramPacket = this.client.receiveMessage();
            if (datagramPacket != null) {
                String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                String[] parts = message.split("-");
                this.handleMsg(Integer.valueOf(parts[1]), datagramPacket.getPort(), parts[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void requestCS() {
        v.tick();
        q[this.id] = v.getValue(this.id);

        client.broadcastMessage(Constants.REQUEST_MSG, q[id]);
        while (!okayCS()) myWait();
    }

    public synchronized void releaseCS() {
        q[id] = Integer.MAX_VALUE;
        client.broadcastMessage(Constants.RELEASE_MSG, v.getValue(id));
    }

    boolean okayCS() {
        for (int j = 0; j < this.client.getPorts().length; j++) {
            if (isGreater(q[id], id, q[j], j)) return false;
            if (isGreater(q[id], id, v.getValue(j), j)) return false;
        }
        return true;
    }

    boolean isGreater(int entry1, int pid1, int entry2, int pid2) {
        if (entry2 == Integer.MAX_VALUE) return false;
        return ((entry1 > entry2) || ((entry1 == entry2) && (pid1 > pid2)));
    }

    public synchronized void handleMsg(int timeStamp, int src, String tag) {
        int id = client.getId(src);
        v.receiveAction(id, timeStamp);

        if (tag.equals(Constants.REQUEST_MSG)) {
            this.q[id] = timeStamp;
            client.sendMessage(src, Constants.ACK_MSG, v.getValue(this.id));
        } else if (tag.equals(Constants.RELEASE_MSG)) this.q[id] = Integer.MAX_VALUE;

    }

    public void run() {
        try {
            while (true) {
                String message = "";
                DatagramPacket datagramPacket;

                while (!(message.equals(Constants.TOKEN_MSG))) {
                    datagramPacket = this.client.receiveMessage();
                    message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                }

                requestCS();

                for (int i = 0; i < 10; i++) {
                    System.out.println("Soc el procés lightweight A" + (this.id + 1));
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                releaseCS();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
