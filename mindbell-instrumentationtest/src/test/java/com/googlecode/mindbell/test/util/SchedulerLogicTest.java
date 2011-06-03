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

import com.googlecode.mindbell.util.TimeOfDay;

/**
 * @author marc
 * 
 */
public class SchedulerLogicTest extends TestCase {

    public void testRescheduleDay() {
        // setup
        MockContextAccessor ca = new MockContextAccessor();
        // ca.setCurrentTime(1343);
        MockPrefsAccessor pa = new MockPrefsAccessor();
        pa.setDaytimeStart(new TimeOfDay(9, 0));
        pa.setDaytimeEnd(new TimeOfDay(21, 0));
        // exercise
        // int targetTime = SchedulerLogic.getNextTargetTime(ca, pa);
        // verify
        // assertTrue(pa.isDaytime(targetTime));
    }
}
