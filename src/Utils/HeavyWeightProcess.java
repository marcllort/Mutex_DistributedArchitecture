package Utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static Utils.Constants.MAX_LENGTH;

public class HeavyWeightProcess {

    public static void startHeavyWeight(int port, int dest_port, int[] ports) {
        try {
            DatagramSocket socket = new DatagramSocket(port);
            String message = Constants.TOKEN_MSG;
            DatagramPacket packetSender = new DatagramPacket(message.getBytes(), message.getBytes().length,
                    InetAddress.getLocalHost(), dest_port);
            DatagramPacket packetReceiver = new DatagramPacket(new byte[MAX_LENGTH], MAX_LENGTH);

            while (true) {
                socket.receive(packetReceiver);

                for (int i = 0; i < ports.length; i++) {
                    packetSender.setPort(ports[i]);
                    socket.send(packetSender);
                }
                for (int i = 0; i < ports.length; i++) {
                    socket.receive(packetReceiver);
                }

                packetSender.setPort(dest_port);
                socket.send(packetSender);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
