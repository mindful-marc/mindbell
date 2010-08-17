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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class MindBell extends Activity {
	private static MediaPlayer mediaPlayer = null;
	private static int originalVolume = -1;
	private static final Object lock = new Object();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bell);
    }
    
    protected void onStart() {
    	super.onStart();
		ringBell(this, new Runnable() {
			public void run() {
				MindBell.this.moveTaskToBack(true);
				MindBell.this.finish();
			}
		});
    }
    
    /**
     * Trigger the bell's sound. This is the preferred way to play the sound.
     * @param context the context in which to play the sound.
     * @param runWhenDone an optional Runnable to call on completion of the sound, or null.
     */
    public static void ringBell(Context context, final Runnable runWhenDone) {
    	Log.d(MindBellPreferences.LOGTAG, "Ring bell request received");
    	final AudioManager audioMan = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
    	
    	synchronized (lock) {
        	// Stop any running bell sounds
        	if (mediaPlayer != null) {
        		if (mediaPlayer.isPlaying()) {
        			mediaPlayer.stop();
        	    	Log.d(MindBellPreferences.LOGTAG, "Stopped ongoing player.");
        		}
    			mediaPlayer.release();
    			mediaPlayer = null;
    			// but leave originalVolume as it is
        	} else {
        		// no current sound: remember volume
        		originalVolume = audioMan.getStreamVolume(AudioManager.STREAM_MUSIC);
            	Log.d(MindBellPreferences.LOGTAG, "Remembering original music volume: "+originalVolume);
        	}
    	}
    	
    	boolean play = true;
    	// If we are to be muted, don't go any further:
    	boolean muteWithPhone = settings.getBoolean(context.getString(R.string.keyMuteWithPhone), false);
    	if (muteWithPhone && audioMan.getStreamVolume(AudioManager.STREAM_RING) == 0) {
    		play = false;
			String message = "muting bell because the phone is muted";
    		Log.d(MindBellPreferences.LOGTAG, message);
    		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    	}
    	
    	// If settings ask us to mute if the phone is active in a call, and that is the case, do not play.
    	boolean muteOffHook = settings.getBoolean(context.getString(R.string.keyMuteOffHook), false);
    	if (muteOffHook) {
    		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
    		if (telephonyManager.getCallState() != TelephonyManager.CALL_STATE_IDLE) {
    			play = false;
    			String message = "muting bell because the phone is off hook";
        		Log.d(MindBellPreferences.LOGTAG, message);
        		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    		}
    	}
    	if (!play) {
    		if (runWhenDone != null) {
    			runWhenDone.run();
    		}
			return;
    	}

    	// OK, let's play the sound
    	mediaPlayer = MediaPlayer.create(context, R.raw.bell10s);
    	mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				Log.d(MindBellPreferences.LOGTAG, "Upon completion, originalVolume is "+originalVolume);
				synchronized (lock) {
					mediaPlayer.release();
					if (originalVolume != -1) {
						audioMan.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
						originalVolume = -1;
					}
					mediaPlayer = null;
				}
				if (runWhenDone != null) {
					runWhenDone.run();
				}
			}
		});
    	String bellVolumeString = settings.getString(context.getString(R.string.keyVolume), null);
    	if (bellVolumeString != null) {
        	int bellVolume = Integer.valueOf(bellVolumeString);
        	audioMan.setStreamVolume(AudioManager.STREAM_MUSIC, bellVolume, 0);
    	}
    	Log.d(MindBellPreferences.LOGTAG, "Ringing bell with volume "+bellVolumeString);
    	mediaPlayer.start();
    }
}