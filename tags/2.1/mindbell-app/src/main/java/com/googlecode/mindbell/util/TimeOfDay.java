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
public class TimeOfDay {

    public final int hour;
    public final int minute;

    /**
     * The current time of day, as provided by the Calendar.getInstance().
     */
    public TimeOfDay() {
        this(Calendar.getInstance());
    }

    public TimeOfDay(Calendar cal) {
        this.hour = cal.get(Calendar.HOUR_OF_DAY);
        this.minute = cal.get(Calendar.MINUTE);
    }

    public TimeOfDay(int hour, int minute) {
        if (hour > 23 || hour < 0) {
            throw new IllegalArgumentException("Hour must be between 0 and 23, but is " + hour);
        }
        if (minute > 59 || minute < 0) {
            throw new IllegalArgumentException("Hour must be between 0 and 59, but is " + hour);
        }
        this.hour = hour;
        this.minute = minute;
    }

    public TimeOfDay(long millisecondsSince1970) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millisecondsSince1970);
        this.hour = cal.get(Calendar.HOUR_OF_DAY);
        this.minute = cal.get(Calendar.MINUTE);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TimeOfDay)) {
            return false;
        }
        TimeOfDay t = (TimeOfDay) o;
        return t.hour == hour && t.minute == minute;
    }

    @Override
    public int hashCode() {
        return 60 * hour + minute;
    }

    public boolean isBefore(TimeOfDay other) {
        return hour < other.hour || hour == other.hour && minute < other.minute;
    }

    /**
     * Determine whether the present time is in the semi-open interval including start up to but excluding end. If start is after
     * end, the interval is understood to span midnight.
     * 
     * @param start
     * @param end
     * @return
     */
    public boolean isInInterval(TimeOfDay start, TimeOfDay end) {
        if (this.equals(start)) {
            return true;
        }
        if (start.isBefore(end)) {
            return start.isBefore(this) && this.isBefore(end);
        } else { // spanning midnight
            return start.isBefore(this) || this.isBefore(end);
        }
    }
}
