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

    public void testCalendarConstructor() {
        int hour = 13;
        int minute = 17;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        TimeOfDay t = new TimeOfDay(cal);
        assertEquals(new TimeOfDay(hour, minute), t);
    }

    public void testGetters() {
        int hour = 1;
        int minute = 2;
        TimeOfDay t = new TimeOfDay(hour, minute);
        assertEquals(hour, t.hour);
        assertEquals(minute, t.minute);
    }

    public void testIdentity() {
        TimeOfDay time = new TimeOfDay(9, 15);
        // verify
        assertEquals(new TimeOfDay(9, 15), time);
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
        TimeOfDay t = new TimeOfDay(9, 1);
        assertFalse(t.isInInterval(start, end));
    }

    public void testInterval4() {
        TimeOfDay start = new TimeOfDay(9, 0);
        TimeOfDay end = new TimeOfDay(9, 59);
        TimeOfDay t = new TimeOfDay(10, 37);
        assertFalse(t.isInInterval(start, end));
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

    public void testMillisecondConstructor() {
        int hour = 13;
        int minute = 17;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        TimeOfDay t = new TimeOfDay(cal.getTimeInMillis());
        assertEquals(new TimeOfDay(hour, minute), t);
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
}
