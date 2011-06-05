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

import com.googlecode.mindbell.util.PrefsAccessor;
import com.googlecode.mindbell.util.SchedulerLogic;
import com.googlecode.mindbell.util.TimeOfDay;

/**
 * @author marc
 * 
 */
public class SchedulerLogicTest extends TestCase {

    MockPrefsAccessor dayPrefs;
    MockPrefsAccessor nightPrefs;
    long timeMillis1200;
    long timeMillis0100;
    long timeMillis2300;
    long timeMillis2059;

    @Override
    public void setUp() {
        dayPrefs = new MockPrefsAccessor();
        dayPrefs.setDaytimeStart(new TimeOfDay(9, 0));
        dayPrefs.setDaytimeEnd(new TimeOfDay(21, 0));
        nightPrefs = new MockPrefsAccessor();
        nightPrefs.setDaytimeStart(new TimeOfDay(13, 0));
        nightPrefs.setDaytimeEnd(new TimeOfDay(2, 0));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        timeMillis1200 = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 1);
        timeMillis0100 = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        timeMillis2300 = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 20);
        cal.set(Calendar.MINUTE, 59);
        timeMillis2059 = cal.getTimeInMillis();
    }

    public void testRescheduleIsAfterTime() {
        PrefsAccessor prefs = dayPrefs;
        long timeMillis = timeMillis1200;
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(timeMillis, prefs);
        assertTrue(targetTimeMillis > timeMillis);
    }

    public void testRescheduleYieldsDay1() {
        PrefsAccessor prefs = dayPrefs;
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(timeMillis1200, prefs);
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDay2() {
        PrefsAccessor prefs = dayPrefs;
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(timeMillis0100, prefs);
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDay3() {
        PrefsAccessor prefs = dayPrefs;
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(timeMillis2300, prefs);
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDay4() {
        PrefsAccessor prefs = dayPrefs;
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(timeMillis2059, prefs);
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDay5() {
        PrefsAccessor prefs = nightPrefs;
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(timeMillis0100, prefs);
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDay6() {
        PrefsAccessor prefs = nightPrefs;
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(timeMillis1200, prefs);
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

}
