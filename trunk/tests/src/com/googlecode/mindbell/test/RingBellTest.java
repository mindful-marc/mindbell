package com.googlecode.mindbell.test;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.mindbell.MindBell;
import com.googlecode.mindbell.util.AndroidContextAccessor;
import com.googlecode.mindbell.util.ContextAccessor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

public class RingBellTest extends AndroidTestCase {

	private Context context;
	private Map<String, Boolean> booleanSettings = new HashMap<String, Boolean>();
	
	private Runnable getDummyRunnable() {
		return new Runnable() {
			public void run() {};
		};
	}
	
	@Override
	protected void setUp() throws Exception {
		context = getContext();
	}
	
	@Override
	protected void tearDown() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor spe = sp.edit();
		for (String s : booleanSettings.keySet()) {
			spe.putBoolean(s, booleanSettings.get(s));
		}
		spe.commit();
	}
	
	private void setBooleanContext(int keyID, boolean value) {
		String key = context.getString(keyID);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		boolean orig = sp.getBoolean(key, value);
		booleanSettings.put(key, orig);
		SharedPreferences.Editor spe = sp.edit();
		spe.putBoolean(key, value);
		spe.commit();
	}
	
	private void setContextMuteWithPhone(boolean value) {
		setBooleanContext(AndroidContextAccessor.KEYMUTEWITHPHONE, value);
	}

	private void setContextMuteOffHook(boolean value) {
		setBooleanContext(AndroidContextAccessor.KEYMUTEOFFHOOK, value);
	}

	
	public void testPreconditions() {
		assertNotNull(context);
	}
	
	public void testRingBell_throwNPE1() {
		try {
			MindBell.ringBell(null, getDummyRunnable());
			fail("Should have thrown null pointer exception");
		} catch (NullPointerException npe) {
			// OK, expected
		}
	}
	
	public void testRingBell() {
		MindBell.ringBell(context, null);
	}
	
	public void testMuteWithPhone_true() {
		// setup
		setContextMuteWithPhone(true);
		// exercise
		ContextAccessor ca = new AndroidContextAccessor(context);
		// verify
		assertTrue(ca.isSettingMuteWithPhone());
	}
	
	public void testMuteWithPhone_false() {
		// setup
		setContextMuteWithPhone(false);
		// exercise
		ContextAccessor ca = new AndroidContextAccessor(context);
		// verify
		assertFalse(ca.isSettingMuteWithPhone());
	}
	
	public void testMuteOffHook_true() {
		// setup
		setContextMuteOffHook(true);
		// exercise
		ContextAccessor ca = new AndroidContextAccessor(context);
		// verify
		assertTrue(ca.isSettingMuteOffHook());
	}
	
	public void testMuteOffHook_false() {
		// setup
		setContextMuteOffHook(false);
		// exercise
		ContextAccessor ca = new AndroidContextAccessor(context);
		// verify
		assertFalse(ca.isSettingMuteOffHook());
	}
	
	
	

}
