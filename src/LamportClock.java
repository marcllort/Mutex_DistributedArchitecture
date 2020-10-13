public class LamportClock {
    private int c;

    public LamportClock(int c) {
        this.c = c;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public void tick() {
        c = c + 1;
    }

    public void sendAction() {
        tick();
    }

    public void receiveAction(int src, int sentValue) {
        c = Math.max(c, sentValue) + 1;
    }

}