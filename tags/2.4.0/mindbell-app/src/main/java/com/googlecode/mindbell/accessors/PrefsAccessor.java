/**
 * Copyright (C) 2011 Marc Schroeder.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.mindbell.accessors;

import java.util.Calendar;

import com.googlecode.mindbell.util.TimeOfDay;

/**
 * @author marc
 * 
 */
public abstract class PrefsAccessor {

    /**
     * 
     */
    public PrefsAccessor() {
        super();
    }

    public abstract boolean doShowBell();

    public abstract boolean doStatusNotification();

    public abstract float getBellVolume(float defaultVolume);

    public abstract TimeOfDay getDaytimeEnd();

    public abstract String getDaytimeEndString();

    public abstract TimeOfDay getDaytimeStart();

    public abstract String getDaytimeStartString();

    public abstract long getInterval();

    public long getNextDaytimeEndInMillis() {
        TimeOfDay tNow = new TimeOfDay();
        TimeOfDay tEnd = getDaytimeEnd();
        Calendar evening = Calendar.getInstance();
        evening.set(Calendar.HOUR_OF_DAY, tEnd.hour);
        evening.set(Calendar.MINUTE, tEnd.minute);
        evening.set(Calendar.SECOND, 0);
        if (tEnd.isBefore(tNow)) { // today's end time has already passed
            evening.add(Calendar.DATE, 1);
        }
        return evening.getTimeInMillis();
    }

    public long getNextDaytimeStartInMillis() {
        TimeOfDay tStart = getDaytimeStart();
        TimeOfDay tNow = new TimeOfDay();
        Calendar morning = Calendar.getInstance();
        morning.set(Calendar.HOUR_OF_DAY, tStart.hour);
        morning.set(Calendar.MINUTE, tStart.minute);
        morning.set(Calendar.SECOND, 0);
        if (tStart.isBefore(tNow)) { // today's start time has already passed
            morning.add(Calendar.DATE, 1);
        }
        return morning.getTimeInMillis();
    }

    public abstract boolean isBellActive();

    public boolean isDaytime() {
        return isDaytime(new TimeOfDay());
    }

    public boolean isDaytime(TimeOfDay t) {
        TimeOfDay tStart = getDaytimeStart();
        TimeOfDay tEnd = getDaytimeEnd();

        return t.isInInterval(tStart, tEnd);
    }

    public boolean isSettingMuteInFlightMode() {
        return true;
    }

    public boolean isSettingMuteOffHook() {
        return true;
    }

    public boolean isSettingMuteWithPhone() {
        return true;
    }

    public boolean isSettingVibrate() {
        return false;
    }

}