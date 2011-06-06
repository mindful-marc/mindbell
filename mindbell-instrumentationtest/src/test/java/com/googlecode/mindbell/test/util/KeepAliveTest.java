/**
 * Copyright (C) 2011 Marc Schroeder.
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
package com.googlecode.mindbell.test.util;

import junit.framework.TestCase;

import com.googlecode.mindbell.test.accessors.MockContextAccessor;
import com.googlecode.mindbell.util.KeepAlive;

/**
 * @author marc
 * 
 */
public class KeepAliveTest extends TestCase {

    public void testExpires() throws InterruptedException {
        MockContextAccessor mca = new MockContextAccessor();
        // timeout shorter than sound duration: cannot finish
        long timeout = mca.getSoundDuration() / 10;
        final KeepAlive keepAlive = new KeepAlive(mca, timeout);
        // exercise: it gets back before the timeout
        Thread th = new Thread() {
            @Override
            public void run() {
                keepAlive.ringBell();
            }
        };
        th.start();
        th.join(2 * timeout);
        if (th.isAlive()) {
            fail("KeepAlive doesn't expire as it should");
        }
    }

    public void testReturnsNaturally() throws InterruptedException {
        MockContextAccessor mca = new MockContextAccessor();
        // timeout longer than sound duration: finishing is easy
        long timeout = mca.getSoundDuration() * 10;
        final KeepAlive keepAlive = new KeepAlive(mca, timeout);
        // exercise: it gets back before the timeout
        Thread th = new Thread() {
            @Override
            public void run() {
                keepAlive.ringBell();
            }
        };
        th.start();
        th.join(2 * timeout);
        if (th.isAlive()) {
            fail("KeepAlive doesn't return naturally as it should");
        }
    }

}
