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
package com.googlecode.mindbell.util;

import java.util.Calendar;

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

    public abstract int getDaytimeStart();

    public abstract int getDaytimeEnd();

    public boolean isDaytime() {
        int tStart = getDaytimeStart();
        int tEnd = getDaytimeEnd();
        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);
        int currentTime = 100 * currentHour + currentMinute;
    
        // Some people may set the end to midnight, or 1am etc.
        // Two cases:
        // a) start < end: it is day time if start <= t <= end
        // b) end <= start: it is day time if either t <= end or start <= t
        // (i.e. if end == start, we treat things as always day time)
        if (tStart < tEnd) {
            return (tStart <= currentTime && currentTime < tEnd);
        } else {
            return (currentTime <= tEnd || tStart <= currentTime);
        }
    }

    public long getNextDaytimeStartInMillis() {
        int tStart = getDaytimeStart();
        Calendar morning = Calendar.getInstance();
        morning.add(Calendar.DATE, 1);
        morning.set(Calendar.HOUR_OF_DAY, tStart / 100);
        morning.set(Calendar.MINUTE, tStart % 100);
        morning.set(Calendar.SECOND, 0);
        return morning.getTimeInMillis();
    }

    public long getNextDaytimeEndInMillis() {
        int tStart = getDaytimeStart();
        int tEnd = getDaytimeEnd();
        Calendar evening = Calendar.getInstance();
        evening.set(Calendar.HOUR_OF_DAY, tEnd / 100);
        evening.set(Calendar.MINUTE, tEnd % 100);
        evening.set(Calendar.SECOND, 0);
        if (tEnd <= tStart) { // end time is in the early hours of next day
            evening.add(Calendar.DATE, 1);
        }
        return evening.getTimeInMillis();
    }

    public abstract boolean doShowBell();

    public abstract boolean isBellActive();

    public abstract long getInterval();

    public abstract String getDaytimeStartString();

    public abstract String getDaytimeEndString();

    public abstract boolean doStatusNotification();

}