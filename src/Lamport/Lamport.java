package Lamport;

import utilities.Network;

import java.net.DatagramPacket;


public class Lamport extends Thread {
    private DirectClock v;
    private int[] q; //request queue
    private int myId = 0;
    private Network network;

    public Lamport(int id, Network network) {
        this.myId = id;
        v = new DirectClock(network.getOtherPorts().length, this.myId);
        this.q = new int[network.getOtherPorts().length];
        for (int j = 0; j < network.getOtherPorts().length; j++) {
            this.q[j] = Integer.MAX_VALUE;
        }
        this.network = network;
    }

    public void run() {
        String message = "";
        DatagramPacket datagramPacket;

        while (true) {

            while (!(message.equals("TOKEN"))) {

                datagramPacket = this.network.reciveMessage();
                message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

            }

            requestCS();

            for (int i = 0; i < 10; i++) {

                System.out.println("Soc el procés lightweight A" + (this.myId + 1));

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            releaseCS();
            this.network.sendTokenMessage(6665);
            message = "";
        }
    }

    public synchronized void requestCS() {
        v.tick();
        q[this.myId] = v.getValue(this.myId);
        network.broadcastMSG("request", q[myId]);
        while (!okeyCS()) {
            myWait();
        }
    }

    public synchronized void releaseCS() {
        q[myId] = Integer.MAX_VALUE;
        network.broadcastMSG("release", v.getValue(myId));
    }

    boolean okeyCS() {
        for (int j = 0; j < this.network.getOtherPorts().length; j++) {
            if (isGreater(q[myId], myId, q[j], j)) {
                return false;
            }
            if (isGreater(q[myId], myId, v.getValue(j), j)) {
                return false;
            }
        }
        return true;
    }

    boolean isGreater(int entry1, int pid1, int entry2, int pid2) {
        if (entry2 == Integer.MAX_VALUE) return false;
        return ((entry1 > entry2) || ((entry1 == entry2) && (pid1 > pid2)));
    }

    public synchronized void handleMsg(int timeStamp, int src, String tag) {
        int id = network.getPosition(src);

        if (id < 0) {
            System.out.println("ERROR: La conversión de puerto a id no salio bien");
            return;
        }

        v.receiveAction(id, timeStamp);

        if (tag.equals("request")) {
            this.q[id] = timeStamp;
            network.sentMessage(src, "ack", v.getValue(this.myId));

        } else if (tag.equals("release")) {
            this.q[id] = Integer.MAX_VALUE;

        }

    }

    public void myWait() {
        DatagramPacket datagramPacket;

        datagramPacket = this.network.reciveMessage();

        if (datagramPacket != null) {

            String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
            String[] parts = message.split("&");

            int portSender = Integer.valueOf(parts[0]);
            int timeStamp = Integer.valueOf(parts[2]);

            this.handleMsg(timeStamp, portSender, parts[1]);

        }

    }
}
