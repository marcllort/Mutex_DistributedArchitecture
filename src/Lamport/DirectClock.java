package Lamport;

public class DirectClock {

    private int id;
    private int[] clock;

    public DirectClock(int numProc, int id) {
        this.id = id;
        this.clock = new int[numProc];
        for (int i = 0; i < numProc; i++) {
            clock[i] = 0;
        }
        clock[id] = 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int[] getClock() {
        return clock;
    }

    public void setClock(int[] clock) {
        this.clock = clock;
    }

    public int getValue(int i) {
        return this.clock[i];
    }

    public void tick() {
        clock[id]++;
    }

    public void sendAction() {
        tick();
    }

    public void receiveAction(int sender, int sentValue) {
        clock[sender] = Math.max(this.clock[sender], sentValue);
        clock[this.id] = Math.max(this.clock[id], sentValue) + 1;
    }

}
