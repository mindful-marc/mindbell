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
package org.mindbell;



import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class MindBellSettings extends Activity {
	public static final String LOGTAG = "MindBell";
	public static final String ACTIVATEBELL = "mindBellActive";
	public static final String RESCHEDULEBELL = "mindBellReschedule";
	public static final String PREFS_NAME = "MindBellPreferences";
	public static final String PREFS_ACTIVE = "active";
	public static final String PREFS_START = "start";
	public static final String PREFS_END = "end";
	public static final String PREFS_FREQUENCY = "frequency";
	
	private NotificationManager theNotificationManager;
	private AlarmManager theAlarmManager;
	private SharedPreferences settings;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        theAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        theNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Relevant GUI items
        final CheckBox cbBellActive = (CheckBox) findViewById(R.id.cbBellActive);
        final Button bRingBell = (Button) findViewById(R.id.bRingBell);
        final Spinner sFrequency = (Spinner) findViewById(R.id.sFrequency);
        final TimePicker tpStart = (TimePicker) findViewById(R.id.tpStart);
        tpStart.setIs24HourView(true);
        final TimePicker tpEnd = (TimePicker) findViewById(R.id.tpEnd);
        tpEnd.setIs24HourView(true);
        final Button bUpdate = (Button) findViewById(R.id.bUpdate);
        
        // Restore preferences
        settings = getSharedPreferences(PREFS_NAME, 0);
        // Make sure all values are set:
        if (!settings.contains(PREFS_ACTIVE)) {
        	settings.edit().putBoolean(PREFS_ACTIVE, false).commit();
        }
        if (!settings.contains(PREFS_START)) {
        	settings.edit().putInt(PREFS_START, 900).commit(); // 9:00
        }
        if (!settings.contains(PREFS_END)) {
        	settings.edit().putInt(PREFS_END, 2100).commit(); // 21:00
        }
        if (!settings.contains(PREFS_FREQUENCY)) {
        	settings.edit().putInt(PREFS_FREQUENCY, 0).commit(); // every hour
        }
        boolean isActive = settings.getBoolean(PREFS_ACTIVE, false);
        cbBellActive.setChecked(isActive);
        int iStart = settings.getInt(PREFS_START, 0);
        tpStart.setCurrentHour(iStart/100);
        tpStart.setCurrentMinute(iStart%100);
        int iEnd = settings.getInt(PREFS_END, 0);
        tpEnd.setCurrentHour(iEnd/100);
        tpEnd.setCurrentMinute(iEnd%100);
        int iFrequency = settings.getInt(PREFS_FREQUENCY, 0);
        sFrequency.setSelection(iFrequency);

        
        // And only now set up change listeners (any GUI changes while restoring prefs should not trigger actions) 
        cbBellActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					activateBell();
				} else {
					deactivateBell();
				}
				settings.edit().putBoolean(PREFS_ACTIVE, isChecked).commit();
			}
        });
                
        tpStart.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				settings.edit().putInt(PREFS_START, 100*hourOfDay+minute).commit();
				
			}
        });
        
        tpEnd.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				settings.edit().putInt(PREFS_END, 100*hourOfDay+minute).commit();
			}
        });
        
        sFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				settings.edit().putInt(PREFS_FREQUENCY, position).commit();
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// do not change settings
			}
        });

        bUpdate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				deactivateBell();
				activateBell();
			}
        });

        bRingBell.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ringBell();
			}
        });

    }
    
    
    private void ringBell() {
    	Intent ringBell = new Intent(MindBellSettings.this, MindBell.class);
    	startActivity(ringBell);
    }

    private void activateBell() {
/*        Intent ringBell = new Intent(MindBellSettings.this, MindBell.class);
        PendingIntent sender = PendingIntent.getActivity(this, -1, ringBell, PendingIntent.FLAG_UPDATE_CURRENT);
        // Schedule for running every X minutes:
        long firstTime = SystemClock.elapsedRealtime();
        //long interval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        long interval = AlarmManager.INTERVAL_HOUR;
        //long interval = 15*1000; // 15 seconds
        //am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, interval, sender);
        theAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, interval, sender);
*/
    	Intent intent = new Intent(this, MindBellScheduler.class);
    	intent.putExtra(ACTIVATEBELL, true);
    	intent.putExtra(RESCHEDULEBELL, true);
    	PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    	// Now determine if we are in "daytime"
		int tStart = settings.getInt(PREFS_START, 0);
		int tEnd = settings.getInt(PREFS_END, 0);
    	if (isDaytime()) {
    		// start scheduler now:
        	try {
        		sender.send();
        	} catch (PendingIntent.CanceledException e) {
        		Log.e(LOGTAG, "Could not send: "+e.getMessage());
        	}
    	} else {
    		Toast.makeText(this, "it is nighttime, scheduling start for "+(tStart/100)+":"+(tStart%100), Toast.LENGTH_SHORT).show();
    		Log.d(LOGTAG, "it is nighttime, scheduling start for "+(tStart/100)+":"+(tStart%100));
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
        contentText = contentText.replace("_STARTTIME_", (tStart/100)+":"+String.format("%02d", tStart%100))
        	.replace("_ENDTIME_", (tEnd/100)+":"+String.format("%02d", tEnd%100));
        Intent notificationIntent = new Intent(this, MindBellSettings.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notif.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, contentIntent);
        notif.flags |= Notification.FLAG_ONGOING_EVENT;
        int id = R.layout.main; // unique ID
        theNotificationManager.notify(id, notif);
        
    }
    
    private void deactivateBell() {
    	// Day or night, we cancel the next message in stock for the scheduler.
    	// During daytime, we send a deactivate message to the scheduler, with reschedule set to false;

    	Intent intent = new Intent(this, MindBellScheduler.class);
    	PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		theAlarmManager.cancel(sender); // filterEquals() matches irrespective of extras
    	Intent intent2 = new Intent(this, MindBell.class);
    	PendingIntent sender2 = PendingIntent.getBroadcast(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
		theAlarmManager.cancel(sender2); // filterEquals() matches irrespective of extras
    	if (isDaytime()) {
        	intent.putExtra(ACTIVATEBELL, false);
        	intent.putExtra(RESCHEDULEBELL, false);
        	sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        	try {
        		sender.send();
        	} catch (PendingIntent.CanceledException e) {
        		Log.e(LOGTAG, "Could not send: "+e.getMessage());
        	}
    	}
    	int id = R.layout.main; // unique ID
        theNotificationManager.cancel(id);
    }
    
    private boolean isDaytime() {
    	int tStart = settings.getInt(PREFS_START, 0);
    	int tEnd = settings.getInt(PREFS_END, 0);
    	Calendar now = Calendar.getInstance();
    	int currentHour = now.get(Calendar.HOUR_OF_DAY);
    	int currentMinute = now.get(Calendar.MINUTE);
    	int currentTime = 100*currentHour + currentMinute;
    	return (tStart <= currentTime && currentTime < tEnd);
    }
}
