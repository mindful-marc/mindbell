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
package com.googlecode.mindbell.util;

/**
 * @author marc
 * 
 */
public class VolumeConverter {
    public static final float MINUS_ONE_DB = 0.891250938f;
    public final int mDynamicRangeDB;
    public final int mMaxProgress;

    public VolumeConverter(int dynamicRangeDB, int maxProgress) {
        mDynamicRangeDB = dynamicRangeDB;
        mMaxProgress = maxProgress;
    }

    /**
     * @param progress
     * @return
     */
    public float progress2volume(int progress) {
        if (progress == 0) {
            return 0;
        }
        float minusDB = ((float) (mMaxProgress - progress)) / mMaxProgress * mDynamicRangeDB;
        float vol = (float) Math.pow(10, -minusDB / 20.);
        return vol;
    }

    /**
     * @param volume2
     * @return
     */
    public int volume2progress(float aVolume) {
        if (aVolume < 0.0001) {
            return 0;
        }
        float minusDB = 20 * (float) Math.log10(aVolume / 1.);
        // limit our resolution to the dynamic range
        if (minusDB < -mDynamicRangeDB) {
            minusDB = -mDynamicRangeDB;
        }
        int progress = Math.round((mDynamicRangeDB + minusDB) / mDynamicRangeDB * mMaxProgress);
        return progress;
    }
}
