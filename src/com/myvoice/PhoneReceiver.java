package com.myvoice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.v("PhoneReceiver", "----------Deepak: on RECV ");

		SharedPreferences p = context.getSharedPreferences("myvoicemail", 0);

		boolean prompt = p.getBoolean("voicemail", false);
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		String number = intent
				.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

		if (state.equals(TelephonyManager.EXTRA_STATE_RINGING) && prompt) {
			Log.v("PhoneReceiver", "----------Deepak: RINGING && prompt ");

			// MyVoiceMail.launch(context, number);//start it immediately when
			// state is received.
			Intent in = new Intent(context, RecordService.class);
			in.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER, number);
			context.startService(in);

			// the only time we manage to start visible phase before phone
			// is call coming from sleep - prompt handles this itself in onStop
			// In other words, the phone start causes our onStop
			// then we take control back!
		}
		else if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
			Log.v("PhoneReceiver", "----------Deepak: RINGING ");
			
		}
	}
}