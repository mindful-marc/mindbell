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
package com.googlecode.mindbell.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.googlecode.mindbell.MindBell;
import com.googlecode.mindbell.R;

/**
 * @author marc
 * 
 */
public class AndroidContextAccessor extends ContextAccessor {
    public static final int KEYMUTEOFFHOOK   = R.string.keyMuteOffHook;
    public static final int KEYMUTEWITHPHONE = R.string.keyMuteWithPhone;

    private final Context   context;

    private MediaPlayer     mediaPlayer      = null;

    private int             originalVolume   = -1;

    public AndroidContextAccessor(Context context) {
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
    public int getBellVolume() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String bellVolumeString = settings.getString(context.getString(R.string.keyVolume), "10");
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
    public int getMusicVolume() {
        AudioManager audioMan = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioMan.getStreamVolume(AudioManager.STREAM_MUSIC);

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
            setMusicVolume(originalVolume);
        }
    }

    @Override
    public void setMusicVolume(int volume) {
        AudioManager audioMan = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioMan.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

    }

    @Override
    public void showMessage(String message) {
        MindBell.logDebug(message);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startBellSound(final Runnable runWhenDone) {
        originalVolume = getMusicVolume();
        MindBell.logDebug("Remembering original music volume: " + originalVolume);

        mediaPlayer = MediaPlayer.create(context, R.raw.bell10s);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                MindBell.logDebug("Upon completion, originalVolume is " + originalVolume);
                finishBellSound();
                restoreOriginalVolume();
                if (runWhenDone != null) {
                    runWhenDone.run();
                }
            }
        });
        int bellVolume = getBellVolume();
        setMusicVolume(bellVolume);
        MindBell.logDebug("Ringing bell with volume " + bellVolume);
        mediaPlayer.start();

    }

}
