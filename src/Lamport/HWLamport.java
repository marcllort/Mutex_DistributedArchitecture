package Lamport;

import Utils.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

public class HWLamport {

    private static int MAX_LENGTH = 100;

    public static void startHeavyWeigth(int port, int dest_port) {

        try {
            DatagramSocket socket = new DatagramSocket(port);
            String message = "TOKEN";
            DatagramPacket packetSender = new DatagramPacket(message.getBytes(), message.getBytes().length,
                    InetAddress.getLocalHost(), dest_port);
            DatagramPacket packetReceiver = new DatagramPacket(new byte[MAX_LENGTH], MAX_LENGTH);

            while (true) {
                socket.receive(packetReceiver);

                for (int i = 0; i < Constants.PORTS_LAMPORT.length; i++) {
                    packetSender.setPort(Constants.PORTS_LAMPORT[i]);
                    socket.send(packetSender);
                }
                for (int i = 0; i < Constants.PORTS_LAMPORT.length; i++) {
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
