import Utils.Constants;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static Utils.Constants.PORT_HW;

// How to use
// 1. Start MainLamportLW (3 instances, with id as param)
// 2. Start MainRALW (2 instances, with id as param)
// 3. Start MainLamport
// 4. Start MainRA
// 5. Start Main

public class Main {

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(PORT_HW);
            String message = Constants.TOKEN_MSG;

            // Sends Token to Lamport HW, to start the process
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length,
                    InetAddress.getLocalHost(), Constants.PORT_HW_LAMPORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}