package RA;

import Utils.Client;
import Utils.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.LinkedList;

import static Utils.Constants.*;


public class RAMutex extends Thread {
    private int numOkay;
    private int myts;
    private int id;
    private Client client;
    private LamportClock c;
    private LinkedList<Integer> pendingQ;

    public RAMutex(int id) {
        this.id = id;
        this.client = new Client(Constants.PORTS_RA[id], Constants.PORTS_RA);
        this.myts = Integer.MAX_VALUE;
        this.pendingQ = new LinkedList<>();
        this.c = new LamportClock();
    }

    public synchronized void requestCS() {
        c.tick();
        myts = c.getValue();

        client.broadcastMessage(Constants.REQUEST_MSG, myts);
        numOkay = 0;
        while (numOkay < client.getPorts().length - 1) myWait();
    }

    public void myWait() {
        try {
            DatagramPacket datagramPacket = this.client.receiveMessage();
            if (datagramPacket != null) {
                String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                String[] parts = message.split(MSG_SEPARATOR);
                handleMsg(Integer.parseInt(parts[1]), datagramPacket.getPort(), parts[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void handleMsg(int timeStamp, int src, String tag) {
        int id = client.getId(src);
        c.receiveAction(timeStamp);

        if (tag.equals(Constants.REQUEST_MSG)) {
            if ((myts == Integer.MAX_VALUE) || (timeStamp < myts) || ((timeStamp == myts) && (id < this.id))) {
                client.sendMessage(src, OKAY_MSG, c.getValue());
            } else pendingQ.add(src);
        } else if (tag.equals(OKAY_MSG)) {
            numOkay++;
            if (numOkay == client.getPorts().length - 1) notify();
        }
    }

    public synchronized void releaseCS() {
        myts = Integer.MAX_VALUE;
        while (!pendingQ.isEmpty()) {
            int pid = pendingQ.removeFirst();
            client.sendMessage(pid, OKAY_MSG, c.getValue());
        }
    }

    public void run() {
        try {
            while (true) {
                String message = "";
                DatagramPacket packet;

                while (!(message.equals(Constants.TOKEN_MSG))) {
                    packet = client.receiveMessage();
                    message = new String(packet.getData(), 0, packet.getLength());
                }
                requestCS();
                for (int i = 0; i < 10; i++) {
                    System.out.println(PROCESS_MSG_B + (id + 1));
                    sleep(1000);
                }
                System.out.println("------------------------------");

                releaseCS();
                client.sendToken(Constants.PORT_HW_RA);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


