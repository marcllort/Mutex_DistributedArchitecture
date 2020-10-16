package Lamport;

import Utils.Constants;
import Utils.Network;

import java.io.IOException;

public class MainLamport {
    public static void main(String args[]) {

        // Creo els 3 LW de Lamport
        for (int i = 0; i < 3; i++) {
            Network network = new Network(
                    Constants.PORTS_LAMPORT[i],
                    Constants.PORTS_LAMPORT
            );
            LamportMutex lamportMutex = new LamportMutex(i, network);
            lamportMutex.start();
        }

        HWLamport.startHeavyWeigth(Constants.PORT_HW_LAMPORT, Constants.PORT_HW_RA);

    }
}
