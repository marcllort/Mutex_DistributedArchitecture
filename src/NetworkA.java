import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.Semaphore;

public class NetworkA {

    private static final int PORT = 5001;
    private static final int TOKEN_PORT = 5004;
    private static final int N = 3;
    private static final String IP = "127.0.0.1";

    private DataOutputStream dataOut;
    private int id;
    private Lamport lamportMutex;
    private boolean firstTime = true;
    private Semaphore semaphore = new Semaphore(1, true);

    public void broadcastMessage(int time, String ack) {
        try {
            semaphore.acquire();
            System.out.println("BROADCAST - " + time + " - " + ack);
            dataOut.writeUTF("broadcast");
            dataOut.writeInt(time);
            dataOut.writeUTF(ack);
            semaphore.release();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void directMessage(int destination, int time, String ack) {
        try {
            semaphore.acquire();
            dataOut.writeUTF("message");
            dataOut.writeInt(destination);
            dataOut.writeInt(time);
            dataOut.writeUTF(ack);
            semaphore.release();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
