/*
 * Copyright (C) 2010 Marc Schroeder.
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
package com.googlecode.mindbell;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.googlecode.mindbell.accessors.AndroidContextAccessor;

public class MindBellPreferences extends PreferenceActivity {
    public static final String LOGTAG = "MindBell";

    private String[] frequencies;
    private String[] hours;

    private final Preference.OnPreferenceChangeListener listChangeListener = new Preference.OnPreferenceChangeListener() {
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            assert preference instanceof ListPreference;
            ListPreference lp = (ListPreference) preference;
            int index = lp.findIndexOfValue((String) newValue);
            if (index != -1) {
                CharSequence newEntry = lp.getEntries()[index];
                lp.setSummary(newEntry);
                return true;
            }
            return false;
        }
    };

    private final Preference.OnPreferenceChangeListener volumeChangeListener = new Preference.OnPreferenceChangeListener() {
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            preference.setSummary(String.valueOf(newValue));
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        frequencies = getResources().getStringArray(R.array.bellFrequencies);
        hours = getResources().getStringArray(R.array.hourStrings);

        setupListPreference(R.string.keyFrequency);
        setupListPreference(R.string.keyStart);
        setupListPreference(R.string.keyEnd);

        // setupVolumeSlider();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Intent intent = new Intent(this, MindBellSwitch.class);
        // PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // try {
        // sender.send();
        // } catch (PendingIntent.CanceledException e) {
        // Log.e(LOGTAG, "Could not send: " + e.getMessage());
        // }
        AndroidContextAccessor.get(this).updateBellSchedule();
    }

    private void setupListPreference(int keyID) {
        ListPreference lp = (ListPreference) getPreferenceScreen().findPreference(getText(keyID));
        lp.setSummary(lp.getEntry());
        lp.setOnPreferenceChangeListener(listChangeListener);
    }

    // private void setupVolumeSlider() {
    // ContextAccessor ca = AndroidContextAccessor.get(this);
    // VolumePreference sbp = (VolumePreference) getPreferenceScreen().findPreference(getText(R.string.keyVolume));
    // sbp.setDefaultValue(ca.getBellDefaultVolume());
    // sbp.setMax(ca.getAlarmMaxVolume());
    // int currentVolume = PreferenceManager.getDefaultSharedPreferences(this).getInt(getString(R.string.keyVolume),
    // ca.getBellDefaultVolume());
    // sbp.setSummary(String.valueOf(currentVolume));
    // sbp.setOnPreferenceChangeListener(volumeChangeListener);
    // }

}