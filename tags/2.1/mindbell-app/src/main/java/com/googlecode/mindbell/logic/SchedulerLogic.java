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
package com.googlecode.mindbell.logic;

import java.util.Random;

import com.googlecode.mindbell.accessors.PrefsAccessor;
import com.googlecode.mindbell.util.TimeOfDay;

/**
 * @author marc
 * 
 */
public class SchedulerLogic {
    private static Random random = new Random();

    public static long getNextTargetTimeMillis(long nowMillis, PrefsAccessor prefs) {
        long meanInterval = prefs.getInterval();
        long randomInterval = getRandomInterval(meanInterval);
        long targetTimeMillis = nowMillis + randomInterval;
        if (!prefs.isDaytime(new TimeOfDay(targetTimeMillis))) {
            // target is in night time
            long dayStartMillis = prefs.getNextDaytimeStartInMillis();
            targetTimeMillis = dayStartMillis + randomInterval - meanInterval / 2;
            assert targetTimeMillis >= dayStartMillis;
        }
        return targetTimeMillis;
    }

    /**
     * Compute a random value following a Gaussian distribution around the given mean. The value is guaranteed not to fall below
     * 0.5 * mean and not above 1.5 * mean.
     * 
     * @param mean
     * @return
     */
    private static long getRandomInterval(long mean) {
        long value = (long) (mean * (1.0 + 0.3 * random.nextGaussian()));
        if (value < mean / 2) {
            value = mean / 2;
        }
        if (value > 3 * mean / 2) {
            value = 3 * mean / 2;
        }
        return value;
    }
}
