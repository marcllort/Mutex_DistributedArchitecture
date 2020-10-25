package Lamport;

import Utils.Constants;
import Utils.HeavyWeightProcess;

public class MainLamport {

    public static void main(String[] args) {
        HeavyWeightProcess.startHeavyWeight(Constants.PORT_HW_LAMPORT, Constants.PORT_HW_RA, Constants.PORTS_LAMPORT);
    }

}
