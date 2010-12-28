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
     */
    public static void ringBell(Context context, final Runnable runWhenDone) {
        ContextAccessor ca = new AndroidContextAccessor(context);
    }

    /**
     * Trigger the bell's sound. This is the preferred way to play the sound.
     * 
     * @param context
     *            the context in which to play the sound.
     * @param runWhenDone
     *            an optional Runnable to call on completion of the sound, or
     *            null.
     */
    public static void ringBell(ContextAccessor ca, final Runnable runWhenDone) {
        logDebug("Ring bell request received");

        synchronized (lock) {
            // Stop any running bell sounds
            if (ca.haveMediaPlayer()) {
                ca.destroyMediaPlayer();
            } else {
                // no current sound: remember volume
                originalVolume = ca.getOriginalVolume();
                logDebug("Remembering original music volume: " + originalVolume);
            }
        }

        if (ca.isMuteRequested()) {
            if (runWhenDone != null) {
                runWhenDone.run();
            }
            return;
        }

        ca.kickoffMediaPlayer(runWhenDone, originalVolume);
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