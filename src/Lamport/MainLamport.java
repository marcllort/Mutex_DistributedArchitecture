package Lamport;

import Utils.Client;
import Utils.Constants;
import Utils.HeavyWeightProcess;

public class MainLamport {
    public static void main(String args[]) {

        for (int i = 0; i < Constants.PORTS_LAMPORT.length; i++) {
            Client client = new Client(
                    Constants.PORTS_LAMPORT[i],
                    Constants.PORTS_LAMPORT
            );
            LamportMutex lamportMutex = new LamportMutex(i, client);
            lamportMutex.start();
        }

        HeavyWeightProcess.startHeavyWeigth(Constants.PORT_HW_LAMPORT, Constants.PORT_HW_RA, Constants.PORTS_LAMPORT);

    }
}
