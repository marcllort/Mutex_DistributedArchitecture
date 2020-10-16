import Utils.Constants;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main {

    public static void main(String[] args) {
        try {
            DatagramSocket mySocket = new DatagramSocket(Constants.PORT_HW_LAMPORT);
            String message = Constants.TOKEN_MSG;

            DatagramPacket packetSender = new DatagramPacket(message.getBytes(), message.getBytes().length,
                    InetAddress.getLocalHost(), Constants.PORT_HW_RA);
            packetSender.setPort(Constants.PORT_HW_RA);
            mySocket.send(packetSender);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}