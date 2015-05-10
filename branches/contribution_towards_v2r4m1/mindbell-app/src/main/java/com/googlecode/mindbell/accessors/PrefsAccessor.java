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
import java.util.Set;

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

    public abstract Set<Integer> getActiveOnDaysOfWeek();

    public abstract String getActiveOnDaysOfWeekString();

    public abstract float getBellVolume(float defaultVolume);

    public abstract TimeOfDay getDaytimeEnd();

    public abstract String getDaytimeEndString();

    public abstract TimeOfDay getDaytimeStart();

    public abstract String getDaytimeStartString();

    public abstract long getInterval();

    public long getNextDaytimeStartInMillis(long nightTimeMillis) {
        TimeOfDay tStart = getDaytimeStart();
        Calendar morning = Calendar.getInstance();
        morning.setTimeInMillis(nightTimeMillis);
        morning.set(Calendar.HOUR_OF_DAY, tStart.hour);
        morning.set(Calendar.MINUTE, tStart.minute);
        morning.set(Calendar.SECOND, 0);
        if (morning.getTimeInMillis() <= nightTimeMillis) { // today's start time has already passed
            morning.add(Calendar.DATE, 1); // therefore go to morning of next day
        }
        while (!(new TimeOfDay(morning)).isActiveOnThatDay(getActiveOnDaysOfWeek())) { // inactive on that day?
            morning.add(Calendar.DATE, 1); // therefore go to morning of next day
        }
        return morning.getTimeInMillis();
    }

    public abstract boolean isBellActive();

    /**
     * Returns true if the bell should ring now, method name carries the historical meaning.
     *
     * @return whether bell should ring
     */
    public boolean isDaytime() {
        return isDaytime(new TimeOfDay());
    }

    /**
     * Returns true if the bell should ring at the given TimeOfDay, so t must be in the active time interval and the weekday of t
     * must be activated. The method name carries the historical meaning.
     *
     * @return whether bell should ring
     */
    public boolean isDaytime(TimeOfDay t) {
        TimeOfDay tStart = getDaytimeStart();
        TimeOfDay tEnd = getDaytimeEnd();
        if (!t.isInInterval(tStart, tEnd)) {
            return false; // time is before or after active time interval
        }
        return t.isActiveOnThatDay(getActiveOnDaysOfWeek());
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