package RA;

import Utils.Client;
import Utils.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.LinkedList;

import static Utils.Constants.PROCESS_MSG_B;


public class RAMutex extends Thread {

    private int myts;
    private LamportClock c = new LamportClock(0);
    private LinkedList pendingQ = new LinkedList();
    private int numOkay = 0;

    private Client network;
    private int myId;

    public RAMutex(int myId, Client network) {
        this.myts = Integer.MAX_VALUE;
        this.network = network;
        this.myId = myId;
    }

    public void start() {
        String message = "";
        DatagramPacket datagramPacket = null;

        while (true) {

            while (!(message.equals(Constants.TOKEN_MSG))) {
                try {
                    datagramPacket = this.network.receiveMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

            }

            this.requestCS();

            for (int i = 0; i < 10; i++) {

                System.out.println(PROCESS_MSG_B + (this.myId + 1));

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            this.releaseCS();
            try {
                this.network.sendTokenMessage(6666);
            } catch (IOException e) {
                e.printStackTrace();
            }
            message = "";
        }
    }

    public synchronized void requestCS() {
        c.tick();
        myts = c.getC();
        this.network.broadcastMessage(Constants.REQUEST_MSG, myts);
        numOkay = 0;
        while (numOkay < this.network.getPorts().length - 1) {
            this.myWait();
        }
    }

    public synchronized void releaseCS() {
        myts = Integer.MAX_VALUE;
        while (!pendingQ.isEmpty()) {
            int src = (int) this.pendingQ.removeFirst();
            this.network.sendMessage(src, "okay", c.getC());
        }
    }

    public synchronized void handleMsg(int timeStamp, int src, String tag) {

        int idSrc = this.network.getId(src);

        c.receiveAction(idSrc, timeStamp);

        if (tag.equals(Constants.REQUEST_MSG)) {
            if ((this.myts == Integer.MAX_VALUE)
                    || (timeStamp < this.myts)
                    || ((timeStamp == this.myts) && (idSrc < this.myId))) {

                this.network.sendMessage(src, "okay", c.getC());
            } else {
                this.pendingQ.add(src);
            }
        } else if (tag.equals("okay")) {
            this.numOkay++;
            if (numOkay == this.network.getPorts().length - 1) {
                //no se que hacer aquí
            }
        }
    }

    public void myWait() {
        DatagramPacket datagramPacket = null;

        try {
            datagramPacket = this.network.receiveMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (datagramPacket != null) {
            String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
            String[] parts = message.split("-");
            this.handleMsg(Integer.valueOf(parts[1]), datagramPacket.getPort(), parts[0]);
        }
    }
}


