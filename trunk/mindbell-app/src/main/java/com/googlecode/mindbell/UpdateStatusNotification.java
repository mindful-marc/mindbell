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
package com.googlecode.mindbell;

import static com.googlecode.mindbell.MindBellPreferences.TAG;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.googlecode.mindbell.accessors.AndroidContextAccessor;

/**
 * Update status notification on changes in system settings to display an active icon or active but muted icon or no icon at all.
 * 
 * @author uwe
 */
public class UpdateStatusNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Update status notification received");
        AndroidContextAccessor.get(context).updateStatusNotification();
    }

}
