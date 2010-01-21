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
import android.content.Intent;
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
	private Map<Preference, String[]> listPrefStrings = new HashMap<Preference, String[]>();


	private Preference.OnPreferenceChangeListener listChangeListener = new Preference.OnPreferenceChangeListener() {
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			// We have put the index numbers as values:
			int val = Integer.valueOf((String)newValue);
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

        Preference ringBell = getPreferenceScreen().findPreference(getText(R.string.keyTry));
        ringBell.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				ringBell();
				return false;
			}
        });
    }

    
    @Override
    public void onPause() {
    	super.onPause();
    	Intent intent = new Intent(this, MindBellSwitch.class);
    	PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    	try {
    		sender.send();
        } catch (PendingIntent.CanceledException e) {
        	Log.e(LOGTAG, "Could not send: "+e.getMessage());
        }
    }
    	
    	
    	
    
	private void setupListPreference(int keyID, String[] valueStrings) {
		ListPreference lp = (ListPreference) getPreferenceScreen().findPreference(getText(keyID));
        int val = Integer.valueOf((String)lp.getValue());
        lp.setSummary(valueStrings[val]);
        listPrefStrings.put(lp, valueStrings);
        lp.setOnPreferenceChangeListener(listChangeListener);
	}


	private void ringBell() {
    	Intent ringBell = new Intent(this, MindBell.class);
    	startActivity(ringBell);
    }

}