package Lamport;

public class DirectClock {

    private int id;                             // Process Id
    private int[] clock;                        // Clock from other processes

    public DirectClock(int numProc, int id) {
        this.id = id;
        this.clock = new int[numProc];
        for (int i = 0; i < numProc; i++) {
            clock[i] = 0;
        }
        clock[0] = 2;
        clock[1] = 0;
        clock[2] = 1;
        //clock[id] = 1;
    }

    public int getValue(int i) {
        return this.clock[i];
    }

    public void tick() {
        clock[id]++;
    }

    public void receiveAction(int sender, int sentValue) {
        clock[sender] = Math.max(this.clock[sender], sentValue);
        clock[this.id] = Math.max(this.clock[id], sentValue) + 1;
    }

}
