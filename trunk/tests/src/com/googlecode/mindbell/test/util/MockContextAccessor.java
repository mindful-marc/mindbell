package com.googlecode.mindbell.test.util;

import com.googlecode.mindbell.util.ContextAccessor;

public class MockContextAccessor extends ContextAccessor {
	private boolean isSettingMuteWithPhone = false;
	private boolean isSettingMuteOffHook = false;
	private boolean isPhoneMuted = false;
	private boolean isPhoneOffHook = false;
	
	public void setSettingMuteWithPhone(boolean value) {
		isSettingMuteWithPhone = value;
	}

	public void setSettingMuteOffHook(boolean value) {
		isSettingMuteOffHook = value;
	}
	
	public void setPhoneMuted(boolean value) {
		isPhoneMuted = value;
	}

	public void setPhoneOffHook(boolean value) {
		isPhoneOffHook = value;
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
	public boolean isSettingMuteWithPhone() {
		return isSettingMuteWithPhone;
	}

	@Override
	public boolean isSettingMuteOffHook() {
		return isSettingMuteOffHook;
	}

	@Override
	public void showMessage(String message) {
		System.out.println(message);
	}

}
