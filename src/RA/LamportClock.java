package RA;

public class LamportClock {

    private int tics;

    public LamportClock() {
        this.tics = 0;
    }

    public LamportClock(int tics) {
        this.tics = tics;
    }

    public int getValue() {
        return tics;
    }

    public void tick() {
        tics++;
    }

    public void receiveAction(int receivedValue) {
        tics = Math.max(tics, receivedValue) + 1;
    }

}