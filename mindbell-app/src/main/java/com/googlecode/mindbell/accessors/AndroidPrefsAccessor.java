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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.googlecode.mindbell.R;
import com.googlecode.mindbell.util.TimeOfDay;

/**
 * @author marc
 * 
 */
public class AndroidPrefsAccessor extends PrefsAccessor {

    private final SharedPreferences settings;
    private final Context context;
    private final String[] hours;

    public AndroidPrefsAccessor(Context context) {
        this(PreferenceManager.getDefaultSharedPreferences(context), context);
    }

    public AndroidPrefsAccessor(SharedPreferences settings, Context context) {
        this.settings = settings;
        this.context = context;
        hours = context.getResources().getStringArray(R.array.hourStrings);
    }

    @Override
    public boolean doShowBell() {
        return settings.getBoolean(context.getString(R.string.keyShow), true);
    }

    @Override
    public boolean doStatusNotification() {
        return settings.getBoolean(context.getString(R.string.keyStatus), false);
    }

    @Override
    public TimeOfDay getDaytimeEnd() {
        return new TimeOfDay(getDaytimeEndHour(), 0);
    }

    /**
     * @return
     */
    private int getDaytimeEndHour() {
        return Integer.valueOf(settings.getString(context.getString(R.string.keyEnd), "0"));
    }

    @Override
    public String getDaytimeEndString() {
        return hours[getDaytimeEndHour()];
    }

    @Override
    public TimeOfDay getDaytimeStart() {
        return new TimeOfDay(getDaytimeStartHour(), 0);
    }

    private int getDaytimeStartHour() {
        return Integer.valueOf(settings.getString(context.getString(R.string.keyStart), "0"));
    }

    @Override
    public String getDaytimeStartString() {
        return hours[getDaytimeStartHour()];
    }

    // @Override
    // public long getInterval() {
    // int frequencySetting = Integer.valueOf(settings.getString(context.getString(R.string.keyFrequency), "0"));
    // long interval;
    // switch (frequencySetting) {
    // case 1: // half hour
    // interval = AlarmManager.INTERVAL_HALF_HOUR;
    // break;
    // case 2: // quarter of an hour
    // interval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    // break;
    // default:
    // interval = AlarmManager.INTERVAL_HOUR;
    // }
    // // interval = 30*1000; // 30 seconds
    // return interval;
    // }

    @Override
    public long getInterval() {
        return Long.valueOf(settings.getString(context.getString(R.string.keyFrequency), "3600000"));
    }

    @Override
    public boolean isBellActive() {
        return settings.getBoolean(context.getString(R.string.keyActive), false);
    }

}
