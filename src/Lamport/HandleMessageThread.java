package Lamport;

import java.io.DataInputStream;
import java.io.IOException;

class HandleMessageThread extends Thread {

    private DataInputStream dataIn;
    private LamportLogic lamportLogic;

    HandleMessageThread (DataInputStream dataIn, LamportLogic lamportLogic) {

        this.dataIn = dataIn;
        this.lamportLogic = lamportLogic;
    }

    @Override
    public void run() {

        int src;
        int timeStamp;
        String tag;

        while (true) {

            try {
                src = dataIn.readInt();
                timeStamp = dataIn.readInt();
                tag = dataIn.readUTF();

                lamportLogic.handleMsg(timeStamp, src, tag);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}