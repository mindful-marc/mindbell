package com.googlecode.mindbell.util;



/**
 * Convenience access to information from the context.
 * Can be replaced by test implementation.
 * @author marc
 *
 */
public abstract class ContextAccessor {

	
	public abstract boolean isPhoneMuted();
	
	public abstract boolean isPhoneOffHook();
	
	public abstract boolean isSettingMuteWithPhone();
	
	public abstract boolean isSettingMuteOffHook();
	
    public boolean isMuteRequested() {
		boolean mute = false;
		// If we are to be muted, don't go any further:
		if (isSettingMuteWithPhone() && isPhoneMuted()) {
			mute = true;
			showMessage("muting bell because the phone is muted");
		}
		
		// If settings ask us to mute if the phone is active in a call, and that is the case, do not play.
		if (isSettingMuteOffHook() && isPhoneOffHook()) {
			mute = true;
			showMessage("muting bell because the phone is off hook");
		}
		return mute;
	}
	
	public abstract void showMessage(String message);

}
