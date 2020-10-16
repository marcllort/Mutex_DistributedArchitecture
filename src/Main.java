import constants.Constants;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        try {


// write your code here
        //creem el socket per establir la connexió amb l'altre heavy weight
        DatagramSocket mySocket = new DatagramSocket(Constants.PORT_HW_RA);
        Random r = new Random();
        int result;
        /*Lamport lamport;

        for (int i = 0; i < arrayPorts.length; i++) {
            lamport = new Lamport(i, new Network(arrayPorts[i], arrayPorts));
            lamport.start();
        }*/
        //El missatge que enviarem a l'altre node el passem a bytes
        String message = "TOKEN";
        byte[] senderBuffer = message.getBytes();
        byte[] reciverBuffer = new byte[10000];

        //Agafem la direcció com a localhost
        InetAddress reciverHost = InetAddress.getLocalHost();

        //Creem el packet a enviar pel socket
        DatagramPacket packetSender = new DatagramPacket(senderBuffer, senderBuffer.length, reciverHost, Constants.PORT_HW_LAMPORT);
        DatagramPacket packetReciver = new DatagramPacket(reciverBuffer, 10000);


            //Enviem el packet
                System.out.println("ENVIO");
                packetSender.setPort(Constants.PORT_HW_LAMPORT);
                mySocket.send(packetSender);


        }catch (Exception e){

        }
    }


}