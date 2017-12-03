package com.hxiong.audiodebug.activity;

import com.hxiong.audiodebug.R;

import android.Manifest;
import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class VolumeSettingActivity extends BaseModuleActivity implements OnSeekBarChangeListener{
    
	 private final VolumeSettingItem[] mVolumeSettingItems={
	       new VolumeSettingItem("语音通话音量",AudioManager.STREAM_VOICE_CALL),
	       new VolumeSettingItem("系统、铃声、通知栏音量",AudioManager.STREAM_SYSTEM),
	       new VolumeSettingItem("媒体、音乐、游戏等音量",AudioManager.STREAM_MUSIC),
	       new VolumeSettingItem("闹铃音量",AudioManager.STREAM_ALARM),
	 };
	
	 private AudioManager mAudioManager;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_volume_setting);
		
		initModuleTitle("系统音量设置");
		
		/**
		 * 获取显示每个item的布局控件
		 */
		LinearLayout mLinearLayout=(LinearLayout)findViewById(R.id.volume_setting_layout);
	
	    /**
	     *  初始化列表
	     */
		initVolumeSettingItem(mLinearLayout,mVolumeSettingItems);

    }

    /**
	 * 
	 * @param layout
	 * @param items
	 * @date 2016年4月5日 下午11:28:37
	 */
	@SuppressLint("InflateParams")
	private void initVolumeSettingItem(final LinearLayout layout,VolumeSettingItem[] items){
		
		if(items==null||items.length<-1){  //防止为空的情况
			return;
		}
		
		for(int i=0;i<items.length;i++){
			View itemView=getLayoutInflater().inflate(R.layout.activity_volume_setting_item1, null);
			final TextView titleView=(TextView)itemView.findViewById(R.id.volume_item_title);
			items[i].mSeekBar=(SeekBar)itemView.findViewById(R.id.volume_setting_seekbar);
			items[i].mHintView=(TextView)itemView.findViewById(R.id.volume_setting_hint_view);		
			titleView.setText(items[i].itemTitle);
			items[i].maxVolume=getAudioManager().getStreamMaxVolume(items[i].mStreamType);
			int currentVolume=getAudioManager().getStreamVolume(items[i].mStreamType);
			String hintText="streamType:"+items[i].mStreamType+" maxVolume:"+items[i].maxVolume+" currentVolume:"+currentVolume;
			items[i].mHintView.setText(hintText);
			items[i].mSeekBar.setMax(items[i].maxVolume);   //设置最大音量限制,这样设置也会调用调用监听器
			items[i].mSeekBar.setOnSeekBarChangeListener(this);  //屏蔽上面setMax的影响
			printLog("initVolumeSettingItem() streamType:"+items[i].mStreamType+",maxVolume:"+items[i].maxVolume+",currentVolume:"+currentVolume);
			items[i].mSeekBar.setProgress(currentVolume);   //设置当前音量
			layout.addView(itemView);
		}
		
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		
		for(int i=0;i<mVolumeSettingItems.length;i++){
			if(mVolumeSettingItems[i].mSeekBar==seekBar){
				printLog("onProgressChanged:streamType is "+mVolumeSettingItems[i].mStreamType+",progress is "+progress+",fromUser is "+fromUser);
				handleVolumeSetting(mVolumeSettingItems[i],progress,fromUser);
				return ;
			}
		}
		
	}
    
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
	/**
	 * 设置当前系统音量
	 * @param item
	 * @param progress
	 * @param fromUser
	 * @date 2016年4月5日 下午11:34:48
	 */
	private void handleVolumeSetting(final VolumeSettingItem volumeSettingItem,int progress, boolean fromUser){
		try{
			String hintText="streamType:"+volumeSettingItem.mStreamType+" maxVolume:"+volumeSettingItem.maxVolume+" currentVolume:"+progress;
			volumeSettingItem.mHintView.setText(hintText);
			getAudioManager().setStreamVolume(volumeSettingItem.mStreamType, progress, 0);
		}catch(Throwable error){
			printLog("handleVolumeSetting error:"+error.getMessage());
		}
	}
	
	
	
	private AudioManager getAudioManager(){
		if(mAudioManager==null)
			mAudioManager=(AudioManager)getSystemService(AUDIO_SERVICE);
		return mAudioManager;
	}
	/**
	 * 对应音量设置栏
	 * @author hxiong
	 * @date 2016年4月5日 下午11:15:47
	 */
	class VolumeSettingItem{
		 String itemTitle;
		 SeekBar mSeekBar;
		 TextView mHintView;
		 final int mStreamType;
		 int maxVolume;
		
		 VolumeSettingItem(String title,int streamType){
			 this.itemTitle=title;
			 this.mStreamType=streamType;
		 }
	}


}
