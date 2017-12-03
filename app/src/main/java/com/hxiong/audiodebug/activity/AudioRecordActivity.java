package com.hxiong.audiodebug.activity;


import com.hxiong.audiodebug.R;

import android.os.Bundle;


public class AudioRecordActivity extends BaseModuleActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_audiorecord);
		
		initModuleTitle("AudioRecord录音");
		
		init();
	}
	/**
	 *
	 * @author hxiong
	 * @date   2017/1/5 0:34
	 */
	private void init(){

	}

}
