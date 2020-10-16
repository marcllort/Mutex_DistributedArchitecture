package utilities;

import java.io.IOException;
import java.net.*;

public class Network {

    private DatagramSocket mySocket;
    private InetAddress reciverHost;
    private int otherPorts[];
    private int myHWPort;
    final int MAX_LEN = 100;
    private int myPort;

    /**
     * @param my_sender_port
     * @param otherPorts
     */
    public Network(int my_sender_port, int otherPorts[]) {
        try {
            this.myPort = my_sender_port;
            this.mySocket = new DatagramSocket(my_sender_port);
            reciverHost = InetAddress.getLocalHost();
            this.otherPorts = otherPorts;

        } catch (SocketException e) {
            System.out.println("ERROR: sockets mal creados");
        } catch (UnknownHostException e) {
            System.out.println("ERROR: Unknown host");
        }
    }

    /**
     * @param message
     */
    public void sentMessage(int port, String message, int clk) {
        String messageBuffer = this.myPort + "&" + message + "&" + clk;
        byte[] senderBuffer = messageBuffer.getBytes();

        DatagramPacket datagramPacket = new DatagramPacket(senderBuffer, senderBuffer.length);
        datagramPacket.setAddress(reciverHost);

        datagramPacket.setPort(port);

        try {
            this.mySocket.send(datagramPacket);

        } catch (IOException e) {
            System.out.println("ERROR: No s'ha pogut enviar el packet!");
        }

    }

    public void broadcastMSG(String message, int clk) {
        for (int port : this.otherPorts) {

            if (port != this.myPort)
                this.sentMessage(port, message, clk);

        }
    }

    public DatagramPacket reciveTimeOutMessage() {
        byte[] reciverBuffer = new byte[MAX_LEN];
        DatagramPacket packetReciver = new DatagramPacket(reciverBuffer, MAX_LEN);


        try {

            this.mySocket.setSoTimeout(1);
            this.mySocket.receive(packetReciver);

        } catch (SocketTimeoutException e2) {
            return null;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return packetReciver;
    }

    public DatagramPacket reciveMessage() {
        byte[] reciverBuffer = new byte[MAX_LEN];
        DatagramPacket packetReciver = new DatagramPacket(reciverBuffer, MAX_LEN);

        try {
            this.mySocket.receive(packetReciver);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return packetReciver;
    }

    public void setTimeOut(int num) {
        try {
            this.mySocket.setSoTimeout(num);
        } catch (SocketException e) {
            System.out.println("ERROR: No he pogut assignar timeout");
        }
    }

    public int getPosition(int port) {
        for (int i = 0; i < this.otherPorts.length; i++) {
            if (port == this.otherPorts[i]) return i;
        }

        return -1;
    }

    public void sendTokenMessage(int port) {
        String message = "TOKEN";
        byte[] senderBuffer = message.getBytes();
        DatagramPacket packetSender = new DatagramPacket(senderBuffer, senderBuffer.length, reciverHost, port);
        try {
            this.mySocket.send(packetSender);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSockets() {
        this.mySocket.close();
    }

    public DatagramSocket getMySocket() {
        return mySocket;
    }

    public void setMySocket(int port) {

        try {
            this.mySocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public InetAddress getReciverHost() {
        return reciverHost;
    }

    public void setReciverHost(InetAddress reciverHost) {
        this.reciverHost = reciverHost;
    }

    public int[] getOtherPorts() {
        return otherPorts;
    }

    public void setOtherPorts(int[] otherPorts) {
        this.otherPorts = otherPorts;
    }

    public void setMyHWPort(int port){
        this.myHWPort = port;
    }

    public int getMyHWPort(){
        return this.myHWPort;
    }

}

