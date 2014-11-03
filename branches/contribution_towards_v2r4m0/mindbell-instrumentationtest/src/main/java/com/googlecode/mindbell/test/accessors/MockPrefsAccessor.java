package com.googlecode.mindbell.test.accessors;

import com.googlecode.mindbell.accessors.PrefsAccessor;
import com.googlecode.mindbell.util.TimeOfDay;

public class MockPrefsAccessor extends PrefsAccessor {

    private boolean showBell = true;

    private boolean statusNotification = true;

    private TimeOfDay daytimeEnd = new TimeOfDay(21, 0);

    private String daytimeEndString = "21:00";

    private TimeOfDay daytimeStart = new TimeOfDay(9, 0);

    private String daytimeStartString = "09:00";

    private long interval = 3600000;

    private boolean bellActive = true;

    @Override
    public boolean doShowBell() {
        return showBell;
    }

    @Override
    public boolean doStatusNotification() {
        return statusNotification;
    }

    @Override
    public float getBellVolume(float defaultVolume) {
        return 0.5f;
    }

    @Override
    public TimeOfDay getDaytimeEnd() {
        return daytimeEnd;
    }

    @Override
    public String getDaytimeEndString() {
        return daytimeEndString;
    }

    @Override
    public TimeOfDay getDaytimeStart() {
        return daytimeStart;
    }

    @Override
    public String getDaytimeStartString() {
        return daytimeStartString;
    }

    @Override
    public long getInterval() {
        return interval;
    }

    @Override
    public boolean isBellActive() {
        return bellActive;
    }

    /**
     * @param theBellActive
     *            the bellActive to set
     */
    public void setBellActive(boolean theBellActive) {
        this.bellActive = theBellActive;
    }

    /**
     * @param theDaytimeEnd
     *            the daytimeEnd to set
     */
    public void setDaytimeEnd(TimeOfDay theDaytimeEnd) {
        this.daytimeEnd = theDaytimeEnd;
    }

    /**
     * @param theDaytimeEndString
     *            the daytimeEndString to set
     */
    public void setDaytimeEndString(String theDaytimeEndString) {
        this.daytimeEndString = theDaytimeEndString;
    }

    /**
     * @param theDaytimeStart
     *            the daytimeStart to set
     */
    public void setDaytimeStart(TimeOfDay theDaytimeStart) {
        this.daytimeStart = theDaytimeStart;
    }

    /**
     * @param theDaytimeStartString
     *            the daytimeStartString to set
     */
    public void setDaytimeStartString(String theDaytimeStartString) {
        this.daytimeStartString = theDaytimeStartString;
    }

    /**
     * @param theInterval
     *            the interval to set
     */
    public void setInterval(long theInterval) {
        this.interval = theInterval;
    }

    /**
     * @param theShowBell
     *            the showBell to set
     */
    public void setShowBell(boolean theShowBell) {
        this.showBell = theShowBell;
    }

    /**
     * @param theStatusNotification
     *            the statusNotification to set
     */
    public void setStatusNotification(boolean theStatusNotification) {
        this.statusNotification = theStatusNotification;
    }

}
