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

    private final String keyActive;
    private final String keyShow;
    private final String keyStatus;
    private final String keyMuteOffHook;
    private final String keyMuteWithPhone;

    private final String keyFrequency;
    private final String keyStart;
    private final String keyEnd;

    private final String keyVolume;

    private final boolean defaultActive = false;
    private final boolean defaultShow = true;
    private final boolean defaultStatus = true;
    private final boolean defaultMuteOffHook = true;
    private final boolean defaultMuteWithPhone = true;

    private final String defaultFrequency = "3600000";
    private final String defaultStart = "9";
    private final String defaultEnd = "21";

    private final int defaultVolume = 5;

    public AndroidPrefsAccessor(Context context) {
        this(PreferenceManager.getDefaultSharedPreferences(context), context);
    }

    public AndroidPrefsAccessor(SharedPreferences settings, Context context) {
        this.settings = settings;
        this.context = context;
        hours = context.getResources().getStringArray(R.array.hourStrings);

        keyActive = context.getString(R.string.keyActive);
        keyShow = context.getString(R.string.keyShow);
        keyStatus = context.getString(R.string.keyStatus);
        keyMuteOffHook = context.getString(R.string.keyMuteOffHook);
        keyMuteWithPhone = context.getString(R.string.keyMuteWithPhone);

        keyFrequency = context.getString(R.string.keyFrequency);
        keyStart = context.getString(R.string.keyStart);
        keyEnd = context.getString(R.string.keyEnd);

        keyVolume = context.getString(R.string.keyVolume);
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
                Log.w(TAG, "Removed setting '" + s + "' since it had wrong type");
            }
        }
        // string settings:
        String[] stringSettings = new String[] { keyStart, keyEnd, keyFrequency };
        for (String s : stringSettings) {
            try {
                settings.getString(s, null);
            } catch (ClassCastException e) {
                settings.edit().remove(s).commit();
                Log.w(TAG, "Removed setting '" + s + "' since it had wrong type");
            }
        }
        // int settings:
        String[] intSettings = new String[] { keyVolume };
        for (String s : intSettings) {
            try {
                settings.getInt(s, 0);
            } catch (ClassCastException e) {
                settings.edit().remove(s).commit();
                Log.w(TAG, "Removed setting '" + s + "' since it had wrong type");
            }
        }

        String frequencyString = settings.getString(keyFrequency, null);
        if (frequencyString != null) {
            try {
                long interval = Long.valueOf(frequencyString);
                if (interval < 5 * 60000) { // less than five minutes
                    settings.edit().remove(keyFrequency).commit();
                    Log.w(TAG, "Removed setting '" + keyFrequency + "' since value '" + frequencyString + "' was too low");
                }
            } catch (NumberFormatException e) {
                settings.edit().remove(keyFrequency).commit();
                Log.w(TAG, "Removed setting '" + keyFrequency + "' since value '" + frequencyString + "' is not a number");
            }
        }

        // Now set default values for those that are missing
        if (!settings.contains(keyActive)) {
            settings.edit().putBoolean(keyActive, defaultActive).commit();
            Log.w(TAG, "Reset missing setting for '" + keyActive + "' to '" + defaultActive + "'");
        }
        if (!settings.contains(keyShow)) {
            settings.edit().putBoolean(keyShow, defaultShow).commit();
            Log.w(TAG, "Reset missing setting for '" + keyShow + "' to '" + defaultShow + "'");
        }
        if (!settings.contains(keyStatus)) {
            settings.edit().putBoolean(keyStatus, defaultStatus).commit();
            Log.w(TAG, "Reset missing setting for '" + keyStatus + "' to '" + defaultStatus + "'");
        }
        if (!settings.contains(keyMuteOffHook)) {
            settings.edit().putBoolean(keyMuteOffHook, defaultMuteOffHook).commit();
            Log.w(TAG, "Reset missing setting for '" + keyMuteOffHook + "' to '" + defaultMuteOffHook + "'");
        }
        if (!settings.contains(keyMuteWithPhone)) {
            settings.edit().putBoolean(keyMuteWithPhone, defaultMuteWithPhone).commit();
            Log.w(TAG, "Reset missing setting for '" + keyMuteWithPhone + "' to '" + defaultMuteWithPhone + "'");
        }
        if (!settings.contains(keyFrequency)) {
            settings.edit().putString(keyFrequency, defaultFrequency).commit();
            Log.w(TAG, "Reset missing setting for '" + keyFrequency + "' to '" + defaultFrequency + "'");
        }
        if (!settings.contains(keyStart)) {
            settings.edit().putString(keyStart, defaultStart).commit();
            Log.w(TAG, "Reset missing setting for '" + keyStart + "' to '" + defaultStart + "'");
        }
        if (!settings.contains(keyEnd)) {
            settings.edit().putString(keyEnd, defaultEnd).commit();
            Log.w(TAG, "Reset missing setting for '" + keyEnd + "' to '" + defaultEnd + "'");
        }
        if (!settings.contains(keyVolume)) {
            settings.edit().putInt(keyVolume, defaultVolume).commit();
            Log.w(TAG, "Reset missing setting for '" + keyVolume + "' to '" + defaultVolume + "'");
        }
        // and report the settings:
        StringBuilder sb = new StringBuilder();
        sb.append("Effective settings:\n");
        for (String s : booleanSettings) {
            sb.append(s).append(" = ").append(settings.getBoolean(s, false)).append("\n");
        }
        for (String s : stringSettings) {
            sb.append(s).append(" = ").append(settings.getString(s, null)).append("\n");
        }
        for (String s : intSettings) {
            sb.append(s).append(" = ").append(settings.getInt(s, -1)).append("\n");
        }
        Log.d(TAG, sb.toString());
    }

    @Override
    public boolean doShowBell() {
        return settings.getBoolean(keyShow, defaultShow);
    }

    @Override
    public boolean doStatusNotification() {
        return settings.getBoolean(keyStatus, defaultStatus);
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
        return Integer.valueOf(settings.getString(keyEnd, defaultEnd));
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
        return Integer.valueOf(settings.getString(keyStart, defaultStart));
    }

    @Override
    public String getDaytimeStartString() {
        return hours[getDaytimeStartHour()];
    }

    @Override
    public long getInterval() {
        Log.d(TAG, "frequency: " + settings.getString(keyFrequency, defaultFrequency));
        long interval = Long.valueOf(settings.getString(keyFrequency, defaultFrequency));
        if (interval < 5 * 60000) { // min: 5 minutes
            interval = Long.valueOf(defaultFrequency);
        }
        return interval;
    }

    @Override
    public boolean isBellActive() {
        return settings.getBoolean(keyActive, defaultActive);
    }

    @Override
    public boolean isSettingMuteOffHook() {
        return settings.getBoolean(keyMuteOffHook, defaultMuteOffHook);
    }

    @Override
    public boolean isSettingMuteWithPhone() {
        return settings.getBoolean(keyMuteWithPhone, defaultMuteWithPhone);
    }

}
