public class Lamport {

    private static final int N = 3;

    private DirectClock v;
    private int[] q;
    private int id = 0;
    private NetworkA networkA;

    public Lamport(int id, NetworkA networkA) {
        this.id = id;
        this.networkA = networkA;

        this.v = new DirectClock(N, id);
        this.q = new int[N];

        for (int i = 0; i < N; i++) {
            q[i] = Integer.MAX_VALUE;
        }
    }

    public synchronized void requestCS() {
        //System.out.println("REQUEST");
        v.tick();
        q[this.id] = v.getValue(this.id);
        networkA.broadcastMessage(q[id], "request");

        while (!okayCS()) myWait();
    }

    public synchronized void releaseCS() {
        //System.out.println("RELEASE");
        q[this.id] = Integer.MAX_VALUE;
        networkA.broadcastMessage(v.getValue(this.id), "release");
    }

    private boolean okayCS() {
        for (int j = 0; j < N; j++) {
            if (isGreater(q[id], id, q[j], j)) return false;
            if (isGreater(q[id], id, v.getValue(j), j)) return false;
        }

        return true;
    }

    private boolean isGreater(int entry1, int pid1, int entry2, int pid2) {
        if (entry2 == Integer.MAX_VALUE) return false;
        return ((entry1 > entry2) || ((entry1 == entry2) && (pid1 > pid2)));
    }

    private void myWait() {
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void handleMsg(int timeStamp, int src, String tag) {
        v.receiveAction(src, timeStamp);
        if (tag.equals("request")) {
            q[src] = timeStamp;
            networkA.directMessage(src, v.getValue(this.id), "ack");
        } else if (tag.equals("release")) {
            q[src] = Integer.MAX_VALUE;
        }

        this.notify();
    }

}
