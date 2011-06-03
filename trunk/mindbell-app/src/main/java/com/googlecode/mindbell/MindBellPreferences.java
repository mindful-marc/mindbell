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

import java.util.HashMap;
import java.util.Map;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class MindBellPreferences extends PreferenceActivity {
    public static final String LOGTAG = "MindBell";
    public static final String ACTIVATEBELL = "mindBellActive";
    public static final String RESCHEDULEBELL = "mindBellReschedule";

    private String[] frequencies;
    private String[] hours;
    private final Map<Preference, String[]> listPrefStrings = new HashMap<Preference, String[]>();

    private final Preference.OnPreferenceChangeListener listChangeListener = new Preference.OnPreferenceChangeListener() {
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            // We have put the index numbers as values:
            int val = Integer.valueOf((String) newValue);
            String[] prefStrings = listPrefStrings.get(preference);
            if (prefStrings != null) {
                preference.setSummary(prefStrings[val]);
            }
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

        setupListPreference(R.string.keyFrequency, frequencies);
        setupListPreference(R.string.keyStart, hours);
        setupListPreference(R.string.keyEnd, hours);

        setupVolumePreference();

    }

    @Override
    public void onPause() {
        super.onPause();
        Intent intent = new Intent(this, MindBellSwitch.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            sender.send();
        } catch (PendingIntent.CanceledException e) {
            Log.e(LOGTAG, "Could not send: " + e.getMessage());
        }
    }

    private void setupListPreference(int keyID, String[] valueStrings) {
        ListPreference lp = (ListPreference) getPreferenceScreen().findPreference(getText(keyID));
        int val = Integer.valueOf(lp.getValue());
        lp.setSummary(valueStrings[val]);
        listPrefStrings.put(lp, valueStrings);
        lp.setOnPreferenceChangeListener(listChangeListener);
    }

    private void setupVolumePreference() {
        // Dynamically fill volumes from audio manager's value range:
        AudioManager audioMan = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioMan.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioMan.getStreamVolume(AudioManager.STREAM_MUSIC);
        assert 0 <= currentVolume;
        assert currentVolume <= maxVolume;
        String[] volumes = new String[maxVolume + 1];
        for (int i = 0; i <= maxVolume; i++) {
            volumes[i] = String.valueOf(i);
        }
        ListPreference lp = (ListPreference) getPreferenceScreen().findPreference(getText(R.string.keyVolume));
        lp.setEntries(volumes);
        lp.setEntryValues(volumes);
        // By default, ring bell at 2/3 of maximum volume:
        int defaultBellVolume = maxVolume * 2 / 3;
        lp.setDefaultValue(String.valueOf(defaultBellVolume));
        if (lp.getValue() == null) {
            lp.setValue(String.valueOf(defaultBellVolume));
        }
        lp.setSummary(lp.getValue());
        listPrefStrings.put(lp, volumes);
        lp.setOnPreferenceChangeListener(listChangeListener);
    }

}