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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.googlecode.mindbell.accessors.AndroidContextAccessor;
import com.googlecode.mindbell.accessors.AndroidPrefsAccessor;
import com.googlecode.mindbell.accessors.PrefsAccessor;
import com.googlecode.mindbell.logic.SchedulerLogic;
import com.googlecode.mindbell.util.KeepAlive;
import com.googlecode.mindbell.util.TimeOfDay;

/**
 * Ring the bell and reschedule.
 * 
 * @author marc
 * 
 */
public class Scheduler extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(MindBellPreferences.LOGTAG, "random scheduler reached");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        PrefsAccessor prefs = new AndroidPrefsAccessor(settings, context);

        if (!prefs.isBellActive()) {
            Log.d(MindBellPreferences.LOGTAG, "bell is not active -- not ringing, not rescheduling.");
            return;
        }

        // reschedule
        Intent nextIntent = new Intent(context, Scheduler.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long nowMillis = Calendar.getInstance().getTimeInMillis();
        long nextBellTimeMillis = SchedulerLogic.getNextTargetTimeMillis(nowMillis, prefs);
        alarmManager.set(AlarmManager.RTC_WAKEUP, nextBellTimeMillis, sender);
        TimeOfDay nextBellTime = new TimeOfDay(nextBellTimeMillis);
        Log.d(MindBellPreferences.LOGTAG,
                "scheduled next bell alarm for " + nextBellTime.hour + ":" + String.format("%02d", nextBellTime.minute));

        // ring if daytime
        if (!prefs.isDaytime()) {
            Log.d(MindBellPreferences.LOGTAG, "not ringing, it is night time");
            return;
        }

        if (prefs.doShowBell()) {
            Log.d(MindBellPreferences.LOGTAG, "audio-visual ring");

            Intent ringBell = new Intent(context, MindBell.class);
            PendingIntent bellIntent = PendingIntent.getActivity(context, -1, ringBell, PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                bellIntent.send();
            } catch (CanceledException e) {
                Log.d(MindBellPreferences.LOGTAG, "cannot ring audio-visual bell: " + e.getMessage());
            }

        } else { // ring audio-only immediately:
            Log.d(MindBellPreferences.LOGTAG, "audio-only ring");
            new KeepAlive(AndroidContextAccessor.get(context), 15000).ringBell();
        }

    }

}
