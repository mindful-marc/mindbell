/*
 * Copyright (C) 2007 The Android Open Source Project
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

package com.googlecode.mindbell.preference;

import java.io.IOException;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.googlecode.mindbell.R;
import com.googlecode.mindbell.util.Utils;
import com.googlecode.mindbell.util.VolumeConverter;

/**
 * @hide
 */
public class MediaVolumePreference extends SeekBarPreference implements View.OnKeyListener {

    /**
     * Turns a {@link SeekBar} into a volume control.
     */
    public class SeekBarVolumizer implements OnSeekBarChangeListener {

        private final Context mContext;

        private final AudioManager mAudioManager;
        private final int mStreamType;
        private int mOriginalStreamVolume;
        private MediaPlayer mPlayer;
        private float volume;
        private final VolumeConverter converter;

        private final SeekBar mSeekBar;

        public SeekBarVolumizer(Context context, SeekBar seekBar, int streamType) {
            mContext = context;
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            mStreamType = streamType;
            mSeekBar = seekBar;
            converter = new VolumeConverter(DYNAMIC_RANGE_DB, MAX_PROGRESS);
            initSeekBar(seekBar);
        }

        public void changeVolumeBy(int amount) {
            mSeekBar.incrementProgressBy(amount);
            stopSample();
            sample();
            // if (mRingtone != null && !mRingtone.isPlaying()) {
            // sample();
            // }
            // if (mRingtone != null && mRingtone.isPlaying()) {
            // stopSample();
            // }
            // sample();
        }

        public SeekBar getSeekBar() {
            return mSeekBar;
        }

        private void initSeekBar(SeekBar seekBar) {
            mOriginalStreamVolume = mAudioManager.getStreamVolume(mStreamType);
            mAudioManager.setStreamVolume(mStreamType, mAudioManager.getStreamMaxVolume(mStreamType), 0);
            seekBar.setMax(MAX_PROGRESS);
            volume = getPersistedFloat(0.5f);
            seekBar.setProgress(converter.volume2progress(volume));
            seekBar.setOnSeekBarChangeListener(this);

            if (mRingtoneResId != -1) {
                Uri defaultUri = null;
                defaultUri = Utils.getResourceUri(mContext, mRingtoneResId);
                mPlayer = new MediaPlayer();
                mPlayer.setAudioStreamType(mStreamType);
                try {
                    mPlayer.setDataSource(mContext, defaultUri);
                    mPlayer.prepare();
                } catch (IOException e) {
                    Log.e(TAG, "Cannot load ringtone", e);
                    mPlayer = null;
                }
            }
            if (mPlayer != null) {
                sample();
            }
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
            volume = converter.progress2volume(progress);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            stopSample();
            sample();
        }

        public void revertVolume() {
            mAudioManager.setStreamVolume(mStreamType, mOriginalStreamVolume, 0);
        }

        private void sample() {
            onSampleStarting(this);
            if (mPlayer != null) {
                mPlayer.setVolume(volume, volume);
                mPlayer.seekTo(0);
                mPlayer.start();
            }
        }

        public void stop() {
            stopSample();
            if (mPlayer != null) {
                mPlayer.release();
                mPlayer = null;
            }
            mSeekBar.setOnSeekBarChangeListener(null);
        }

        public void stopSample() {
            if (mPlayer != null) {
                mPlayer.pause();
            }
        }

    }

    public static final int DYNAMIC_RANGE_DB = 50;
    public static final int MAX_PROGRESS = 50;

    private static final String TAG = "VolumePreference";
    private static final String mindfulns = "http://mindful-apps.com/ns";

    private int mStreamType;
    private int mRingtoneResId;

    /** May be null if the dialog isn't visible. */
    private SeekBarVolumizer mSeekBarVolumizer;

    public MediaVolumePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Log.d(TAG, "Attributes: " + attrs.getAttributeCount());
        // for (int i = 0; i < attrs.getAttributeCount(); i++) {
        // Log.d(TAG, "Attr " + i + ": " + attrs.getAttributeName(i) + "=" + attrs.getAttributeValue(i));
        // }
        mStreamType = attrs.getAttributeIntValue(mindfulns, "streamType", AudioManager.STREAM_NOTIFICATION);
        mRingtoneResId = attrs.getAttributeResourceValue(mindfulns, "ringtone", -1);
    }

    // public void onActivityStop() {
    // cleanup();
    // }

    /**
     * Do clean up. This can be called multiple times!
     */
    private void cleanup() {
        // getPreferenceManager().unregisterOnActivityStopListener(this);

        if (mSeekBarVolumizer != null) {
            Dialog dialog = getDialog();
            if (dialog != null && dialog.isShowing()) {
                View view = dialog.getWindow().getDecorView().findViewById(R.id.seekbar);
                if (view != null) {
                    view.setOnKeyListener(null);
                }
                // Stopped while dialog was showing, revert changes
                mSeekBarVolumizer.revertVolume();
            }
            mSeekBarVolumizer.stop();
            mSeekBarVolumizer = null;
        }

    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        final SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekbar);
        mSeekBarVolumizer = new SeekBarVolumizer(getContext(), seekBar, mStreamType);

        // getPreferenceManager().registerOnActivityStopListener(this);

        // grab focus and key events so that pressing the volume buttons in the
        // dialog doesn't also show the normal volume adjust toast.
        view.setOnKeyListener(this);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (!positiveResult && mSeekBarVolumizer != null) {
            mSeekBarVolumizer.revertVolume();
        }

        if (positiveResult && mSeekBarVolumizer != null) {
            Log.d(TAG, "Persisting volume as " + mSeekBarVolumizer.volume);
            persistFloat(mSeekBarVolumizer.volume);
            Log.d(TAG, "And reverting volume to " + mSeekBarVolumizer.mOriginalStreamVolume);
            mSeekBarVolumizer.revertVolume();

        }

        cleanup();
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        // If key arrives immediately after the activity has been cleaned up.
        if (mSeekBarVolumizer == null) {
            return true;
        }
        boolean isdown = (event.getAction() == KeyEvent.ACTION_DOWN);
        switch (keyCode) {
        case KeyEvent.KEYCODE_VOLUME_DOWN:
            if (isdown) {
                mSeekBarVolumizer.changeVolumeBy(-1);
            }
            return true;
        case KeyEvent.KEYCODE_VOLUME_UP:
            if (isdown) {
                mSeekBarVolumizer.changeVolumeBy(1);
            }
            return true;
        default:
            return false;
        }
    }

    protected void onSampleStarting(SeekBarVolumizer volumizer) {
        if (mSeekBarVolumizer != null && volumizer != mSeekBarVolumizer) {
            mSeekBarVolumizer.stopSample();
        }
    }

    public void setRingtoneResId(int resid) {
        mRingtoneResId = resid;
    }

    public void setStreamType(int streamType) {
        mStreamType = streamType;
    }
}