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


import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class MindBellPreferences extends PreferenceActivity {
	public static final String LOGTAG = "MindBell";
	public static final String ACTIVATEBELL = "mindBellActive";
	public static final String RESCHEDULEBELL = "mindBellReschedule";

	private String[] frequencies;
	private String[] hours;
	private Map<Preference, String[]> listPrefStrings = new HashMap<Preference, String[]>();
	private NotificationManager theNotificationManager;
	private AlarmManager theAlarmManager;
	private SharedPreferences settings; 


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

        theAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        theNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        settings = PreferenceManager.getDefaultSharedPreferences(this);

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
    	deactivateBell(); // always cancel anything using any previous settings first
    	boolean isChecked = settings.getBoolean(getString(R.string.keyActive), false);
    	if (isChecked) {
    		activateBell();
    	}
    }
    
	private void setupListPreference(int keyID, String[] valueStrings) {
		ListPreference lp = (ListPreference) getPreferenceScreen().findPreference(getText(keyID));
        int val = Integer.valueOf((String)lp.getValue());
        lp.setSummary(valueStrings[val]);
        listPrefStrings.put(lp, valueStrings);
        lp.setOnPreferenceChangeListener(listChangeListener);
	}

	
    private void activateBell() {
    	Intent intent = new Intent(this, MindBellScheduler.class);
    	intent.putExtra(ACTIVATEBELL, true);
    	intent.putExtra(RESCHEDULEBELL, true);
    	PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    	// Now determine if we are in "daytime"
		int tStart = getDaytimeStart();
    	if (isDaytime()) {
    		// start scheduler now:
        	try {
        		sender.send();
        	} catch (PendingIntent.CanceledException e) {
        		Log.e(LOGTAG, "Could not send: "+e.getMessage());
        	}
    	} else {
    		String startTime = getDaytimeStartString();
    		Toast.makeText(this, "it is nighttime, scheduling start for "+startTime, Toast.LENGTH_SHORT).show();
    		Log.d(LOGTAG, "it is nighttime, scheduling start for "+startTime);
    		// program start of scheduler for next morning
    		Calendar morning = Calendar.getInstance();
    		int currentHour = morning.get(Calendar.HOUR_OF_DAY);
    		int currentMinute = morning.get(Calendar.MINUTE);
    		int currentTime = 100*currentHour + currentMinute;
    		if (currentTime < tStart) {
    			// already right day
    		} else {
    			// need to add one day
    			morning.add(Calendar.DATE, 1);
    		}
    		morning.set(Calendar.HOUR_OF_DAY, tStart/100);
    		morning.set(Calendar.MINUTE, tStart%100);
    		morning.set(Calendar.SECOND, 0);
    		theAlarmManager.set(AlarmManager.RTC_WAKEUP, morning.getTimeInMillis(), sender);
    	}
        Notification notif = new Notification(R.drawable.bell_status_active, "", System.currentTimeMillis());
        CharSequence contentTitle = getText(R.string.statusTitleBellActive);
        String contentText = getText(R.string.statusTextBellActive).toString();
        contentText = contentText.replace("_STARTTIME_", getDaytimeStartString())
        	.replace("_ENDTIME_", getDaytimeEndString());
        Intent notificationIntent = new Intent(this, MindBellPreferences.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notif.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, contentIntent);
        notif.flags |= Notification.FLAG_ONGOING_EVENT;
        int id = R.layout.bell; // unique ID
        theNotificationManager.notify(id, notif);
        
    }
    
    private void deactivateBell() {
    	// Day or night, we cancel the next message in stock for the scheduler.
    	Intent intent = new Intent(this, MindBellScheduler.class);
    	PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		theAlarmManager.cancel(sender); // filterEquals() matches irrespective of extras
		// Now we send a "deactivate" message to the scheduler irrespective of day- or nighttime
		// because the boundaries of day and night may have been changed, and even if we think it is night,
		// the scheduler may still be active.
		intent.putExtra(ACTIVATEBELL, false);
    	intent.putExtra(RESCHEDULEBELL, false);
    	sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    	try {
    		sender.send();
    	} catch (PendingIntent.CanceledException e) {
    		Log.e(LOGTAG, "Could not send: "+e.getMessage());
    	}
    	// And finally we switch off the notification
    	int id = R.layout.bell; // unique ID
        theNotificationManager.cancel(id);
    }
    
    private boolean isDaytime() {
    	int tStart = getDaytimeStart();
    	int tEnd = getDaytimeEnd();
    	Calendar now = Calendar.getInstance();
    	int currentHour = now.get(Calendar.HOUR_OF_DAY);
    	int currentMinute = now.get(Calendar.MINUTE);
    	int currentTime = 100*currentHour + currentMinute;
    	return (tStart <= currentTime && currentTime < tEnd);
    }

	private void ringBell() {
    	Intent ringBell = new Intent(this, MindBell.class);
    	startActivity(ringBell);
    }

	private int getDaytimeStart() {
		return 100 * Integer.valueOf(settings.getString(getString(R.string.keyStart), "0"));
	}
	
	private String getDaytimeStartString() {
		int startStringIndex = Integer.valueOf(settings.getString(getString(R.string.keyStart), "0"));
		return hours[startStringIndex];
	}
	
	private int getDaytimeEnd() {
		return 100 * Integer.valueOf(settings.getString(getString(R.string.keyEnd), "0"));
	}

	private String getDaytimeEndString() {
		int endStringIndex = Integer.valueOf(settings.getString(getString(R.string.keyEnd), "0"));
		return hours[endStringIndex];
	}

}