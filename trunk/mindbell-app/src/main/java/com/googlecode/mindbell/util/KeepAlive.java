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
        int i = 0;
        while (!isDone && i < 10) {
            try {
                Thread.sleep(sleepDuration);
            } catch (InterruptedException ie) {
            }
            i++;
        }
    }

    private void setDone() {
        isDone = true;
    }

}
