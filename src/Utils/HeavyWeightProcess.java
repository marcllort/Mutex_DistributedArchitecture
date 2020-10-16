package Utils;

import Utils.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

public class HeavyWeightProcess {

    private static int MAX_LENGTH = 100;

    public static void startHeavyWeigth(int port, int dest_port, int[] ports) {

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
