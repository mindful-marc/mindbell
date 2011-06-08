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
package com.googlecode.mindbell.test.accessors;

import java.util.Timer;
import java.util.TimerTask;

import com.googlecode.mindbell.accessors.ContextAccessor;

public class MockContextAccessor extends ContextAccessor {
    private boolean isSettingMuteWithPhone = false;
    private boolean isSettingMuteOffHook = false;
    private boolean isPhoneMuted = false;
    private boolean isPhoneOffHook = false;
    private boolean isPlaying = false;
    private long mockSoundDuration = 1000; // ms

    private static final int ORIGINAL_VOLUME = 7;
    private static final int MAX_VOLUME = 7;
    private static final int BELL_VOLUME = 5;

    private int alarmVolume = ORIGINAL_VOLUME;

    @Override
    public void finishBellSound() {
        isPlaying = false;
        alarmVolume = ORIGINAL_VOLUME;
    }

    @Override
    public int getAlarmMaxVolume() {
        return MAX_VOLUME;
    }

    @Override
    public int getAlarmVolume() {
        return alarmVolume;
    }

    @Override
    public int getBellVolume() {
        return BELL_VOLUME;
    }

    public long getSoundDuration() {
        return mockSoundDuration;
    }

    @Override
    public boolean isBellSoundPlaying() {
        return isPlaying;
    }

    @Override
    public boolean isPhoneMuted() {
        return isPhoneMuted;
    }

    @Override
    public boolean isPhoneOffHook() {
        return isPhoneOffHook;
    }

    @Override
    public boolean isSettingMuteOffHook() {
        return isSettingMuteOffHook;
    }

    @Override
    public boolean isSettingMuteWithPhone() {
        return isSettingMuteWithPhone;
    }

    @Override
    public void setAlarmVolume(int volume) {
    }

    public void setPhoneMuted(boolean value) {
        isPhoneMuted = value;
    }

    public void setPhoneOffHook(boolean value) {
        isPhoneOffHook = value;
    }

    public void setSettingMuteOffHook(boolean value) {
        isSettingMuteOffHook = value;
    }

    public void setSettingMuteWithPhone(boolean value) {
        isSettingMuteWithPhone = value;
    }

    public void setSoundDuration(long duration) {
        mockSoundDuration = duration;
    }

    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void startBellSound(final Runnable runWhenDone) {
        isPlaying = true;
        alarmVolume = BELL_VOLUME;

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                finishBellSound();
                if (runWhenDone != null) {
                    runWhenDone.run();
                }
            }
        }, mockSoundDuration);
    }

}
