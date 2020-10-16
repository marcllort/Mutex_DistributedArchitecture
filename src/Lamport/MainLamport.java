package Lamport;

import constants.Constants;
import utilities.Network;

import java.io.IOException;

public class MainLamport {
    public static void main(String args[]){




        //creo els 3 lw de lamport
        for (int i = 0 ; i < 3; i++){
            Network network = new Network(
                    Constants.PORTS_LAMPORT[i],
                    Constants.PORTS_LAMPORT
            );
            Lamport lamport = new Lamport(i, network);
            lamport.start();
        }
        HWLamport hwLamport = new HWLamport();
        try {
            hwLamport.startHeavyWeigth(Constants.PORT_HW_LAMPORT,Constants.PORT_HW_RA);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
