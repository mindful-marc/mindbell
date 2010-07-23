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

import com.googlecode.mindbell.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class MindBell extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bell);
    }
    
    protected void onStart() {
    	super.onStart();
    	
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
    	boolean play = true;
    	// If settings ask us to mute if the phone is active in a call, and that is the case, do not play.
    	boolean muteOffHook = settings.getBoolean(getString(R.string.keyMuteOffHook), false);
    	if (muteOffHook) {
    		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    		if (telephonyManager.getCallState() != TelephonyManager.CALL_STATE_IDLE) {
    			play = false;
        		Log.d(MindBellPreferences.LOGTAG, "muting because the phone is off hook");
        		Toast.makeText(this, "muting because the phone is off hook", Toast.LENGTH_SHORT).show();
    		}
    	}
    	if (play) {
    		ringBell(this, new MediaPlayer.OnCompletionListener() {
    			public void onCompletion(MediaPlayer mp) {
    				mp.release();
    				MindBell.this.finish();
    			}
            });
    	} else {
    		finish();
    	}
    }
    
    /**
     * Trigger the bell's sound. This is the preferred way to play the sound.
     * @param context the context in which to play the sound.
     * @param listener an optional listener to call on completion of the sound, or null.
     */
    public static void ringBell(Context context, MediaPlayer.OnCompletionListener listener) {
    	MediaPlayer mp = MediaPlayer.create(context, R.raw.bell10s);
    	if (listener != null) {
    		mp.setOnCompletionListener(listener);
    	}
    	mp.start();
    }
}