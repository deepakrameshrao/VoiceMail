package com.myvoice;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;


public class PlatformCallUtils {

	public static void endCall() {
		try {
			TelephonyManager tm = (TelephonyManager) RecordService.mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			Class<?> c = Class.forName(tm.getClass().getName());
			Method m = c.getDeclaredMethod("getITelephony");
			m.setAccessible(true);
			ITelephony telephonyService = (ITelephony) m.invoke(tm);
			// telephonyService.silenceRinger();
			telephonyService.endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 16-Oct-2014 - IDC
	 * Auto answer call method modified as previous logic was not working with 
	 * new version of android (4.1 onwards)
	 */
	public static void autoAnswerCall() {
		Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
		buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
		RecordService.mContext.sendOrderedBroadcast(buttonDown, "android.permission.CALL_PRIVILEGED");
		
		Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
		buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
		RecordService.mContext.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
	}
}