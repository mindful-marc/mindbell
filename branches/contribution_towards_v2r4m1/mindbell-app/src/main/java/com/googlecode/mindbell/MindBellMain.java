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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.googlecode.mindbell.accessors.AndroidContextAccessor;
import com.googlecode.mindbell.accessors.AndroidPrefsAccessor;
import com.googlecode.mindbell.accessors.ContextAccessor;
import com.googlecode.mindbell.logic.RingingLogic;

public class MindBellMain extends Activity {
    private static final String POPUP_PREFS_FILE = "popup-prefs";
    private static final String KEY_POPUP = "popup";
    private SharedPreferences popupPrefs;

    private void checkWhetherToShowPopup() {
        if (!hasShownPopup()) {
            setPopupShown(true);
            showPopup();
        }
    }

    /**
     * @return
     */
    private boolean hasShownPopup() {
        return popupPrefs.getBoolean(KEY_POPUP, false);
    }

    /**
     * 
     */
    private void notifyIfNotActive() {
        if (!new AndroidPrefsAccessor(this).isBellActive()) {
            Toast.makeText(this, R.string.howToSet, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popupPrefs = getSharedPreferences(POPUP_PREFS_FILE, MODE_PRIVATE);
        setContentView(R.layout.main);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        switch (id) {
        case R.id.about:
            dialog = new AboutDialog(this);
            break;
        default:
            dialog = null;
        }
        return dialog;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the currently selected menu XML resource.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        MenuItem settingsItem = menu.findItem(R.id.settings);
        settingsItem.setIntent(new Intent(this, MindBellPreferences.class));
        // MenuItem aboutItem = menu.findItem(R.id.about);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            showDialog(R.id.about);
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            notifyIfNotActive();
            ContextAccessor ca = AndroidContextAccessor.get(this);
            RingingLogic.ringBell(ca, null);
        }
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            checkWhetherToShowPopup();
        }
    }

    private void setPopupShown(boolean shown) {
        popupPrefs.edit().putBoolean(KEY_POPUP, shown).commit();
    }

    /**
     * 
     */
    private void showPopup() {
        DialogInterface.OnClickListener yesListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                takeUserToOffer();
            }
        };

        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_dialog, null);
        new AlertDialog.Builder(this).setTitle(R.string.main_title_popup).setIcon(R.drawable.alarm_natural_icon)
                .setView(popupView).setPositiveButton(R.string.main_yes_popup, yesListener)
                .setNegativeButton(R.string.main_no_popup, null).show();
    }

    private void takeUserToOffer() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.main_uri_popup))));
    }
}
