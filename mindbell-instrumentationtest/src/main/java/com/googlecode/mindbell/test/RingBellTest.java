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
package com.googlecode.mindbell.test;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

import com.googlecode.mindbell.MindBell;
import com.googlecode.mindbell.test.util.MockContextAccessor;
import com.googlecode.mindbell.util.AndroidContextAccessor;
import com.googlecode.mindbell.util.ContextAccessor;

public class RingBellTest extends AndroidTestCase {

    private Context                    context;
    private final Map<String, Boolean> booleanSettings = new HashMap<String, Boolean>();

    private final Runnable             dummyRunnable   = new Runnable() {
                                                           public void run() {
                                                           };
                                                       };

    private Runnable getDummyRunnable() {
        return dummyRunnable;
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

    private void setContextMuteOffHook(boolean value) {
        setBooleanContext(AndroidContextAccessor.KEYMUTEOFFHOOK, value);
    }

    private void setContextMuteWithPhone(boolean value) {
        setBooleanContext(AndroidContextAccessor.KEYMUTEWITHPHONE, value);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        context = getContext();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = sp.edit();
        for (String s : booleanSettings.keySet()) {
            spe.putBoolean(s, booleanSettings.get(s));
        }
        spe.commit();
    }

    public void testMuteOffHook_false() {
        // setup
        setContextMuteOffHook(false);
        // exercise
        ContextAccessor ca = new AndroidContextAccessor(context);
        // verify
        assertFalse(ca.isSettingMuteOffHook());
    }

    public void testMuteOffHook_true() {
        // setup
        setContextMuteOffHook(true);
        // exercise
        ContextAccessor ca = new AndroidContextAccessor(context);
        // verify
        assertTrue(ca.isSettingMuteOffHook());
    }

    public void testMuteWithPhone_false() {
        // setup
        setContextMuteWithPhone(false);
        // exercise
        ContextAccessor ca = new AndroidContextAccessor(context);
        // verify
        assertFalse(ca.isSettingMuteWithPhone());
    }

    public void testMuteWithPhone_true() {
        // setup
        setContextMuteWithPhone(true);
        // exercise
        ContextAccessor ca = new AndroidContextAccessor(context);
        // verify
        assertTrue(ca.isSettingMuteWithPhone());
    }

    public void testPreconditions() {
        assertNotNull(context);
    }

    public void testRingBell_Mock_false() {
        // setup
        MockContextAccessor mca = new MockContextAccessor();
        mca.setPhoneMuted(true);
        mca.setSettingMuteWithPhone(true);
        // exercise
        boolean isRinging = MindBell.ringBell(mca, null);
        // verify
        assertFalse(isRinging);
    }

    public void testRingBell_Mock_true() {
        // setup
        MockContextAccessor mca = new MockContextAccessor();
        mca.setPhoneMuted(false);
        mca.setPhoneOffHook(false);
        // exercise
        boolean isRinging = MindBell.ringBell(mca, null);
        // verify
        assertTrue(isRinging);
    }

    public void testRingBell_throwNPE1() {
        try {
            MindBell.ringBell((ContextAccessor) null, getDummyRunnable());
            fail("Should have thrown null pointer exception");
        } catch (NullPointerException npe) {
            // OK, expected
        }
    }

    public void testRingBell_true() {
        // setup
        setContextMuteWithPhone(false);
        setContextMuteOffHook(false);
        // exercise
        boolean isRinging = MindBell.ringBell(context, null);
        // verify
        assertTrue(isRinging);
    }

}
