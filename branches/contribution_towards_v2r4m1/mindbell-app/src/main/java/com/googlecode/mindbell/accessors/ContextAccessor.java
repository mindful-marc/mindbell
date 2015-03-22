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

    /**
     * Check whether bell should be muted, show reason if wanted, and return reason, null otherwise.
     */
    public String getMuteRequestReason(boolean shouldShowMessage) {
        String reason = null;
        if (isSettingMuteWithPhone() && isPhoneMuted()) { // Mute bell with phone?
            reason = getReasonMutedWithPhone();
        } else if (isSettingMuteOffHook() && isPhoneOffHook()) { // Mute bell while phone is off hook (or ringing)?
            reason = getReasonMutedOffHook();
        } else if (isSettingMuteInFlightMode() && isPhoneInFlightMode()) { // Mute bell while in flight mode?
            reason = getReasonMutedInFlightMode();
        }
        if (reason != null && shouldShowMessage) {
            showMessage(reason);
        }
        return reason;
    }

    /**
     * Returns reason to mute bell as String, override when concrete context is available.
     */
    protected String getReasonMutedInFlightMode() {
        return "bell muted in flight mode";
    }

    /**
     * Returns reason to mute bell as String, override when concrete context is available.
     */
    protected String getReasonMutedOffHook() {
        return "bell muted during calls";
    }

    /**
     * Returns reason to mute bell as String, override when concrete context is available.
     */
    protected String getReasonMutedWithPhone() {
        return "bell muted with phone";
    }

    public abstract boolean isBellSoundPlaying();

    /**
     * Return whether bell should be muted and show reason message if wanted.
     */
    public boolean isMuteRequested(boolean shouldShowMessage) {
        return getMuteRequestReason(shouldShowMessage) != null;
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
