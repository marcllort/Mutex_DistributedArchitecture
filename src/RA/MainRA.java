package RA;

import Utils.Constants;
import Utils.HeavyWeightProcess;

public class MainRA {

    public static void main(String[] args) {
        HeavyWeightProcess.startHeavyWeight(Constants.PORT_HW_RA, Constants.PORT_HW_LAMPORT, Constants.PORTS_RA);
    }

}
