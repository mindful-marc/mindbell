/**
 * 
 */
package com.googlecode.mindbell.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.googlecode.mindbell.MindBell;

/**
 * @author marc
 * 
 */
public class MindBellTest extends ActivityInstrumentationTestCase2<MindBell> {

    private Activity mActivity;
    private View     mView;

    public MindBellTest() {
        super("com.googlecode.mindbell", MindBell.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
        mView = mActivity.findViewById(com.googlecode.mindbell.R.id.bell);

    }

    public void testDummy() {
        assertTrue(true);
    }

    public void testPreconditions() {
        assertNotNull(mView);
    }
}
