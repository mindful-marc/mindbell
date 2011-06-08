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

/**
 * Convenience access to information from the context. Can be replaced by test implementation.
 * 
 * @author marc
 * 
 */
public abstract class ContextAccessor {

    public abstract void finishBellSound();

    public abstract int getAlarmMaxVolume();

    public abstract int getAlarmVolume();

    public int getBellDefaultVolume() {
        return (int) Math.ceil(getAlarmMaxVolume() * 0.67);
    }

    public abstract int getBellVolume();

    public abstract boolean isBellSoundPlaying();

    public boolean isMuteRequested() {
        boolean mute = false;
        // If we are to be muted, don't go any further:
        if (isSettingMuteWithPhone() && isPhoneMuted()) {
            mute = true;
            showMessage("muting bell because the phone is muted");
        }

        // If settings ask us to mute if the phone is active in a call, and that
        // is the case, do not play.
        if (isSettingMuteOffHook() && isPhoneOffHook()) {
            mute = true;
            showMessage("muting bell because the phone is off hook");
        }
        return mute;
    }

    public abstract boolean isPhoneMuted();

    public abstract boolean isPhoneOffHook();

    public abstract boolean isSettingMuteOffHook();

    public abstract boolean isSettingMuteWithPhone();

    public abstract void setAlarmVolume(int volume);

    public abstract void showMessage(String message);

    public abstract void startBellSound(final Runnable runWhenDone);

}
