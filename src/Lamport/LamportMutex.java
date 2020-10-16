package Lamport;

import Utils.Network;

import java.net.DatagramPacket;


public class LamportMutex extends Thread {
    private DirectClock v;
    private int[] q;
    private int id = 0;
    private Network network;

    public LamportMutex(int id, Network network) {
        this.id = id;
        this.network = network;
        this.v = new DirectClock(network.getPorts().length, this.id);
        this.q = new int[network.getPorts().length];
        for (int j = 0; j < network.getPorts().length; j++) this.q[j] = Integer.MAX_VALUE;
    }

    public void myWait() {
        DatagramPacket datagramPacket = this.network.receiveMessage();

        if (datagramPacket != null) {
            String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
            String[] parts = message.split("&");

            int portSender = Integer.valueOf(parts[0]);
            int timeStamp = Integer.valueOf(parts[2]);

            this.handleMsg(timeStamp, portSender, parts[1]);
        }
    }

    public synchronized void requestCS() {
        v.tick();
        q[this.id] = v.getValue(this.id);

        network.broadcastMessage("request", q[id]);
        while (!okayCS()) myWait();
    }

    public synchronized void releaseCS() {
        q[id] = Integer.MAX_VALUE;
        network.broadcastMessage("release", v.getValue(id));
    }

    boolean okayCS() {
        for (int j = 0; j < this.network.getPorts().length; j++) {
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
        int id = network.getPosition(src);

        v.receiveAction(id, timeStamp);

        if (tag.equals("request")) {
            this.q[id] = timeStamp;
            network.sendMessage(src, "ack", v.getValue(this.id));
        } else if (tag.equals("release")) this.q[id] = Integer.MAX_VALUE;

    }


    public void run() {
        while (true) {
            String message = "";
            DatagramPacket datagramPacket;

            while (!(message.equals("TOKEN"))) {
                datagramPacket = this.network.receiveMessage();
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
            this.network.sendTokenMessage(6665);
        }
    }

}
