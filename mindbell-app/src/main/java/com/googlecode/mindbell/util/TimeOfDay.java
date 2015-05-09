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

import static com.googlecode.mindbell.MindBellPreferences.TAG;

import java.util.Calendar;
import java.util.Set;

import android.util.Log;

/**
 * @author marc
 *
 */
public class TimeOfDay {

    public final int hour;
    public final int minute;
    public final Integer weekday; // null or 1-Calendar.SUNDAY ... 7-Calendar.SATURDAY

    /**
     * The current time of day, as provided by the Calendar.getInstance().
     */
    public TimeOfDay() {
        this(Calendar.getInstance());
    }

    public TimeOfDay(Calendar cal) {
        this(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.DAY_OF_WEEK));
    }

    public TimeOfDay(int hour, int minute) {
        this(hour, minute, null);
    }

    public TimeOfDay(int hour, int minute, Integer weekday) {
        if (hour > 23 || hour < 0) {
            throw new IllegalArgumentException("Hour must be between 0 and 23, but is " + hour);
        }
        if (minute > 59 || minute < 0) {
            throw new IllegalArgumentException("Minute must be between 0 and 59, but is " + minute);
        }
        if (weekday != null && (weekday > 7 || weekday < 1)) {
            throw new IllegalArgumentException("Weekday must be between 1 and 7, but is " + weekday);
        }
        this.hour = hour;
        this.minute = minute;
        this.weekday = weekday;
    }

    public TimeOfDay(long millisecondsSince1970) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millisecondsSince1970);
        this.hour = cal.get(Calendar.HOUR_OF_DAY);
        this.minute = cal.get(Calendar.MINUTE);
        this.weekday = cal.get(Calendar.DAY_OF_WEEK);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TimeOfDay other = (TimeOfDay) obj;
        if (hour != other.hour) {
            return false;
        }
        if (minute != other.minute) {
            return false;
        }
        if (weekday == null) {
            if (other.weekday != null) {
                return false;
            }
        } else if (!weekday.equals(other.weekday)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + hour;
        result = prime * result + minute;
        result = prime * result + ((weekday == null) ? 0 : weekday.hashCode());
        return result;
    }

    /**
     * Returns true if this weekday is one on which the bell is active.
     *
     * @param activeOnDaysOfWeek
     * @return
     */
    public boolean isActiveOnThatDay(Set<Integer> activeOnDaysOfWeek) {
        boolean result = activeOnDaysOfWeek.contains(Integer.valueOf(weekday));
        Log.d(TAG, "  isActiveOnThatDay(" + toString() + ") -> " + result);
        return result;
    }

    /**
     * Return true if this time is earlier than the other regardless of the weekday.
     *
     * @param other
     * @return
     */
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
        if (this.isSameTime(start)) {
            return true;
        }
        if (start.isBefore(end)) {
            return start.isBefore(this) && this.isBefore(end);
        } else { // spanning midnight
            return start.isBefore(this) || this.isBefore(end);
        }
    }

    /**
     * Return true if this time is the same than the other regardless of the weekday.
     *
     * @param other
     * @return
     */
    public boolean isSameTime(TimeOfDay other) {
        return hour == other.hour && minute == other.minute;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TimeOfDay [hour=" + hour + ", minute=" + minute + ", weekday=" + weekday + "]";
    }

}
