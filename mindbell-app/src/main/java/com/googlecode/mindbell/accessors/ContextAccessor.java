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
    public static final float MINUS_ONE_DB = 0.891250938f;
    public static final float MINUS_THREE_DB = 0.707945784f;
    public static final float MINUS_SIX_DB = 0.501187234f;

    public abstract void finishBellSound();

    public abstract int getAlarmMaxVolume();

    public abstract int getAlarmVolume();

    public float getBellDefaultVolume() {
        return MINUS_SIX_DB;
    }

    public abstract float getBellVolume();

    public abstract boolean isBellSoundPlaying();

    public boolean isMuteRequested() {

        // Should bell be muted when phone is muted?
        if (isSettingMuteWithPhone() && isPhoneMuted()) {
            showMessage("muting bell because the phone is muted");
            return true;
        }

        // Should bell be muted when phone is off hook (or ringing)?
        if (isSettingMuteOffHook() && isPhoneOffHook()) {
            showMessage("muting bell because the phone is off hook");
            return true;
        }

        // Should bell be muted when phone is in flight mode?
        if (isSettingMuteInFlightMode() && isPhoneInFlightMode()) {
            showMessage("muting bell because the phone is is in flight mode");
            return true;
        }

        // No need to suppress the bell
        return false;
    }

    public abstract boolean isPhoneInFlightMode();

    public abstract boolean isPhoneMuted();

    public abstract boolean isPhoneOffHook();

    public abstract boolean isSettingMuteInFlightMode();

    public abstract boolean isSettingMuteOffHook();

    public abstract boolean isSettingMuteWithPhone();

    public boolean isSettingVibrate() {
        return false;
    }

    public abstract void setAlarmVolume(int volume);

    public abstract void showMessage(String message);

    public abstract void startBellSound(final Runnable runWhenDone);

}
