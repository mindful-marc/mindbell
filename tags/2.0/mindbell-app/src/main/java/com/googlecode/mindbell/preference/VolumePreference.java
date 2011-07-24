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

import android.app.Dialog;
import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings.System;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.googlecode.mindbell.R;
import com.googlecode.mindbell.util.Utils;

/**
 * @hide
 */
public class VolumePreference extends SeekBarPreference implements
/* PreferenceManager.OnActivityStopListener, */View.OnKeyListener {

    private static class SavedState extends BaseSavedState {
        VolumeStore mVolumeStore = new VolumeStore();

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        public SavedState(Parcel source) {
            super(source);
            mVolumeStore.volume = source.readInt();
            mVolumeStore.originalVolume = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        VolumeStore getVolumeStore() {
            return mVolumeStore;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mVolumeStore.volume);
            dest.writeInt(mVolumeStore.originalVolume);
        }
    }

    /**
     * Turns a {@link SeekBar} into a volume control.
     */
    public class SeekBarVolumizer implements OnSeekBarChangeListener, Runnable {

        private final Context mContext;
        private final Handler mHandler = new Handler();

        private final AudioManager mAudioManager;
        private final int mStreamType;
        private int mOriginalStreamVolume;
        private Ringtone mRingtone;

        private int mLastProgress = -1;
        private final SeekBar mSeekBar;

        private final ContentObserver mVolumeObserver = new ContentObserver(mHandler) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                if (mSeekBar != null) {
                    int volume = System.getInt(mContext.getContentResolver(), System.VOLUME_SETTINGS[mStreamType], -1);
                    // Works around an atomicity problem with volume updates
                    // TODO: Fix the actual issue, probably in AudioService
                    if (volume >= 0) {
                        mSeekBar.setProgress(volume);
                    }
                }
            }
        };

        public SeekBarVolumizer(Context context, SeekBar seekBar, int streamType) {
            mContext = context;
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            mStreamType = streamType;
            mSeekBar = seekBar;

            initSeekBar(seekBar);
        }

        public void changeVolumeBy(int amount) {
            mSeekBar.incrementProgressBy(amount);
            // if (mRingtone != null && !mRingtone.isPlaying()) {
            // sample();
            // }
            if (mRingtone != null && mRingtone.isPlaying()) {
                stopSample();
            }
            sample();
            postSetVolume(mSeekBar.getProgress());
        }

        public SeekBar getSeekBar() {
            return mSeekBar;
        }

        private void initSeekBar(SeekBar seekBar) {
            seekBar.setMax(mAudioManager.getStreamMaxVolume(mStreamType));
            mOriginalStreamVolume = mAudioManager.getStreamVolume(mStreamType);
            seekBar.setProgress(mOriginalStreamVolume);
            seekBar.setOnSeekBarChangeListener(this);

            mContext.getContentResolver().registerContentObserver(System.getUriFor(System.VOLUME_SETTINGS[mStreamType]), false,
                    mVolumeObserver);

            if (mRingtoneResId != -1) {
                Uri defaultUri = null;
                defaultUri = Utils.getResourceUri(mContext, mRingtoneResId);
                mRingtone = RingtoneManager.getRingtone(mContext, defaultUri);
            }
            if (mRingtone != null) {
                mRingtone.setStreamType(mStreamType);
                sample();
            }
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
            if (!fromTouch) {
                return;
            }

            postSetVolume(progress);

            stopSample();
            sample();
        }

        public void onRestoreInstanceState(VolumeStore volumeStore) {
            if (volumeStore.volume != -1) {
                mOriginalStreamVolume = volumeStore.originalVolume;
                mLastProgress = volumeStore.volume;
                postSetVolume(mLastProgress);
            }
        }

        public void onSaveInstanceState(VolumeStore volumeStore) {
            if (mLastProgress >= 0) {
                volumeStore.volume = mLastProgress;
                volumeStore.originalVolume = mOriginalStreamVolume;
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            if (mRingtone != null && !mRingtone.isPlaying()) {
                sample();
            }
        }

        void postSetVolume(int progress) {
            // Do the volume changing separately to give responsive UI
            mLastProgress = progress;
            mHandler.removeCallbacks(this);
            mHandler.post(this);
        }

        public void revertVolume() {
            mAudioManager.setStreamVolume(mStreamType, mOriginalStreamVolume, 0);
        }

        public void run() {
            mAudioManager.setStreamVolume(mStreamType, mLastProgress, 0);
        }

        private void sample() {
            onSampleStarting(this);
            if (mRingtone != null) {
                mRingtone.play();
            }
        }

        public void stop() {
            stopSample();
            mContext.getContentResolver().unregisterContentObserver(mVolumeObserver);
            mSeekBar.setOnSeekBarChangeListener(null);
        }

        public void stopSample() {
            if (mRingtone != null) {
                mRingtone.stop();
            }
        }
    }

    public static class VolumeStore {
        public int volume = -1;
        public int originalVolume = -1;
    }

    private static final String TAG = "VolumePreference";
    private static final String mindfulns = "http://mindful-apps.com/ns";

    private int mStreamType;
    private int mRingtoneResId;

    /** May be null if the dialog isn't visible. */
    private SeekBarVolumizer mSeekBarVolumizer;

    public VolumePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "Attributes: " + attrs.getAttributeCount());
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            Log.d(TAG, "Attr " + i + ": " + attrs.getAttributeName(i) + "=" + attrs.getAttributeValue(i));
        }
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
            persistInt(mSeekBarVolumizer.mLastProgress);
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

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        if (mSeekBarVolumizer != null) {
            mSeekBarVolumizer.onRestoreInstanceState(myState.getVolumeStore());
        }
    }

    protected void onSampleStarting(SeekBarVolumizer volumizer) {
        if (mSeekBarVolumizer != null && volumizer != mSeekBarVolumizer) {
            mSeekBarVolumizer.stopSample();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        if (mSeekBarVolumizer != null) {
            mSeekBarVolumizer.onSaveInstanceState(myState.getVolumeStore());
        }
        return myState;
    }

    public void setRingtoneResId(int resid) {
        mRingtoneResId = resid;
    }

    public void setStreamType(int streamType) {
        mStreamType = streamType;
    }
}