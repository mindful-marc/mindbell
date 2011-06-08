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

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.mindbell.MindBell;
import com.googlecode.mindbell.MindBellPreferences;
import com.googlecode.mindbell.R;
import com.googlecode.mindbell.util.Utils;

/**
 * @author marc
 * 
 */
public class AndroidContextAccessor extends ContextAccessor {
    public static final int KEYMUTEOFFHOOK = R.string.keyMuteOffHook;
    public static final int KEYMUTEWITHPHONE = R.string.keyMuteWithPhone;

    public static AndroidContextAccessor get(Context context) {
        return new AndroidContextAccessor(context);
    }

    private final Context context;

    private MediaPlayer mediaPlayer = null;

    private int originalVolume = -1;

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
        restoreOriginalVolume();
    }

    @Override
    public int getAlarmMaxVolume() {
        AudioManager audioMan = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioMan.getStreamMaxVolume(AudioManager.STREAM_ALARM);
    }

    @Override
    public int getAlarmVolume() {
        AudioManager audioMan = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioMan.getStreamVolume(AudioManager.STREAM_ALARM);
    }

    @Override
    public int getBellVolume() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String bellVolumeString = settings.getString(context.getString(R.string.keyVolume),
                String.valueOf(getBellDefaultVolume()));
        assert bellVolumeString != null;
        try {
            int bellVolume = Integer.valueOf(bellVolumeString);
            return bellVolume;
        } catch (NumberFormatException nfe) {
            return 10;
        }
    }

    private boolean getBooleanSetting(int keyID) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(keyID);
        return settings.getBoolean(key, false);
    }

    @Override
    public boolean isBellSoundPlaying() {
        return mediaPlayer != null;
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
    public boolean isSettingMuteOffHook() {
        return getBooleanSetting(KEYMUTEOFFHOOK);
    }

    @Override
    public boolean isSettingMuteWithPhone() {
        return getBooleanSetting(KEYMUTEWITHPHONE);
    }

    private void restoreOriginalVolume() {
        if (originalVolume != -1) {
            setAlarmVolume(originalVolume);
        }
    }

    @Override
    public void setAlarmVolume(int volume) {
        AudioManager audioMan = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioMan.setStreamVolume(AudioManager.STREAM_ALARM, volume, 0);

    }

    @Override
    public void showMessage(String message) {
        MindBell.logDebug(message);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startBellSound(final Runnable runWhenDone) {
        MindBell.logDebug("Starting bell sound");
        originalVolume = getAlarmVolume();
        MindBell.logDebug("Remembering original music volume: " + originalVolume);

        int bellVolume = getBellVolume();
        setAlarmVolume(bellVolume);
        MindBell.logDebug("Ringing bell with volume " + bellVolume);
        Uri bellUri = Utils.getResourceUri(context, R.raw.bell10s);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        try {
            mediaPlayer.setDataSource(context, bellUri);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    MindBell.logDebug("Upon completion, originalVolume is " + originalVolume);
                    finishBellSound();
                    if (runWhenDone != null) {
                        runWhenDone.run();
                    }
                }
            });
            mediaPlayer.start();

        } catch (IOException ioe) {
            Log.e(MindBellPreferences.LOGTAG, "Cannot set up bell sound", ioe);
            if (runWhenDone != null) {
                runWhenDone.run();
            }
        }

    }

}
