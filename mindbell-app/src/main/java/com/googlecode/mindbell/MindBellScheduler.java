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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.googlecode.mindbell.util.AndroidPrefsAccessor;
import com.googlecode.mindbell.util.PrefsAccessor;

/**
 * Turn the mindbell automatically on and off depending on the time of day. This is called from alarm controller every morning (to
 * turn the mind bell on) and every evening (to turn the mind bell off). Also it is called from MindBellPreferences when the user
 * turns on or off the mind bell altogether: When the mind bell is activated, and it is daytime, this is called as if it was
 * morning; if the mind bell is deactivated, and it is daytime, this is called as if it was evening.
 * 
 * @author marc
 * 
 */
public class MindBellScheduler extends BroadcastReceiver {
    private AlarmManager theAlarmManager;
    private PrefsAccessor prefs;
    private Context context;

    private void activateBell(Context context) {
        PendingIntent sender = null;
        if (prefs.doShowBell()) {
            Intent ringBell = new Intent(context, MindBell.class);
            sender = PendingIntent.getActivity(context, -1, ringBell, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            Intent ringBellAudioOnly = new Intent(context, MindBellAudioOnly.class);
            sender = PendingIntent.getBroadcast(context, -1, ringBellAudioOnly, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        // Schedule for running every X minutes:
        // the "inexact" scheduler ends up scheduling at regular intervals in
        // the end:
        long firstTime = SystemClock.elapsedRealtime();
        long interval = prefs.getInterval(); // in milliseconds
        theAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, interval, sender);

        // The following doesn't work because alarmManager.set() overwrites
        // previous entries with same time.
        /*
         * // Instead, schedule our own quasi-random events from now until eveningTime: long firstTime =
         * Calendar.getInstance().getTimeInMillis(); long interval = getInterval(); // in milliseconds long currentTime =
         * firstTime; long eveningTime = getNextDaytimeEndInMillis(); Random random = new Random(); //
         * Log.i(MindBellPreferences.LOGTAG, "firstTime="+firstTime); // Log.i(MindBellPreferences.LOGTAG,
         * "eveningTime="+eveningTime); // Log.i(MindBellPreferences.LOGTAG, "interval="+interval); //
         * Log.i(MindBellPreferences.LOGTAG, "That means into the time from first to evening, we can fit interval "
         * +((eveningTime-firstTime)/interval)+" times"); while (currentTime < eveningTime) {
         * theAlarmManager.set(AlarmManager.RTC_WAKEUP, currentTime, sender); //Log.d(MindBellPreferences.LOGTAG,
         * "Scheduling bell for "+currentTime); currentTime += interval * (1.0 + 0.2 * random.nextGaussian()); }
         */
    }

    private void deactivateBell(Context context) {
        // Make sure to deactivate any pending audio-visual and audio-only
        // bells:
        Intent ringBell = new Intent(context, MindBell.class);
        PendingIntent sender = PendingIntent.getActivity(context, -1, ringBell, PendingIntent.FLAG_UPDATE_CURRENT);
        // And cancel the alarm.
        theAlarmManager.cancel(sender);
        Intent ringBellAudioOnly = new Intent(context, MindBellAudioOnly.class);
        PendingIntent sender2 = PendingIntent.getBroadcast(context, -1, ringBellAudioOnly, PendingIntent.FLAG_UPDATE_CURRENT);
        theAlarmManager.cancel(sender2);
    }

    @Override
    public void onReceive(Context aContext, Intent intent) {

        Log.d(MindBellPreferences.LOGTAG, "scheduler reached");
        this.context = aContext;

        theAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        prefs = new AndroidPrefsAccessor(settings, aContext);

        // Are we requested to turn on or off the mind bell?
        boolean activate = intent.getBooleanExtra(MindBellPreferences.ACTIVATEBELL, false);
        boolean reschedule = intent.getBooleanExtra(MindBellPreferences.RESCHEDULEBELL, false);
        Log.d(MindBellPreferences.LOGTAG, "activate: " + activate + ", reschedule: " + reschedule);
        if (activate) { // turned on
            activateBell(context);
            Log.i(MindBellPreferences.LOGTAG, "activated bell for daytime use");
            if (reschedule) {
                // Schedule our own "off" call at the end of daytime:
                Intent nextIntent = new Intent(context, MindBellScheduler.class);
                nextIntent.putExtra(MindBellPreferences.ACTIVATEBELL, false);
                nextIntent.putExtra(MindBellPreferences.RESCHEDULEBELL, true);
                PendingIntent sender = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                int tEnd = prefs.getDaytimeEnd();
                long eveningTimeInMillis = prefs.getNextDaytimeEndInMillis();

                theAlarmManager.set(AlarmManager.RTC_WAKEUP, eveningTimeInMillis, sender);
                Log.d(MindBellPreferences.LOGTAG, "scheduled 'off' alarm for " + (tEnd / 100) + ":"
                        + String.format("%02d", tEnd % 100));
            }
        } else {
            deactivateBell(context);
            Log.i(MindBellPreferences.LOGTAG, "scheduler deactivated bell");
            if (reschedule) {
                // Schedule our own "on" call at tomorrow's start of daytime:
                Intent nextIntent = new Intent(context, MindBellScheduler.class);
                nextIntent.putExtra(MindBellPreferences.ACTIVATEBELL, true);
                nextIntent.putExtra(MindBellPreferences.RESCHEDULEBELL, true);
                PendingIntent sender = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                int tStart = prefs.getDaytimeStart();
                long morningTimeInMillis = prefs.getNextDaytimeStartInMillis();
                theAlarmManager.set(AlarmManager.RTC_WAKEUP, morningTimeInMillis, sender);
                Log.d(MindBellPreferences.LOGTAG, "scheduled 'on' alarm for " + (tStart / 100) + ":"
                        + String.format("%02d", tStart % 100));
            }
        }
    }

}
