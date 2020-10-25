package Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static Utils.Constants.MAX_LENGTH;

public class Client {

    private int ownPort;
    private int ports[];
    private DatagramSocket socket;
    private InetAddress host;

    public Client(int ownPort, int ports[]) {
        try {
            this.ownPort = ownPort;
            this.ports = ports;
            this.socket = new DatagramSocket(ownPort);
            this.host = InetAddress.getLocalHost();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DatagramPacket receiveMessage() throws IOException {
        byte[] receiverBuffer = new byte[MAX_LENGTH];
        DatagramPacket packetReceiver = new DatagramPacket(receiverBuffer, MAX_LENGTH);
        socket.receive(packetReceiver);
        return packetReceiver;
    }

    public void sendMessage(int port, String message, int clock) {
        try {
            String messageBuffer = message + "-" + clock;
            socket.send(new DatagramPacket(messageBuffer.getBytes(),
                    messageBuffer.getBytes().length, host, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToken(int port) throws IOException {
        String message = Constants.TOKEN_MSG;
        DatagramPacket packetSender = new DatagramPacket(message.getBytes(), message.getBytes().length, host, port);
        socket.send(packetSender);
    }

    public void broadcastMessage(String message, int clock) {
        for (int port : ports) {
            if (port != ownPort) {
                sendMessage(port, message, clock);
            }
        }
    }

    public int getId(int port) {
        for (int i = 0; i < ports.length; i++) {
            if (port == ports[i]) return i;
        }
        return -1;
    }

    public int[] getPorts() {
        return this.ports;
    }

}

