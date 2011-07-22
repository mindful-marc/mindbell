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

import static com.googlecode.mindbell.MindBellPreferences.TAG;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

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

    private final String keyShow;
    private final String keyStatus;
    private final String keyEnd;
    private final String keyStart;
    private final String keyFrequency;
    private final String keyActive;
    private final String keyVolume;
    private final String keyMuteOffHook;
    private final String keyMuteWithPhone;

    public AndroidPrefsAccessor(Context context) {
        this(PreferenceManager.getDefaultSharedPreferences(context), context);
    }

    public AndroidPrefsAccessor(SharedPreferences settings, Context context) {
        this.settings = settings;
        this.context = context;
        hours = context.getResources().getStringArray(R.array.hourStrings);

        keyShow = context.getString(R.string.keyShow);
        keyStatus = context.getString(R.string.keyStatus);
        keyEnd = context.getString(R.string.keyEnd);
        keyStart = context.getString(R.string.keyStart);
        keyFrequency = context.getString(R.string.keyFrequency);
        keyActive = context.getString(R.string.keyActive);
        keyVolume = context.getString(R.string.keyVolume);
        keyMuteOffHook = context.getString(R.string.keyMuteOffHook);
        keyMuteWithPhone = context.getString(R.string.keyMuteWithPhone);
        checkSettings();
    }

    /**
     * Check that any data in the SharedPreferences are of the expected type. Should we find anything that doesn't fit the
     * expectations, we delete it.
     */
    private void checkSettings() {
        // boolean settings:
        String[] booleanSettings = new String[] { keyShow, keyStatus, keyActive, keyMuteOffHook, keyMuteWithPhone };
        for (String s : booleanSettings) {
            try {
                settings.getBoolean(s, false);
            } catch (ClassCastException e) {
                settings.edit().remove(s).commit();
            }
        }
        // string settings:
        String[] stringSettings = new String[] { keyStart, keyEnd, keyFrequency };
        for (String s : stringSettings) {
            try {
                settings.getString(s, null);
            } catch (ClassCastException e) {
                settings.edit().remove(s).commit();
            }
        }
        // int settings:
        String[] intSettings = new String[] { keyVolume };
        for (String s : intSettings) {
            try {
                settings.getInt(s, 0);
            } catch (ClassCastException e) {
                settings.edit().remove(s).commit();
            }
        }

    }

    @Override
    public boolean doShowBell() {
        return settings.getBoolean(keyShow, true);
    }

    @Override
    public boolean doStatusNotification() {
        return settings.getBoolean(keyStatus, false);
    }

    @Override
    public int getBellVolume(int defaultVolume) {
        return settings.getInt(keyVolume, defaultVolume);
    }

    @Override
    public TimeOfDay getDaytimeEnd() {
        return new TimeOfDay(getDaytimeEndHour(), 0);
    }

    /**
     * @return
     */
    private int getDaytimeEndHour() {
        return Integer.valueOf(settings.getString(keyEnd, "0"));
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
        return Integer.valueOf(settings.getString(keyStart, "0"));
    }

    @Override
    public String getDaytimeStartString() {
        return hours[getDaytimeStartHour()];
    }

    @Override
    public long getInterval() {
        Log.d(TAG, "frequency: " + settings.getString(keyFrequency, "3600000"));
        long interval = Long.valueOf(settings.getString(keyFrequency, "3600000"));
        if (interval < 5 * 60000) { // min: 5 minutes
            interval = 5 * 60000;
        }
        return interval;
    }

    @Override
    public boolean isBellActive() {
        return settings.getBoolean(keyActive, false);
    }

    @Override
    public boolean isSettingMuteOffHook() {
        return settings.getBoolean(keyMuteOffHook, true);
    }

    @Override
    public boolean isSettingMuteWithPhone() {
        return settings.getBoolean(keyMuteWithPhone, true);
    }

}
