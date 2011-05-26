package com.googlecode.mindbell.util;

public class KeepAlive {

    private boolean               isDone = false;
    private final long            timeout;
    private final long            sleepDuration;

    private final ContextAccessor ca;

    public KeepAlive(ContextAccessor ca, long timeout) {
        this.ca = ca;
        this.timeout = timeout;
        this.sleepDuration = timeout / 10;
    }

    public void ringBell() {
        RingingLogic.ringBell(ca, new Runnable() {
            public void run() {
                setDone();
            }
        });
        long totalSlept = 0;
        while (!isDone && totalSlept < timeout) {
            try {
                Thread.sleep(sleepDuration);
            } catch (InterruptedException ie) {
            }
            totalSlept += sleepDuration;
        }
    }

    private void setDone() {
        isDone = true;
    }

}
