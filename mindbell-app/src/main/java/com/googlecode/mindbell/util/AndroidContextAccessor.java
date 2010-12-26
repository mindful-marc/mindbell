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

    public AndroidContextAccessor(Context context) {
        this.context = context;
    }

    private boolean getBooleanSetting(int keyID) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(keyID);
        return settings.getBoolean(key, false);
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

    @Override
    public void showMessage(String message) {
        MindBell.logDebug(message);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
