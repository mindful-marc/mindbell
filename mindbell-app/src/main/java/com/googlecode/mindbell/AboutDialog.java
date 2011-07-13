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
package com.googlecode.mindbell;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.googlecode.mindbell.util.Utils;

/**
 * @author marc
 * 
 */
public class AboutDialog extends Dialog {

    /**
     * @param context
     */
    public AboutDialog(Context context) {
        super(context);
        init(context);
    }

    /**
     * @param context
     * @param cancelable
     * @param cancelListener
     */
    public AboutDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    /**
     * @param context
     * @param theme
     */
    public AboutDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    private void init(Context context) {
        setContentView(R.layout.about_dialog);
        setTitle(R.string.menuAbout);
        WebView w = (WebView) findViewById(R.id.aboutWebview);
        String html = Utils.getResourceAsString(context, R.raw.about);
        if (html != null) {
            w.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
        }
        Button ok = (Button) findViewById(R.id.aboutOkButton);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
