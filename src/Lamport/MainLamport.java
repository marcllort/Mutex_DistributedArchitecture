package Lamport;

import Utils.Constants;
import Utils.Client;

public class MainLamport {
    public static void main(String args[]) {

        for (int i = 0; i < 3; i++) {
            Client client = new Client(
                    Constants.PORTS_LAMPORT[i],
                    Constants.PORTS_LAMPORT
            );
            LamportMutex lamportMutex = new LamportMutex(i, client);
            lamportMutex.start();
        }

        HWLamport.startHeavyWeigth(Constants.PORT_HW_LAMPORT, Constants.PORT_HW_RA);

    }
}
