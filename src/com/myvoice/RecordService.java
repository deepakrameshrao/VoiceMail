package com.myvoice;

import java.io.IOException;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;

public class RecordService extends Service{

	 static MediaRecorder recorder;
	 static MediaPlayer player;
	 boolean recording = false;
	 static String incomingNumber = null;
	 
	 public static Context mContext;
	 /**
	     * TelephonyManager instance used by this activity
	     */
	    private TelephonyManager tm;
	    
	    /**
	     * AIDL access to the telephony service process
	     */
	    private ITelephony telephonyService;
	 
	 
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext = this.getApplicationContext();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		Log.v("RecordService","----------Deepak: on RECV ");
		
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		
			 Log.v("RecordService","----------Deepak: RINGING ");
	        RecordService.incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
	        // grab an instance of telephony manager
	        //tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
	        
	        // connect to the underlying Android telephony system
	        //connectToTelephonyService();
			PlatformCallUtils.autoAnswerCall();

			/*try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
	        recorder = new MediaRecorder();
	        /*recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
	        if(incomingNumber != null)
	        	recorder.setOutputFile("/sdcard/voicemail"+incomingNumber+".3gpp");
	        else
	        	recorder.setOutputFile("/sdcard/voicemail.3gpp");*/
	       
			
			//My changes start
			player = new MediaPlayer();
			//player.setVolume(leftVolume, rightVolume)
			AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
			//am.setStreamVolume(AudioManager.MODE_IN_CALL, am.getStreamMaxVolume(AudioManager.MODE_IN_CALL), 0);
			//player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
			Log.v("call prompt","----------Deepak: Player start");
            player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            
            try {
				player.setDataSource("/sdcard/voicemail.3ga");
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalStateException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			player.setOnCompletionListener(new OnCompletionListener() {
			
				public void onCompletion(MediaPlayer mp) {
					Log.v("call prompt","----------Deepak: STOP PLAY. START RECORD");
					try {
					recorder.reset();
					recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			        if(incomingNumber != null)
			        	recorder.setOutputFile("/sdcard/vm"+incomingNumber+System.currentTimeMillis()+".3gpp");
			        else
			        	recorder.setOutputFile("/sdcard/vm"+System.currentTimeMillis()+".3gpp");
					
							recorder.prepare();
					
					recorder.start();   // Recording is now started
					recording = true;
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			});
			
			/*try {
				player.setDataSource("/sdcard/voicemail.mp3");
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalStateException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			
			
			//answer();
			try {
            	player.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			player.start();
            Log.v("call prompt","----------Deepak: Player started");
            
			IntentFilter ph = new IntentFilter (TelephonyManager.ACTION_PHONE_STATE_CHANGED);
			registerReceiver(PhoneState, ph);
	}
	@Override
	public void onDestroy() {
		if(player.isPlaying()){
			player.stop();
			player.release();
		}
		if(recording){
			recorder.stop();
			recorder.release();
			recording = false;
		}
		super.onDestroy();
	}
	
	 /** 
     * Connect to the telephony service
     */
    @SuppressWarnings("unchecked") private void connectToTelephonyService() {
            try 
            {
                    // "cheat" with Java reflection to gain access to TelephonyManager's ITelephony getter
                    Class c = Class.forName(tm.getClass().getName());
                    Method m = c.getDeclaredMethod("getITelephony");
                    m.setAccessible(true);
                    telephonyService = (ITelephony)m.invoke(tm);

            } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("call prompt","FATAL ERROR: could not connect to telephony subsystem");
                    Log.e("call prompt","Exception object: "+e);
            }               
    }
    
    /**
     * AIDL/ITelephony technique for answering the phone
     */
    private void answerCallAidl() {
            try {
            	
                	//recorder.start();   // Recording is now started
                    telephonyService.silenceRinger();
                    telephonyService.answerRingingCall();
                    try {
                    	player.prepare();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					player.start();
                    Log.v("call prompt","----------Deepak: Player start");
            } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.e("call prompt","FATAL ERROR: call to service method answerRiningCall failed.");
                    Log.e("call prompt","Exception object: "+e);
            }               
    }
    
    /** 
     * AIDL/ITelephony technique for ignoring calls
     */
    private void ignoreCallAidl() {
            try 
            {
                    telephonyService.silenceRinger();
                    telephonyService.endCall();
            } 
            catch (RemoteException e) 
            {
                    e.printStackTrace();
                    Log.e("call prompt","FATAL ERROR: call to service method endCall failed.");
                    Log.e("call prompt","Exception object: "+e);
            }
    }
    
    void answer() {
		
	//special thanks the auto answer open source app
	//which demonstrated this answering functionality
		//Intent answer = new Intent(Intent.ACTION_MEDIA_BUTTON);
  		//answer.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
  		//sendOrderedBroadcast(answer, null);
		
		//due to inconsistency, replaced with more reliable cheat method Tedd discovered
		answerCallAidl();
		
  		//moveTaskToBack(true);
	}
	
	void reject() {
		
		ignoreCallAidl();
        
        //moveTaskToBack(true);
  		//finish();
	}
	
	
	//we don't want to exist after phone changes to active state or goes back to idle
	//we also don't want to rely on this receiver to close us after success
	BroadcastReceiver PhoneState = new BroadcastReceiver() {
		
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v("call prompt","----------Deepak: OnRECV....");
			if (!intent.getAction().equals("android.intent.action.PHONE_STATE")) return;
			String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
			if (/*state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) ||*/ state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
				if(recording){
					Log.v("call prompt","----------Deepak: OnRECV. Send Notification....");
					NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					Intent in = new Intent(getApplicationContext(), VoiceMailList.class);
					PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
					Notification ni = new Notification(R.drawable.droidxvoicemailiconnormal, "Hello!! you have a new voice mail from: "+incomingNumber, System.currentTimeMillis());
					ni.setLatestEventInfo(getApplicationContext(), "New Voicemail", "Hello!! you have a new voice mail from: "+incomingNumber, contentIntent);
					ni.flags = Notification.FLAG_AUTO_CANCEL;
					nm.notify(0, ni);
					
					recorder.stop();
					recorder.release();
					recording = false;
					stopSelf();
				}
				/*if (!success && !isFinishing()) {
					//no known intentional dismissal and not already finishing
					//need to finish to avoid handing out after missed calls
					Log.v("call start or return to idle","no user input success - closing the prompt");
					success = true;//so re-start won't fire
					finish();
				}*/
			}

			return;
	    		
		}};

}
