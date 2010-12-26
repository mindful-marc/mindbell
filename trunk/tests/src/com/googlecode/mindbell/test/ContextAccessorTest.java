package com.googlecode.mindbell.test;

import com.googlecode.mindbell.test.util.MockContextAccessor;
import com.googlecode.mindbell.util.ContextAccessor;

import junit.framework.TestCase;

public class ContextAccessorTest extends TestCase {

	public void testMuted_true() {
		// setup
		MockContextAccessor ca = new MockContextAccessor();
		ca.setPhoneMuted(true);
		ca.setSettingMuteWithPhone(true);
		// exercise/verify
		assertTrue(ca.isMuteRequested());
	}

	public void testMuted_false1() {
		// setup
		MockContextAccessor ca = new MockContextAccessor();
		ca.setPhoneMuted(true);
		ca.setSettingMuteWithPhone(false);
		// exercise/verify
		assertFalse(ca.isMuteRequested());
	}

	public void testMuted_false2() {
		// setup
		MockContextAccessor ca = new MockContextAccessor();
		ca.setPhoneMuted(false);
		ca.setSettingMuteWithPhone(true);
		// exercise/verify
		assertFalse(ca.isMuteRequested());
	}

	public void testOffHook_true() {
		// setup
		MockContextAccessor ca = new MockContextAccessor();
		ca.setPhoneOffHook(true);
		ca.setSettingMuteOffHook(true);
		// exercise/verify
		assertTrue(ca.isMuteRequested());
	}

	public void testOffHook_false1() {
		// setup
		MockContextAccessor ca = new MockContextAccessor();
		ca.setPhoneOffHook(true);
		ca.setSettingMuteOffHook(false);
		// exercise/verify
		assertFalse(ca.isMuteRequested());
	}

	public void testOffHook_false2() {
		// setup
		MockContextAccessor ca = new MockContextAccessor();
		ca.setPhoneOffHook(false);
		ca.setSettingMuteOffHook(true);
		// exercise/verify
		assertFalse(ca.isMuteRequested());
	}


}
