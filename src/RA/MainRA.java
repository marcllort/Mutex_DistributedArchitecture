package RA;

import Lamport.LamportMutex;
import Utils.Client;
import Utils.Constants;
import Utils.HeavyWeightProcess;

public class MainRA {
    public static void main(String args[]) {

        for (int i = 0; i < Constants.PORTS_RA.length; i++) {
            Client client = new Client(
                    Constants.PORTS_RA[i],
                    Constants.PORTS_RA
            );
            RAMutex raMutex = new RAMutex(i, client);
            raMutex.start();
        }

        HeavyWeightProcess.startHeavyWeigth(Constants.PORT_HW_RA, Constants.PORT_HW_LAMPORT, Constants.PORTS_RA);

    }
}
