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


import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;

import com.googlecode.mindbell.R;

/**
 * @author marc
 * 
 */
public class AndroidPrefsAccessor extends PrefsAccessor {

    private final SharedPreferences settings;
    private final Context context;
    private final String[] hours;

    public AndroidPrefsAccessor(SharedPreferences settings, Context context) {
        this.settings = settings;
        this.context = context;
        hours = context.getResources().getStringArray(R.array.hourStrings);
    }

    @Override
    public boolean doStatusNotification() {
        return settings.getBoolean(context.getString(R.string.keyStatus), false);
    }

    @Override
    public int getDaytimeEnd() {
        return 100 * Integer.valueOf(settings.getString(context.getString(R.string.keyEnd), "0"));
    }

    @Override
    public String getDaytimeEndString() {
        int endStringIndex = Integer.valueOf(settings.getString(context.getString(R.string.keyEnd), "0"));
        return hours[endStringIndex];
    }

    @Override
    public int getDaytimeStart() {
        return 100 * Integer.valueOf(settings.getString(context.getString(R.string.keyStart), "0"));
    }

    @Override
    public String getDaytimeStartString() {
        int startStringIndex = Integer.valueOf(settings.getString(context.getString(R.string.keyStart), "0"));
        return hours[startStringIndex];
    }

    @Override
    public long getInterval() {
        int frequencySetting = Integer.valueOf(settings.getString(context.getString(R.string.keyFrequency), "0"));
        long interval;
        switch (frequencySetting) {
        case 1: // half hour
            interval = AlarmManager.INTERVAL_HALF_HOUR;
            break;
        case 2: // quarter of an hour
            interval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
            break;
        default:
            interval = AlarmManager.INTERVAL_HOUR;
        }
        // interval = 30*1000; // 30 seconds
        return interval;
    }

    @Override
    public boolean isBellActive() {
        return settings.getBoolean(context.getString(R.string.keyActive), false);
    }

    @Override
    public boolean doShowBell() {
        return settings.getBoolean(context.getString(R.string.keyShow), true);
    }

}
