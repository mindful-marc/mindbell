/**
 * 
 */
package com.googlecode.mindbell.util;

import com.googlecode.mindbell.MindBell;
import com.googlecode.mindbell.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;


/**
 * @author marc
 *
 */
public class AndroidContextAccessor extends ContextAccessor {
	public static final int KEYMUTEOFFHOOK = R.string.keyMuteOffHook;
	public static final int KEYMUTEWITHPHONE = R.string.keyMuteWithPhone;
	

	private Context context;
	
	public AndroidContextAccessor(Context context) {
		this.context = context;
	}


	private boolean getBooleanSetting(int keyID) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		String key = context.getString(keyID);
		return settings.getBoolean(key, false);
	}

	@Override
	public void showMessage(String message) {
		MindBell.logDebug(message);
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean isPhoneMuted() {
		final AudioManager audioMan = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		return audioMan.getStreamVolume(AudioManager.STREAM_RING) == 0;
	}

	@Override
	public boolean isPhoneOffHook() {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getCallState() != TelephonyManager.CALL_STATE_IDLE;
	}

	@Override
	public boolean isSettingMuteWithPhone() {
		return getBooleanSetting(KEYMUTEWITHPHONE);
	}

	@Override
	public boolean isSettingMuteOffHook() {
		return getBooleanSetting(KEYMUTEOFFHOOK);
	}

}
