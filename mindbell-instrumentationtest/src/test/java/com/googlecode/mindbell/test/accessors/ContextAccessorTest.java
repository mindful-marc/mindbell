package com.googlecode.mindbell.test.accessors;

import junit.framework.TestCase;

import com.googlecode.mindbell.accessors.ContextAccessor;

public class ContextAccessorTest extends TestCase {

    private ContextAccessor createContextAccessor() {
        return new MockContextAccessor();
    }

    public void testAlarmVolume() {
        // setup
        ContextAccessor ca = createContextAccessor();
        // exercise
        ca.startBellSound(null);
        // verify
        assertEquals(ca.getAlarmMaxVolume(), ca.getAlarmVolume());
    }

    public void testFinish() {
        // setup
        ContextAccessor ca = createContextAccessor();
        ca.startBellSound(null);
        // exercise
        ca.finishBellSound();
        // verify
        assertFalse(ca.isBellSoundPlaying());

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

    public void testMuted_true() {
        // setup
        MockContextAccessor ca = new MockContextAccessor();
        ca.setPhoneMuted(true);
        ca.setSettingMuteWithPhone(true);
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

    public void testOffHook_true() {
        // setup
        MockContextAccessor ca = new MockContextAccessor();
        ca.setPhoneOffHook(true);
        ca.setSettingMuteOffHook(true);
        // exercise/verify
        assertTrue(ca.isMuteRequested());
    }

    public void testOriginalVolume() {
        // setup
        ContextAccessor ca = createContextAccessor();
        int originalVolume = ca.getAlarmVolume();
        // exercise
        ca.startBellSound(null);
        ca.finishBellSound();
        // verify
        assertEquals(originalVolume, ca.getAlarmVolume());

    }

    public void testPlay() {
        // setup
        ContextAccessor ca = createContextAccessor();
        // exercise
        ca.startBellSound(null);
        // verify
        assertTrue(ca.isBellSoundPlaying());
    }

    public void testReasonableDefault() {
        ContextAccessor ca = createContextAccessor();
        float bellDefaultVolume = ca.getBellDefaultVolume();
        assertTrue(0 <= bellDefaultVolume);
        assertTrue(bellDefaultVolume <= 1);
    }
}
