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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

/**
 * Turn the mindbell automatically on and off depending on the time of day.
 * This is called from alarm controller every morning (to turn the mind bell on)
 * and every evening (to turn the mind bell off).
 * Also it is called from MindBellSettings when the user turns on or off the mind bell altogether:
 * When the mind bell is activated, and it is daytime, this is called as if it was morning;
 * if the mind bell is deactivated, and it is daytime, this is called as if it was evening.
 * @author marc
 *
 */
public class MindBellScheduler extends BroadcastReceiver {
	private AlarmManager theAlarmManager;
	private SharedPreferences settings;

	@Override
	public void onReceive(Context context, Intent intent) {
		
        Log.d(MindBellSettings.LOGTAG, "scheduler reached");
        theAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        settings = context.getSharedPreferences(MindBellSettings.PREFS_NAME, 0);

        // Are we requested to turn on or off the mind bell?
        boolean activate = intent.getBooleanExtra(MindBellSettings.ACTIVATEBELL, false);
        boolean reschedule = intent.getBooleanExtra(MindBellSettings.RESCHEDULEBELL, false);
        Log.d(MindBellSettings.LOGTAG, "activate: "+activate+", reschedule: "+reschedule);
		if (activate) { // turned on
			activateBell(context);
			Log.i(MindBellSettings.LOGTAG, "activated bell for daytime use");
	        if (reschedule) {
	        	// Schedule our own "off" call at the end of daytime:
	        	Intent nextIntent = new Intent(context, MindBellScheduler.class);
	        	nextIntent.putExtra(MindBellSettings.ACTIVATEBELL, false);
	        	nextIntent.putExtra(MindBellSettings.RESCHEDULEBELL, true);
	        	PendingIntent sender = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

	        	int tEnd = settings.getInt(MindBellSettings.PREFS_END, 0);
	    		Calendar evening = Calendar.getInstance();
	    		evening.set(Calendar.HOUR_OF_DAY, tEnd/100);
	    		evening.set(Calendar.MINUTE, tEnd%100);
	    		evening.set(Calendar.SECOND, 0);
	    		theAlarmManager.set(AlarmManager.RTC_WAKEUP, evening.getTimeInMillis(), sender);
		        Log.d(MindBellSettings.LOGTAG, "scheduled 'off' alarm for "+(tEnd/100)+":"+String.format("%02d", tEnd%100));
	        }
		} else {
			deactivateBell(context);
			Log.i(MindBellSettings.LOGTAG, "scheduler deactivated bell");
	        if (reschedule) {
	        	// Schedule our own "on" call at tomorrow's start of daytime:
	        	Intent nextIntent = new Intent(context, MindBellScheduler.class);
	        	nextIntent.putExtra(MindBellSettings.ACTIVATEBELL, true);
	        	nextIntent.putExtra(MindBellSettings.RESCHEDULEBELL, true);
	        	PendingIntent sender = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

	        	int tStart = settings.getInt(MindBellSettings.PREFS_START, 0);
	    		Calendar morning = Calendar.getInstance();
	    		morning.add(Calendar.DATE, 1);
	    		morning.set(Calendar.HOUR_OF_DAY, tStart/100);
	    		morning.set(Calendar.MINUTE, tStart%100);
	    		morning.set(Calendar.SECOND, 0);
	    		theAlarmManager.set(AlarmManager.RTC_WAKEUP, morning.getTimeInMillis(), sender);
		        Log.d(MindBellSettings.LOGTAG, "scheduled 'on' alarm for "+(tStart/100)+":"+String.format("%02d", tStart%100));
	        }
		}
	}

    private void activateBell(Context context) {
        Intent ringBell = new Intent(context, MindBell.class);
        PendingIntent sender = PendingIntent.getActivity(context, -1, ringBell, PendingIntent.FLAG_UPDATE_CURRENT);
        // Schedule for running every X minutes:
        long firstTime = SystemClock.elapsedRealtime();
        int frequencySetting = settings.getInt(MindBellSettings.PREFS_FREQUENCY, 0);
        long interval;
        switch(frequencySetting) {
        case 1: // half hour
        	interval = AlarmManager.INTERVAL_HALF_HOUR;
        	break;
        case 2: // quarter of an hour
        	interval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        	break;
        default:
        	interval = AlarmManager.INTERVAL_HOUR;
        }
        //long interval = 15*1000; // 15 seconds
        theAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, interval, sender);
    }
    
    private void deactivateBell(Context context) {
        Intent ringBell = new Intent(context, MindBell.class);
        PendingIntent sender = PendingIntent.getActivity(context, -1, ringBell, PendingIntent.FLAG_UPDATE_CURRENT);
        // And cancel the alarm.
        theAlarmManager.cancel(sender);
    }

}
