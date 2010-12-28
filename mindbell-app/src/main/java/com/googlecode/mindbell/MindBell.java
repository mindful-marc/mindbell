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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.googlecode.mindbell.util.AndroidContextAccessor;
import com.googlecode.mindbell.util.ContextAccessor;

public class MindBell extends Activity {

    private static int          originalVolume = -1;
    private static final Object lock           = new Object();

    public static void logDebug(String message) {
        Log.d(MindBellPreferences.LOGTAG, message);
    }

    /**
     * Trigger the bell's sound. This is the preferred way to play the sound.
     * 
     * @param context
     *            the context in which to play the sound.
     * @param runWhenDone
     *            an optional Runnable to call on completion of the sound, or
     *            null.
     * @return true if bell started ringing, false otherwise
     */
    public static boolean ringBell(Context context, final Runnable runWhenDone) {
        ContextAccessor ca = new AndroidContextAccessor(context);
        return ringBell(ca, runWhenDone);
    }

    /**
     * Trigger the bell's sound. This is the preferred way to play the sound.
     * 
     * @param context
     *            the context in which to play the sound.
     * @param runWhenDone
     *            an optional Runnable to call on completion of the sound, or
     *            null.
     * @return true if bell started ringing, false otherwise
     */
    public static boolean ringBell(ContextAccessor ca, final Runnable runWhenDone) {
        logDebug("Ring bell request received");

        // 1. Verify if we should be muted
        if (ca.isMuteRequested()) {
            if (runWhenDone != null) {
                runWhenDone.run();
            }
            return false;
        }
        // 2. Stop any ongoing ring, and manually reset volume to original.
        if (ca.isBellSoundPlaying()) {
            ca.finishBellSound();
        }

        // 3. Kick off the playback of the bell sound, with an automatic volume
        // reset built-in if not stopped.
        ca.startBellSound(runWhenDone);
        return true;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bell);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ringBell(this, new Runnable() {
            public void run() {
                MindBell.this.moveTaskToBack(true);
                MindBell.this.finish();
            }
        });
    }

}