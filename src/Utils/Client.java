package Utils;

import java.io.IOException;
import java.net.*;

public class Client {

    private static int MAX_LENGTH = 100;

    private int port;
    private DatagramSocket socket;
    private InetAddress host;
    private int ports[];

    public Client(int my_sender_port, int ports[]) {
        try {
            this.port = my_sender_port;
            this.socket = new DatagramSocket(my_sender_port);
            host = InetAddress.getLocalHost();
            this.ports = ports;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(int port, String message, int clk) {
        String messageBuffer = message + "-" + clk;
        byte[] senderBuffer = messageBuffer.getBytes();

        DatagramPacket datagramPacket = new DatagramPacket(senderBuffer, senderBuffer.length);
        datagramPacket.setAddress(host);
        datagramPacket.setPort(port);

        try {
            this.socket.send(datagramPacket);
        } catch (IOException e) {
            System.err.println("Couldn't send the message");
        }
    }

    public void broadcastMessage(String message, int clk) {
        for (int port : this.ports) {
            if (port != this.port) {
                this.sendMessage(port, message, clk);
            }
        }
    }

    public DatagramPacket receiveMessage() throws IOException {
        byte[] receiverBuffer = new byte[MAX_LENGTH];
        DatagramPacket packetReceiver = new DatagramPacket(receiverBuffer, MAX_LENGTH);
        this.socket.receive(packetReceiver);
        return packetReceiver;
    }

    public void sendTokenMessage(int port) throws IOException {
        String message = Constants.TOKEN_MSG;
        byte[] senderBuffer = message.getBytes();
        DatagramPacket packetSender = new DatagramPacket(senderBuffer, senderBuffer.length, host, port);
        this.socket.send(packetSender);
    }

    public int getId(int port) {
        for (int i = 0; i < this.ports.length; i++) {
            if (port == this.ports[i]) return i;
        }
        System.err.println("Couldn't find port");
        return -1;
    }

    public int[] getPorts() {
        return ports;
    }

}

