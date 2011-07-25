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

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.googlecode.mindbell.accessors.AndroidPrefsAccessor;

public class MindBellPreferences extends PreferenceActivity {
    /**
     * @author marc
     * 
     */
    private static final class ListChangeListener implements Preference.OnPreferenceChangeListener {
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
    }

    public static final String TAG = "MindBell";

    private final Preference.OnPreferenceChangeListener listChangeListener = new ListChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check settings, delete any settings that are not valid
        new AndroidPrefsAccessor(this);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        setupListPreference(R.string.keyFrequency);
        setupListPreference(R.string.keyStart);
        setupListPreference(R.string.keyEnd);

    }

    @Override
    public void onPause() {
        super.onPause();

        Intent intent = new Intent(this, UpdateBellSchedule.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            sender.send();
        } catch (PendingIntent.CanceledException e) {
            Log.e(TAG, "Could not send: " + e.getMessage());
        }
    }

    private void setupListPreference(int keyID) {
        ListPreference lp = (ListPreference) getPreferenceScreen().findPreference(getText(keyID));
        lp.setSummary(lp.getEntry());
        lp.setOnPreferenceChangeListener(listChangeListener);
    }

}