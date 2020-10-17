package RA;

import Utils.Client;
import Utils.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.LinkedList;

import static Utils.Constants.PROCESS_MSG_B;


public class RAMutex extends Thread {
    private int numOkay;
    private int myts;
    private int id;
    private Client client;
    private LamportClock c;
    private LinkedList pendingQ;

    public RAMutex(int id, Client client) {
        this.id = id;
        this.client = client;
        this.myts = Integer.MAX_VALUE;
        this.pendingQ = new LinkedList();
        this.c = new LamportClock();
    }

    public void myWait() {
        try {
            DatagramPacket datagramPacket = this.client.receiveMessage();
            if (datagramPacket != null) {
                String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                String[] parts = message.split("-");
                handleMsg(Integer.parseInt(parts[1]), datagramPacket.getPort(), parts[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void requestCS() {
        c.tick();
        myts = c.getValue();
        client.broadcastMessage(Constants.REQUEST_MSG, myts);
        numOkay = 0;
        while (numOkay < client.getPorts().length - 1) {
            myWait();
        }
    }

    public synchronized void releaseCS() {
        myts = Integer.MAX_VALUE;
        while (!pendingQ.isEmpty()) {
            int src = (int) pendingQ.removeFirst();
            client.sendMessage(src, "okay", c.getValue());
        }
    }

    public synchronized void handleMsg(int timeStamp, int src, String tag) {

        int idSrc = client.getId(src);
        c.receiveAction(timeStamp);

        if (tag.equals(Constants.REQUEST_MSG)) {
            if ((myts == Integer.MAX_VALUE)
                    || (timeStamp < myts)
                    || ((timeStamp == myts) && (idSrc < id))) {

                client.sendMessage(src, "okay", c.getValue());
            } else {
                pendingQ.add(src);
            }
        } else if (tag.equals("okay")) {
            numOkay++;
            if (numOkay == client.getPorts().length - 1) {
                //no se que hacer aquí
            }
        }
    }


    public void run() {
        String message = "";
        DatagramPacket datagramPacket = null;

        while (true) {

            while (!(message.equals(Constants.TOKEN_MSG))) {
                try {
                    datagramPacket = client.receiveMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

            }

            requestCS();

            for (int i = 0; i < 10; i++) {

                System.out.println(PROCESS_MSG_B + (id + 1));

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            releaseCS();
            try {
                client.sendTokenMessage(Constants.PORT_HW_RA);
            } catch (IOException e) {
                e.printStackTrace();
            }
            message = "";
        }
    }
}


