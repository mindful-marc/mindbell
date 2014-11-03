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
package com.googlecode.mindbell.accessors;

import static com.googlecode.mindbell.MindBellPreferences.TAG;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.mindbell.MindBell;
import com.googlecode.mindbell.MindBellMain;
import com.googlecode.mindbell.R;
import com.googlecode.mindbell.Scheduler;
import com.googlecode.mindbell.util.Utils;

/**
 * @author marc
 * 
 */
public class AndroidContextAccessor extends ContextAccessor {
    public static final int KEYMUTEINFLIGHTMODE = R.string.keyMuteInFlightMode;
    public static final int KEYMUTEOFFHOOK = R.string.keyMuteOffHook;
    public static final int KEYMUTEWITHPHONE = R.string.keyMuteWithPhone;

    private static final int uniqueNotificationID = R.layout.bell;

    public static AndroidContextAccessor get(Context context) {
        return new AndroidContextAccessor(context);
    }

    private final Context context;

    private MediaPlayer mediaPlayer = null;

    private PrefsAccessor prefs = null;

    private AndroidContextAccessor(Context context) {
        this.context = context;
    }

    @Override
    public void finishBellSound() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            MindBell.logDebug("Stopped ongoing player.");
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public int getAlarmMaxVolume() {
        AudioManager audioMan = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioMan.getStreamMaxVolume(AudioManager.STREAM_ALARM);
    }

    @Override
    public int getAlarmVolume() {
        AudioManager audioMan = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int alarmVolume = audioMan.getStreamVolume(AudioManager.STREAM_ALARM);
        Log.d(TAG, "Alarm volume is " + alarmVolume);
        return alarmVolume;
    }

    @Override
    public float getBellVolume() {
        if (prefs == null) {
            prefs = new AndroidPrefsAccessor(context);
        }
        float bellVolume = prefs.getBellVolume(getBellDefaultVolume());
        Log.d(TAG, "Bell volume is " + bellVolume);
        return bellVolume;
    }

    @Override
    protected String getReasonMutedInFlightMode() {
        return context.getText(R.string.reasonMutedInFlightMode).toString();
    }

    @Override
    protected String getReasonMutedOffHook() {
        return context.getText(R.string.reasonMutedOffHook).toString();
    }

    @Override
    protected String getReasonMutedWithPhone() {
        return context.getText(R.string.reasonMutedWithPhone).toString();
    }

    @Override
    public boolean isBellSoundPlaying() {
        return mediaPlayer != null;
    }

    @Override
    public boolean isPhoneInFlightMode() {
        // Using AIRPLANE_MODE_ON requires API-Level 17 (JELLY_BEAN_MR1, 4.2)
        return Settings.System.getInt(context.getContentResolver(), Global.AIRPLANE_MODE_ON, 0) == 1;
    }

    @Override
    public boolean isPhoneMuted() {
        final AudioManager audioMan = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioMan.getStreamVolume(AudioManager.STREAM_RING) == 0;
    }

    @Override
    public boolean isPhoneOffHook() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getCallState() != TelephonyManager.CALL_STATE_IDLE;
    }

    @Override
    public boolean isSettingMuteInFlightMode() {
        if (prefs == null) {
            prefs = new AndroidPrefsAccessor(context);
        }
        return prefs.isSettingMuteInFlightMode();
    }

    @Override
    public boolean isSettingMuteOffHook() {
        if (prefs == null) {
            prefs = new AndroidPrefsAccessor(context);
        }
        return prefs.isSettingMuteOffHook();
    }

    @Override
    public boolean isSettingMuteWithPhone() {
        if (prefs == null) {
            prefs = new AndroidPrefsAccessor(context);
        }
        return prefs.isSettingMuteWithPhone();
    }

    @Override
    public boolean isSettingVibrate() {
        if (prefs == null) {
            prefs = new AndroidPrefsAccessor(context);
        }
        return prefs.isSettingVibrate();
    }

    private void removeStatusNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(uniqueNotificationID);
    }

    @Override
    public void setAlarmVolume(int volume) {
        AudioManager audioMan = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Log.d(TAG, "Setting alarm volume to " + volume);
        audioMan.setStreamVolume(AudioManager.STREAM_ALARM, volume, 0);

    }

    @Override
    public void showMessage(String message) {
        MindBell.logDebug(message);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startBellSound(final Runnable runWhenDone) {
        // MindBell.logDebug("Starting bell sound");

        setAlarmVolume(getAlarmMaxVolume());
        float bellVolume = getBellVolume();
        MindBell.logDebug("Ringing bell with volume " + bellVolume);
        Uri bellUri = Utils.getResourceUri(context, R.raw.bell10s);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        mediaPlayer.setVolume(bellVolume, bellVolume);
        try {
            mediaPlayer.setDataSource(context, bellUri);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    // MindBell.logDebug("Upon completion, originalVolume is " + originalVolume);
                    finishBellSound();
                    if (runWhenDone != null) {
                        runWhenDone.run();
                    }
                }
            });

            mediaPlayer.start();

            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (isSettingVibrate()) {
                long[] pattern = new long[] { 100, 200, 100, 600 };
                vibrator.vibrate(pattern, -1);
            } else {
                vibrator.vibrate(20);
            }

        } catch (IOException ioe) {
            Log.e(TAG, "Cannot set up bell sound", ioe);
            if (runWhenDone != null) {
                runWhenDone.run();
            }
        }

    }

    public void updateBellSchedule() {
        PrefsAccessor prefs = new AndroidPrefsAccessor(context);
        updateStatusNotification(prefs, true);
        if (prefs.isBellActive()) {
            Log.d(TAG, "Update bell schedule for active bell");
            Intent intent = new Intent(context, Scheduler.class);
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                sender.send();
            } catch (PendingIntent.CanceledException e) {
                Log.e(TAG, "Could not send: " + e.getMessage());
            }
        }
    }

    /**
     * This is about updating status notifcation on changes in system settings, removal of notification is done by
     * udpateBellSchedule().
     */
    public void updateStatusNotification() {
        PrefsAccessor prefs = new AndroidPrefsAccessor(context);
        updateStatusNotification(prefs, false);
    }

    private void updateStatusNotification(PrefsAccessor prefs, boolean shouldShowMessage) {
        if (!prefs.isBellActive() || !prefs.doStatusNotification()) { // bell inactive or no notification wanted?
            Log.i(TAG, "remove status notification because of inactive bell or unwanted notification");
            removeStatusNotification();
        } else {
            // Suppose bell is not muted
            int statusDrawable = R.drawable.bell_status_active;
            CharSequence contentTitle = context.getText(R.string.statusTitleBellActive);
            String contentText = context.getText(R.string.statusTextBellActive).toString();
            String muteRequestReason = getMuteRequestReason(shouldShowMessage);
            // Override icon and notification text if bell is muted
            if (muteRequestReason != null) {
                statusDrawable = R.drawable.bell_status_active_but_muted;
                contentText = muteRequestReason;
            }
            contentText = contentText.replace("_STARTTIME_", prefs.getDaytimeStartString()).replace("_ENDTIME_",
                    prefs.getDaytimeEndString());
            // Now do the notification update
            Log.i(TAG, "Update status notification: " + contentText);
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notif = new Notification(statusDrawable, "", System.currentTimeMillis());
            Intent notificationIntent = new Intent(context, MindBellMain.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            notif.setLatestEventInfo(context.getApplicationContext(), contentTitle, contentText, contentIntent);
            notif.flags |= Notification.FLAG_ONGOING_EVENT;
            notificationManager.notify(uniqueNotificationID, notif);
        }
    }

}
