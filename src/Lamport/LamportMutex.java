package Lamport;

import Utils.Client;
import Utils.Constants;

import java.net.DatagramPacket;

import static Utils.Constants.*;
import static java.lang.Thread.sleep;


public class LamportMutex {
    private DirectClock v;              // DirectClock
    private int[] q;                    // Request queue, stores each LW timestamp
    private int id;                     // LW Process Id
    private Client client;              // Network helper class
    private int acks = 0;

    public LamportMutex(int id) {
        this.id = id;
        this.client = new Client(Constants.PORTS_LAMPORT[id], Constants.PORTS_LAMPORT);
        this.v = new DirectClock(client.getPorts().length, this.id);
        this.q = new int[client.getPorts().length];
        for (int j = 0; j < client.getPorts().length; j++) this.q[j] = Integer.MAX_VALUE;
    }

    public synchronized void requestCS() {
        v.tick();                                                                           // Increase/update timestamp
        q[id] = v.getValue(id);                                                             // Update value in the request list

        client.broadcastMessage(Constants.REQUEST_MSG, q[id]);

        while (!okayCS()) myWait();
    }

    boolean okayCS() {                                                                      // Order deciding logic
        for (int j = 0; j < client.getPorts().length; j++) {
            if (isGreater(q[id], id, q[j], j)) return false;
            if (isGreater(q[id], id, v.getValue(j), j)) return false;
        }
        if (acks < client.getPorts().length - 1) {
            return false;
        }
        return true;
    }

    boolean isGreater(int entry1, int pid1, int entry2, int pid2) {
        if (entry2 == Integer.MAX_VALUE) return false;
        return ((entry1 > entry2) || ((entry1 == entry2) && (pid1 > pid2)));
    }

    public void myWait() {
        try {
            DatagramPacket datagramPacket = client.receiveMessage();
            if (datagramPacket != null) {
                String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                String[] parts = message.split(MSG_SEPARATOR);
                if (parts[0].equals(ACK_MSG)) {
                    acks++;
                }
                handleMsg(Integer.parseInt(parts[1]), datagramPacket.getPort(), parts[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void handleMsg(int timeStamp, int src, String tag) {
        int id = client.getId(src);
        v.receiveAction(id, timeStamp);                                                     // Update Clock for the process, and your own clock

        if (tag.equals(Constants.REQUEST_MSG)) {
            q[id] = timeStamp;
            client.sendMessage(src, Constants.ACK_MSG, v.getValue(id));
        } else if (tag.equals(Constants.RELEASE_MSG)) q[id] = Integer.MAX_VALUE;

    }

    public synchronized void releaseCS() {
        q[id] = Integer.MAX_VALUE;
        client.broadcastMessage(Constants.RELEASE_MSG, v.getValue(id));
    }

    public void run() {
        try {
            while (true) {
                String message = "";
                DatagramPacket packet;

                while (isNotToken(message)) {                                                                           // Receive the Token that decides which algorithm is currently running
                    packet = client.receiveMessage();
                    message = new String(packet.getData(), 0, packet.getLength());
                }
                requestCS();
                for (int i = 0; i < 10; i++) {
                    System.out.println(PROCESS_MSG_A + (id + 1));
                    sleep(1000);
                }
                System.out.println("------------------------------");

                releaseCS();
                client.sendToken(Constants.PORT_HW_LAMPORT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isNotToken(String message) {
        return !(message.equals(Constants.TOKEN_MSG));
    }
}
