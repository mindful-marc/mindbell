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
package com.googlecode.mindbell.test.util;

import com.googlecode.mindbell.util.ContextAccessor;

public class MockContextAccessor extends ContextAccessor {
    private boolean isSettingMuteWithPhone = false;
    private boolean isSettingMuteOffHook   = false;
    private boolean isPhoneMuted           = false;
    private boolean isPhoneOffHook         = false;

    @Override
    public boolean isPhoneMuted() {
        return isPhoneMuted;
    }

    @Override
    public boolean isPhoneOffHook() {
        return isPhoneOffHook;
    }

    @Override
    public boolean isSettingMuteOffHook() {
        return isSettingMuteOffHook;
    }

    @Override
    public boolean isSettingMuteWithPhone() {
        return isSettingMuteWithPhone;
    }

    public void setPhoneMuted(boolean value) {
        isPhoneMuted = value;
    }

    public void setPhoneOffHook(boolean value) {
        isPhoneOffHook = value;
    }

    public void setSettingMuteOffHook(boolean value) {
        isSettingMuteOffHook = value;
    }

    public void setSettingMuteWithPhone(boolean value) {
        isSettingMuteWithPhone = value;
    }

    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

}
