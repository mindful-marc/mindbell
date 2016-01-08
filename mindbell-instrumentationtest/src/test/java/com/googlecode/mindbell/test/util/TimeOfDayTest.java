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
package com.googlecode.mindbell.test.util;

import java.util.Calendar;

import junit.framework.TestCase;

import com.googlecode.mindbell.util.TimeOfDay;

/**
 * @author marc
 *
 */
public class TimeOfDayTest extends TestCase {

    public void testBefore1() {
        TimeOfDay t1 = new TimeOfDay(9, 1);
        TimeOfDay t2 = new TimeOfDay(9, 2);
        assertTrue(t1.isBefore(t2));
        assertFalse(t2.isBefore(t1));
    }

    public void testBefore2() {
        TimeOfDay t1 = new TimeOfDay(8, 5);
        TimeOfDay t2 = new TimeOfDay(9, 2);
        assertTrue(t1.isBefore(t2));
        assertFalse(t2.isBefore(t1));
    }

    public void testCalendarConstructor1() {
        int hour = 13;
        int minute = 17;
        int weekday = 1;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.DAY_OF_WEEK, weekday);
        TimeOfDay t = new TimeOfDay(cal);
        assertEquals(new TimeOfDay(hour, minute, weekday), t);
    }

    public void testCalendarConstructor2() {
        int hour = 13;
        int minute = 17;
        int weekday = 2;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.DAY_OF_WEEK, weekday);
        TimeOfDay t = new TimeOfDay(cal);
        assertEquals(new TimeOfDay(hour, minute, weekday), t);
    }

    public void testCalendarConstructor3() {
        int hour = 13;
        int minute = 17;
        int weekday = 3;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.DAY_OF_WEEK, weekday);
        TimeOfDay t = new TimeOfDay(cal);
        assertEquals(new TimeOfDay(hour, minute, weekday), t);
    }

    public void testCalendarConstructor4() {
        int hour = 13;
        int minute = 17;
        int weekday = 4;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.DAY_OF_WEEK, weekday);
        TimeOfDay t = new TimeOfDay(cal);
        assertEquals(new TimeOfDay(hour, minute, weekday), t);
    }

    public void testCalendarConstructor5() {
        int hour = 13;
        int minute = 17;
        int weekday = 5;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.DAY_OF_WEEK, weekday);
        TimeOfDay t = new TimeOfDay(cal);
        assertEquals(new TimeOfDay(hour, minute, weekday), t);
    }

    public void testCalendarConstructor6() {
        int hour = 13;
        int minute = 17;
        int weekday = 6;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.DAY_OF_WEEK, weekday);
        TimeOfDay t = new TimeOfDay(cal);
        assertEquals(new TimeOfDay(hour, minute, weekday), t);
    }

    public void testCalendarConstructor7() {
        int hour = 13;
        int minute = 17;
        int weekday = 7;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.DAY_OF_WEEK, weekday);
        TimeOfDay t = new TimeOfDay(cal);
        assertEquals(new TimeOfDay(hour, minute, weekday), t);
    }

    public void testGetters1() {
        int hour = 1;
        int minute = 2;
        TimeOfDay t = new TimeOfDay(hour, minute);
        assertEquals(hour, t.hour);
        assertEquals(minute, t.minute);
        assertNull(t.weekday);
    }

    public void testGetters2() {
        int hour = 1;
        int minute = 2;
        int weekday = 5;
        TimeOfDay t = new TimeOfDay(hour, minute, weekday);
        assertEquals(hour, t.hour);
        assertEquals(minute, t.minute);
        assertEquals(weekday, t.weekday.intValue());
    }

    public void testIdentity1() {
        TimeOfDay time = new TimeOfDay(9, 15);
        assertEquals(new TimeOfDay(9, 15), time);
    }

    public void testIdentity2() {
        TimeOfDay time = new TimeOfDay(9, 15, 1);
        assertEquals(new TimeOfDay(9, 15, 1), time);
    }

    public void testIdentity3() {
        TimeOfDay time = new TimeOfDay(9, 15, 1);
        assertFalse(time.equals(new TimeOfDay(9, 15, 2)));
    }

    public void testInterval1() {
        TimeOfDay start = new TimeOfDay(9, 0);
        TimeOfDay end = new TimeOfDay(21, 0);
        TimeOfDay t = new TimeOfDay(12, 15);
        assertTrue(t.isInInterval(start, end));
    }

    public void testInterval2() {
        TimeOfDay start = new TimeOfDay(9, 0);
        TimeOfDay end = new TimeOfDay(9, 0);
        TimeOfDay t = new TimeOfDay(9, 0);
        assertTrue(t.isInInterval(start, end));
    }

    public void testInterval3() {
        TimeOfDay start = new TimeOfDay(9, 0);
        TimeOfDay end = new TimeOfDay(9, 1);
        TimeOfDay t = new TimeOfDay(9, 0);
        assertTrue(t.isInInterval(start, end));
    }

    public void testInterval4() {
        TimeOfDay start = new TimeOfDay(9, 0);
        TimeOfDay end = new TimeOfDay(9, 1);
        TimeOfDay t = new TimeOfDay(9, 1);
        assertFalse(t.isInInterval(start, end));
    }

    public void testInterval5() {
        TimeOfDay start = new TimeOfDay(9, 0);
        TimeOfDay end = new TimeOfDay(9, 59);
        TimeOfDay t = new TimeOfDay(10, 37);
        assertFalse(t.isInInterval(start, end));
    }

    public void testInterval6() {
        TimeOfDay start = new TimeOfDay(9, 0, 2);
        TimeOfDay end = new TimeOfDay(21, 0, 1);
        TimeOfDay t = new TimeOfDay(12, 15, 3);
        assertTrue(t.isInInterval(start, end));
    }

    public void testInterval7() {
        TimeOfDay start = new TimeOfDay(9, 0, 5);
        TimeOfDay end = new TimeOfDay(9, 59, 6);
        TimeOfDay t = new TimeOfDay(10, 37, 7);
        assertFalse(t.isInInterval(start, end));
    }

    public void testInterval8() {
        TimeOfDay start = new TimeOfDay(13, 0, null);
        TimeOfDay end = new TimeOfDay(2, 0, null);
        TimeOfDay t = new TimeOfDay(13, 0, 7);
        assertTrue(t.isInInterval(start, end));
    }

    public void testInvalid1() {
        try {
            new TimeOfDay(25, 0);
        } catch (IllegalArgumentException iae) {
            // expected
            return;
        }
        fail("Should have thrown an IllegalArgumentException");
    }

    public void testInvalid2() {
        try {
            new TimeOfDay(-1, 0);
        } catch (IllegalArgumentException iae) {
            // expected
            return;
        }
        fail("Should have thrown an IllegalArgumentException");
    }

    public void testInvalid3() {
        try {
            new TimeOfDay(5, 60);
        } catch (IllegalArgumentException iae) {
            // expected
            return;
        }
        fail("Should have thrown an IllegalArgumentException");
    }

    public void testInvalid4() {
        try {
            new TimeOfDay(5, -1);
        } catch (IllegalArgumentException iae) {
            // expected
            return;
        }
        fail("Should have thrown an IllegalArgumentException");
    }

    public void testInvalid5() {
        try {
            new TimeOfDay(5, 5, 0);
        } catch (IllegalArgumentException iae) {
            // expected
            return;
        }
        fail("Should have thrown an IllegalArgumentException");
    }

    public void testInvalid6() {
        try {
            new TimeOfDay(6, 6, 8);
        } catch (IllegalArgumentException iae) {
            // expected
            return;
        }
        fail("Should have thrown an IllegalArgumentException");
    }

    public void testIsSameTime1() {
        TimeOfDay time = new TimeOfDay(9, 15);
        assertTrue(time.isSameTime(new TimeOfDay(9, 15)));
    }

    public void testIsSameTime2() {
        TimeOfDay time = new TimeOfDay(9, 15, 1);
        assertTrue(time.isSameTime(new TimeOfDay(9, 15, 2)));
    }

    public void testIsSameTime3() {
        TimeOfDay time = new TimeOfDay(9, 15, null);
        assertTrue(time.isSameTime(new TimeOfDay(9, 15, 2)));
    }

    public void testMillisecondConstructor1() {
        int hour = 13;
        int minute = 17;
        int weekday = 1;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.DAY_OF_WEEK, weekday);
        TimeOfDay t = new TimeOfDay(cal.getTimeInMillis());
        assertEquals(new TimeOfDay(hour, minute, weekday), t);
    }

    public void testMillisecondConstructor2() {
        int hour = 13;
        int minute = 17;
        int weekday = 2;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.DAY_OF_WEEK, weekday);
        TimeOfDay t = new TimeOfDay(cal.getTimeInMillis());
        assertEquals(new TimeOfDay(hour, minute, weekday), t);
    }

    public void testMillisecondConstructor3() {
        int hour = 13;
        int minute = 17;
        int weekday = 3;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.DAY_OF_WEEK, weekday);
        TimeOfDay t = new TimeOfDay(cal.getTimeInMillis());
        assertEquals(new TimeOfDay(hour, minute, weekday), t);
    }

    public void testMillisecondConstructor4() {
        int hour = 13;
        int minute = 17;
        int weekday = 4;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.DAY_OF_WEEK, weekday);
        TimeOfDay t = new TimeOfDay(cal.getTimeInMillis());
        assertEquals(new TimeOfDay(hour, minute, weekday), t);
    }

    public void testMillisecondConstructor5() {
        int hour = 13;
        int minute = 17;
        int weekday = 5;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.DAY_OF_WEEK, weekday);
        TimeOfDay t = new TimeOfDay(cal.getTimeInMillis());
        assertEquals(new TimeOfDay(hour, minute, weekday), t);
    }

    public void testMillisecondConstructor6() {
        int hour = 13;
        int minute = 17;
        int weekday = 6;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.DAY_OF_WEEK, weekday);
        TimeOfDay t = new TimeOfDay(cal.getTimeInMillis());
        assertEquals(new TimeOfDay(hour, minute, weekday), t);
    }

    public void testMillisecondConstructor7() {
        int hour = 13;
        int minute = 17;
        int weekday = 7;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.DAY_OF_WEEK, weekday);
        TimeOfDay t = new TimeOfDay(cal.getTimeInMillis());
        assertEquals(new TimeOfDay(hour, minute, weekday), t);
    }

    public void testNightInterval1() {
        TimeOfDay start = new TimeOfDay(9, 0);
        TimeOfDay end = new TimeOfDay(1, 1);
        TimeOfDay t = new TimeOfDay(9, 1);
        assertTrue(t.isInInterval(start, end));
    }

    public void testNightInterval2() {
        TimeOfDay start = new TimeOfDay(9, 0);
        TimeOfDay end = new TimeOfDay(1, 1);
        TimeOfDay t = new TimeOfDay(0, 1);
        assertTrue(t.isInInterval(start, end));
    }

    public void testNightInterval3() {
        TimeOfDay start = new TimeOfDay(9, 0);
        TimeOfDay end = new TimeOfDay(1, 1);
        TimeOfDay t = new TimeOfDay(1, 1);
        assertFalse(t.isInInterval(start, end));
    }

    public void testNightInterval4() {
        TimeOfDay start = new TimeOfDay(23, 59);
        TimeOfDay end = new TimeOfDay(0, 0);
        TimeOfDay t = new TimeOfDay(9, 1);
        assertFalse(t.isInInterval(start, end));
    }

    public void testNightInterval5() {
        TimeOfDay start = new TimeOfDay(23, 59);
        TimeOfDay end = new TimeOfDay(0, 1);
        TimeOfDay t = new TimeOfDay(0, 0);
        assertTrue(t.isInInterval(start, end));
    }

    public void testNightInterval6() {
        TimeOfDay start = new TimeOfDay(23, 59, 4);
        TimeOfDay end = new TimeOfDay(0, 1, 2);
        TimeOfDay t = new TimeOfDay(0, 0, 1);
        assertTrue(t.isInInterval(start, end));
    }

    public void testNightInterval7() {
        TimeOfDay start = new TimeOfDay(9, 0, 7);
        TimeOfDay end = new TimeOfDay(1, 1, 1);
        TimeOfDay t = new TimeOfDay(1, 1, 5);
        assertFalse(t.isInInterval(start, end));
    }

}
