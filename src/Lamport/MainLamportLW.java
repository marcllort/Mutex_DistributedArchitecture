package Lamport;

public class MainLamportLW {

    public static void main(String[] args) {
        LamportMutex lamportMutex = new LamportMutex(Integer.parseInt(args[0]));
        lamportMutex.run();
    }

}
