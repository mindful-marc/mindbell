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
package com.googlecode.mindbell.test.logic;

import static com.googlecode.mindbell.MindBellPreferences.TAG;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;

import junit.framework.TestCase;
import android.util.Log;

import com.googlecode.mindbell.accessors.PrefsAccessor;
import com.googlecode.mindbell.logic.SchedulerLogic;
import com.googlecode.mindbell.test.accessors.MockPrefsAccessor;
import com.googlecode.mindbell.util.TimeOfDay;

/**
 * @author marc
 *
 */
public class SchedulerLogicTest extends TestCase {

    private PrefsAccessor getDayPrefs() {
        MockPrefsAccessor dayPrefs = new MockPrefsAccessor();
        dayPrefs.setDaytimeStart(new TimeOfDay(9, 0));
        dayPrefs.setDaytimeEnd(new TimeOfDay(21, 0));
        dayPrefs.setActiveOnDaysOfWeek(new HashSet<Integer>(Arrays.asList(new Integer[] { 2, 3, 4, 5, 6 })));
        return dayPrefs;
    }

    private PrefsAccessor getNightPrefs() {
        MockPrefsAccessor nightPrefs = new MockPrefsAccessor();
        nightPrefs.setDaytimeStart(new TimeOfDay(13, 0));
        nightPrefs.setDaytimeEnd(new TimeOfDay(2, 0));
        nightPrefs.setActiveOnDaysOfWeek(new HashSet<Integer>(Arrays.asList(new Integer[] { 2, 3, 4, 5, 6 })));
        return nightPrefs;
    }

    private long getTimeMillis(int hour, int minute, int weekday) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.DAY_OF_WEEK, weekday);
        return cal.getTimeInMillis();
    }

    private int getWeekday(long timeMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMillis);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public void testRescheduleYieldsDayFriday1() {
        PrefsAccessor prefs = getDayPrefs();
        long nowTimeMillis = getTimeMillis(12, 00, Calendar.FRIDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.FRIDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDayFriday2() {
        PrefsAccessor prefs = getDayPrefs();
        long nowTimeMillis = getTimeMillis(01, 00, Calendar.FRIDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.FRIDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDayFriday3() {
        PrefsAccessor prefs = getDayPrefs();
        long nowTimeMillis = getTimeMillis(23, 00, Calendar.FRIDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.MONDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDayFriday4() {
        PrefsAccessor prefs = getDayPrefs();
        long nowTimeMillis = getTimeMillis(20, 59, Calendar.FRIDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.MONDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDayFriday5() {
        PrefsAccessor prefs = getNightPrefs();
        long nowTimeMillis = getTimeMillis(01, 00, Calendar.FRIDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.FRIDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDayFriday6() {
        PrefsAccessor prefs = getNightPrefs();
        long nowTimeMillis = getTimeMillis(12, 00, Calendar.FRIDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.FRIDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDayMonday1() {
        PrefsAccessor prefs = getDayPrefs();
        long nowTimeMillis = getTimeMillis(12, 00, Calendar.MONDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.MONDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDayMonday2() {
        PrefsAccessor prefs = getDayPrefs();
        long nowTimeMillis = getTimeMillis(01, 00, Calendar.MONDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.MONDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDayMonday3() {
        PrefsAccessor prefs = getDayPrefs();
        long nowTimeMillis = getTimeMillis(23, 00, Calendar.MONDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.TUESDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDayMonday4() {
        PrefsAccessor prefs = getDayPrefs();
        long nowTimeMillis = getTimeMillis(20, 59, Calendar.MONDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.TUESDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDayMonday5() {
        PrefsAccessor prefs = getNightPrefs();
        long nowTimeMillis = getTimeMillis(01, 00, Calendar.MONDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.MONDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDayMonday6() {
        PrefsAccessor prefs = getNightPrefs();
        long nowTimeMillis = getTimeMillis(12, 00, Calendar.MONDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.MONDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDaySaturday1() {
        PrefsAccessor prefs = getDayPrefs();
        long nowTimeMillis = getTimeMillis(12, 00, Calendar.SATURDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.MONDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDaySaturday2() {
        PrefsAccessor prefs = getDayPrefs();
        long nowTimeMillis = getTimeMillis(01, 00, Calendar.SATURDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.MONDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDaySaturday3() {
        PrefsAccessor prefs = getDayPrefs();
        long nowTimeMillis = getTimeMillis(23, 00, Calendar.SATURDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.MONDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDaySaturday4() {
        PrefsAccessor prefs = getDayPrefs();
        long nowTimeMillis = getTimeMillis(20, 59, Calendar.SATURDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.MONDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDaySaturday5() {
        PrefsAccessor prefs = getNightPrefs();
        long nowTimeMillis = getTimeMillis(01, 00, Calendar.SATURDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.MONDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDaySaturday6() {
        PrefsAccessor prefs = getNightPrefs();
        long nowTimeMillis = getTimeMillis(12, 00, Calendar.SATURDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.MONDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDaySunday1() {
        PrefsAccessor prefs = getDayPrefs();
        long nowTimeMillis = getTimeMillis(12, 00, Calendar.SUNDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.MONDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDaySunday2() {
        PrefsAccessor prefs = getDayPrefs();
        long nowTimeMillis = getTimeMillis(01, 00, Calendar.SUNDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.MONDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDaySunday3() {
        PrefsAccessor prefs = getDayPrefs();
        long nowTimeMillis = getTimeMillis(23, 00, Calendar.SUNDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.MONDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDaySunday4() {
        PrefsAccessor prefs = getDayPrefs();
        long nowTimeMillis = getTimeMillis(20, 59, Calendar.SUNDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.MONDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDaySunday5() {
        PrefsAccessor prefs = getNightPrefs();
        long nowTimeMillis = getTimeMillis(01, 00, Calendar.SUNDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.MONDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

    public void testRescheduleYieldsDaySunday6() {
        PrefsAccessor prefs = getNightPrefs();
        long nowTimeMillis = getTimeMillis(12, 00, Calendar.SUNDAY);
        long targetTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowTimeMillis, prefs);
        Log.d(TAG, (new TimeOfDay(nowTimeMillis)).toString() + " -> " + (new TimeOfDay(targetTimeMillis)).toString());
        assertTrue(targetTimeMillis > nowTimeMillis);
        assertEquals(Calendar.MONDAY, getWeekday(targetTimeMillis));
        assertTrue(prefs.isDaytime(new TimeOfDay(targetTimeMillis)));
    }

}
