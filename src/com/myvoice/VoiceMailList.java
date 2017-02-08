package com.myvoice;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController.AnimationParameters;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class VoiceMailList extends ListActivity 
{
	private static final String mSdcardPath = Environment.getExternalStorageDirectory().getPath();
	List<File> voicemailFileList;
	VoicemailAdapter vmAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		voicemailFileList = new ArrayList<File>();
		
		File sdcard = new File(mSdcardPath);
		for (File file : sdcard.listFiles()) {
			Log.d("LST", "-------Deepak: FILE: "+ file.getName());
			if(file.getName().startsWith("voicemail")){
				Log.d("LST", "-------Deepak: Adding file to the list");
				voicemailFileList.add(file);
			}
		}
		
		vmAdapter = new VoicemailAdapter(this);
		getListView().setVisibility(0);
		//getListView().setDividerHeight(5);
		
		//getListView().setBackgroundColor(android.R.color.background_light);
		getListView().setCacheColorHint(android.R.color.transparent);
		
		getListView().setAdapter(vmAdapter);
		
		//setContentView(getListView());
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	public class VoicemailAdapter extends BaseAdapter{

		 private LayoutInflater mInflater;
		 public VoicemailAdapter(Context context) {
			 mInflater = LayoutInflater.from(context);
		 }
		
		public int getCount() {
			return voicemailFileList.size();
		}

		public Object getItem(int position) {
			return voicemailFileList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d("LST", "-------Deepak: GETVIEW");
			if (convertView == null) {
				Log.d("LST", "-------Deepak: CONVERTVIEW = NULL");
                convertView = mInflater.inflate(R.layout.list_layout,
                        null);
			}
			Log.d("LST", "-------Deepak: CONVERTVIEW : "+ convertView);
                TextView tx = (TextView) convertView.findViewById(R.id.list_text);
                if(tx != null){
                	tx.setText(voicemailFileList.get(position).getName());
                	Log.d("LST", "-------Deepak: Adding to TEXTVIEW");
                }
			
			return convertView;
		}
		
	}
}
