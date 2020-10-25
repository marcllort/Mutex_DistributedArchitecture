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
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length,
                    InetAddress.getLocalHost(), 0);
            DatagramPacket packetReceiver = new DatagramPacket(new byte[MAX_LENGTH], MAX_LENGTH);

            while (true) {
                socket.receive(packetReceiver);
                // Send TOKEN to all LW ports, so they start the process and organize themselves with their algorithm
                for (int i = 0; i < ports.length; i++) {
                    packet.setPort(ports[i]);
                    socket.send(packet);
                }
                // Wait for all the LW's to finish
                for (int i = 0; i < ports.length; i++) {
                    socket.receive(packetReceiver);
                }
                // Send the TOKEN to the other HW process
                packet.setPort(dest_port);
                socket.send(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
