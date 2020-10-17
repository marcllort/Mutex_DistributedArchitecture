import Utils.Constants;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static Utils.Constants.PORT_HW;

public class Main {

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(PORT_HW);
            String message = Constants.TOKEN_MSG;

            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length,
                    InetAddress.getLocalHost(), Constants.PORT_HW_LAMPORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}