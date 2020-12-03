package RA;

public class MainRALW {

    public static void main(String[] args) {
        RAMutex raMutex = new RAMutex(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        raMutex.run();
    }

}
