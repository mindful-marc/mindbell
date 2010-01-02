package org.mindbell;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;

public class MindBell extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bell);
    }
    
    protected void onStart() {
    	super.onStart();
    	
        MediaPlayer mp = MediaPlayer.create(this, R.raw.bell10s);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				mp.release();
				MindBell.this.finish();
			}
        });
        mp.start();
        
    }
}