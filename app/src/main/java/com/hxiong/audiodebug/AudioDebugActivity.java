package com.hxiong.audiodebug;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.hxiong.audiodebug.activity.KeySoundActivity;
import com.hxiong.audiodebug.activity.MediaPlayerActivity;
import com.hxiong.audiodebug.activity.MediaRecorderActivity;
import com.hxiong.audiodebug.activity.RingtoneActivity;
import com.hxiong.audiodebug.activity.VolumeSettingActivity;
import com.hxiong.audiodebug.adapter.DebugItem;
import com.hxiong.audiodebug.adapter.DebugItemAdapter;

public class AudioDebugActivity extends BaseActivity {

	/**
	 * 一旦定义后就不可以再修改
	 */
	private final DebugItem[] debugItems={
		new DebugItem(R.drawable.module_icon_bg_1,"使用AudioManager播放按键声音",KeySoundActivity.class),	
		new DebugItem(R.drawable.module_icon_bg_2,"使用AudioManager设置系统音量",VolumeSettingActivity.class),
		new DebugItem(R.drawable.module_icon_bg_3,"使用RingtoneManager设置系统铃声",RingtoneActivity.class),
		new DebugItem(R.drawable.module_icon_bg_4,"使用MediaRecorder实现录音",MediaRecorderActivity.class),
//		new DebugItem(R.drawable.module_icon_bg_5,"使用AudioRecord实现录音",AudioRecordActivity.class),
		new DebugItem(R.drawable.module_icon_bg_6,"使用MediaPlayer播放音频",MediaPlayerActivity.class),
//		new DebugItem(R.drawable.module_icon_bg_7,"使用AudioTrack播放音频",AudioTrackActivity.class),
//		new DebugItem(R.drawable.module_icon_bg_8,"使用AudioGroup实现多人在线通话",VolumeSettingActivity.class),
	};
	
	/**
	 * 显示各个模块的列表控件
	 */
	private ListView moduleListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_debug);
		
		moduleListView=(ListView)findViewById(R.id.debug_module_listView);
		DebugItemAdapter adapter=new DebugItemAdapter(this,debugItems);
		moduleListView.setAdapter(adapter);
		moduleListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   
				startActivity(position);
			}
		});
		printLog("********************************application create***********************************");
        printLog("DebugItemAdapter>>>>>>"+DebugItemAdapter.class.getClassLoader());
        printLog("DebugItemAdapter>>>>>>"+DebugItemAdapter.class.getClassLoader().getParent());
        printLog("DebugItemAdapter>>>>>>"+DebugItemAdapter.class.getSuperclass());
	}
	
	/**
	 * 启动另一个activity
	 * @param whichModule  根据按下的item，跳转到对应的activity
	 * @date 2016年3月23日 下午11:26:11
	 */
	private final void startActivity(int whichModule){
		try{
			printLog("startActivity class:"+debugItems[whichModule].mClass.getSimpleName());
			Intent intent=new Intent(AudioDebugActivity.this,debugItems[whichModule].mClass);
			startActivity(intent);
		}catch(Throwable error){
			printLog("startActivity:"+debugItems[whichModule].mClass.getSimpleName()+" error:"+error.getMessage());
		}
	}
	
	/**
	 * 按下返回键时调用，弹出提示框，询问用户是否真的推出
	 */
	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
		.setTitle("提示信息")
		.setIcon(R.drawable.dialog_icon)
		.setMessage("您要退出当前应用？")
		.setPositiveButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// do nothing
			}
		})
		.setNegativeButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();   //退出
			}
		}).show();
	}
	
	@Override
	protected void onDestroy() {
		printLog("********************************application destory***********************************");
		closeLogFile();   //记得关闭保存log的文件，防止数据丢失
		super.onDestroy();
	}
}
