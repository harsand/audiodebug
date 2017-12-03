package com.hxiong.audiodebug.activity;

import java.io.File;

import com.hxiong.audiodebug.BaseActivity;
import com.hxiong.audiodebug.R;
import android.annotation.SuppressLint;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class BaseModuleActivity extends BaseActivity {
    
	protected TextView moduleTitle;
	
	/**
	 * 如果子类使用的布局包含了R.layout.activity_base_module_title
	 * 可以直接调用此方法初始化标题，必须在setContentView()后调用
	 * @param title
	 * @date 2016年3月26日 下午7:03:27
	 */
	protected void initModuleTitle(String title){
		moduleTitle=(TextView)findViewById(R.id.base_module_item_title);
		moduleTitle.setText(title);
		
		/**
		 * 给返回添加监听器,点击时结束当前activity
		 */
		findViewById(R.id.goback_layout).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
	}
	
	/**
	 * 当前应用默认存储的文件夹
	 * @return 文件夹的路径
	 * @date 2016年5月30日 下午11:40:05
	 */
	@SuppressLint("SdCardPath")
	protected String getRecordDir(){
		String RecordDir="";
		try{
			RecordDir=Environment.getExternalStorageDirectory().getPath();
			RecordDir=RecordDir+"/AudioDebug";
		}catch(Throwable error){
			RecordDir="/sdcard/AudioDebug";
			error.printStackTrace();
		}
		File file=new File(RecordDir);
		if(!file.exists()){
			file.mkdir();
		}
		file=null;  //方便gc回收 ？？
		return RecordDir;
	}
	
	/**
	 * MediaRecorder 录音文件的保存位置，默认目录
	 * @return 默认文件夹的路径
	 * @date 2016年5月30日 下午11:44:01
	 */
	protected String getMediaRecorderDir(){
		String mediaRecorderDir=getRecordDir()+"/mediaRecorderDir";
		File file=new File(mediaRecorderDir);
		if(!file.exists()){
			file.mkdir();
		}
		file=null;  //方便gc回收 ？？
		return mediaRecorderDir;
	}
	
}
