package com.hxiong.audiodebug.activity;

import android.os.Bundle;

import com.hxiong.audiodebug.R;

public class AudioTrackActivity extends BaseModuleActivity {
       
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_mediaplayer);
		
		initModuleTitle("播放音频");
	}
}
