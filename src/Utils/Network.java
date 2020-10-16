package Utils;

import java.io.IOException;
import java.net.*;

public class Network {

    private static int MAX_LEN = 100;

    private int myPort;
    private DatagramSocket socket;
    private InetAddress host;
    private int ports[];

    public Network(int my_sender_port, int ports[]) {
        try {
            this.myPort = my_sender_port;
            this.socket = new DatagramSocket(my_sender_port);
            host = InetAddress.getLocalHost();
            this.ports = ports;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(int port, String message, int clk) {
        String messageBuffer = this.myPort + "&" + message + "&" + clk;
        byte[] senderBuffer = messageBuffer.getBytes();

        DatagramPacket datagramPacket = new DatagramPacket(senderBuffer, senderBuffer.length);
        datagramPacket.setAddress(host);
        datagramPacket.setPort(port);

        try {
            this.socket.send(datagramPacket);
        } catch (IOException e) {
            System.err.println("ERROR: Couldn't send the message");
        }
    }

    public void broadcastMessage(String message, int clk) {
        for (int port : this.ports) {
            if (port != this.myPort) {
                this.sendMessage(port, message, clk);
            }
        }
    }

    public DatagramPacket receiveMessage() {
        byte[] receiverBuffer = new byte[MAX_LEN];
        DatagramPacket packetReceiver = new DatagramPacket(receiverBuffer, MAX_LEN);

        try {
            this.socket.receive(packetReceiver);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return packetReceiver;
    }

    public void sendTokenMessage(int port) {
        String message = "TOKEN";
        byte[] senderBuffer = message.getBytes();
        DatagramPacket packetSender = new DatagramPacket(senderBuffer, senderBuffer.length, host, port);
        try {
            this.socket.send(packetSender);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPosition(int port) {
        for (int i = 0; i < this.ports.length; i++) {
            if (port == this.ports[i]) return i;
        }
        return -1;
    }

    public int[] getPorts() {
        return ports;
    }

}

