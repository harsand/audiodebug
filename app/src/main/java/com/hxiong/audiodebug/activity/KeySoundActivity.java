package com.hxiong.audiodebug.activity;

import com.hxiong.audiodebug.R;
import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class KeySoundActivity extends BaseModuleActivity implements OnSeekBarChangeListener{

	private AudioManager mAudioManager;
	@SuppressLint("InlinedApi")
	private KeySoundItem[] mKeySoundItems={
		new KeySoundItem("按键点击提示音",AudioManager.FX_KEY_CLICK),	
		new KeySoundItem("焦点上移提示音",AudioManager.FX_FOCUS_NAVIGATION_UP),
		new KeySoundItem("焦点下移提示音",AudioManager.FX_FOCUS_NAVIGATION_DOWN),
		new KeySoundItem("焦点左移提示音",AudioManager.FX_FOCUS_NAVIGATION_LEFT),
		new KeySoundItem("焦点右移提示音",AudioManager.FX_FOCUS_NAVIGATION_RIGHT),
		new KeySoundItem("输入法标准按键提示音",AudioManager.FX_KEYPRESS_STANDARD),
		new KeySoundItem("输入法空格按键提示音",AudioManager.FX_KEYPRESS_SPACEBAR),
		new KeySoundItem("输入法删除按键提示音",AudioManager.FX_KEYPRESS_DELETE),
		new KeySoundItem("输入法返回按键提示音",AudioManager.FX_KEYPRESS_RETURN),
		new KeySoundItem("无效按键提示音",AudioManager.FX_KEYPRESS_INVALID)
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_key_sound);
		
		initModuleTitle("播放系统按键声音");
		
		/**
		 * 用于防止item的布局控件
		 */
		LinearLayout mLinearLayout=(LinearLayout)findViewById(R.id.sound_content_layout);
		
		/**
		 * 初始化每一个item和显示的内容
		 */
		initKeySoundItem(mLinearLayout,mKeySoundItems);
		
	}
	
	/**
	 * 初始化item的具体内容和添加监听器
	 * @param linearLayout
	 * @param items
	 * @date 2016年3月27日 下午11:19:48
	 */
	@SuppressLint("InflateParams")
	private void initKeySoundItem(final LinearLayout linearLayout,KeySoundItem[] items){
		
		if(items==null||items.length<-1){
			return;
		}
		
		for(int i=0;i<items.length;i++){
			View itemView=getLayoutInflater().inflate(R.layout.activity_key_sound_item, null);
			final TextView titleView=(TextView)itemView.findViewById(R.id.keysound_item_title);
			items[i].mSeekBar=(SeekBar)itemView.findViewById(R.id.play_volume_seekbar);
			items[i].mHintView=(TextView)itemView.findViewById(R.id.play_volume_hint_view);		
			titleView.setText(items[i].itemTitle);
			items[i].mSeekBar.setOnSeekBarChangeListener(this);
			items[i].maxVolume=getAudioManager().getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
			linearLayout.addView(itemView);
		}
	}
	
	/**
	 * 通知进度已经被修改。客户端可以使用fromUser参数区分用户触发的改变还是编程触发的改变。
	 * seekBar  当前被修改进度的SeekBar
	 * progress 当前的进度值。此值的取值范围为0到max之间。Max为用户通过setMax(int)设置的值，默认为100
	 * fromUser 如果是用户触发的改变则返回True
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		
		for(int i=0;i<mKeySoundItems.length;i++){
			if(mKeySoundItems[i].mSeekBar==seekBar){
				printLog("onProgressChanged:progress is "+progress+",fromUser is "+fromUser);
				handleProgressChanged(mKeySoundItems[i],progress,fromUser);
				return ;
			}
		}
	}
    
	/**
	 * 通知用户已经开始一个触摸拖动手势。客户端可能需要使用这个来禁用seekbar的滑动功能。
	 * seekBar 触摸的SeekBar
	 */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		//printLog("onStartTrackingTouch had been called...");
	}
    
	/**
	 * 通知用户触摸手势已经结束。户端可能需要使用这个来启用seekbar的滑动功能。
	 * seekBar 触摸的SeekBar
	 */
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		//printLog("onStopTrackingTouch had been called...");
	}
	
	/**
	 * 播放对应的按键声音
	 * @param keySoundItem
	 * @param progress
	 * @param fromUser
	 * @date 2016年3月27日 下午11:45:24
	 */
	private void handleProgressChanged(final KeySoundItem keySoundItem,int progress,boolean fromUser){
		try{
			/**
			 * 计算要播放的音量大小
			 */
			float volume=(float)progress*keySoundItem.maxVolume/100;
			String hintText="effectType:"+keySoundItem.mEffectType+" maxVolume:"+keySoundItem.maxVolume+" playVolume:"+volume;
			keySoundItem.mHintView.setText(hintText);
			printLog("playSoundEffect() effectType:"+keySoundItem.mEffectType+",volume:"+volume);
			getAudioManager().playSoundEffect(keySoundItem.mEffectType, volume);
		}catch(Throwable error){
			printLog("handleProgressChanged error:"+error.getMessage());
		}
	}
	/**
	 * 
	 * @return AudioManager
	 * @date 2016年3月27日 下午11:13:23
	 */
	private AudioManager getAudioManager(){
		if(mAudioManager==null)
			mAudioManager=(AudioManager)getSystemService(AUDIO_SERVICE);
		return mAudioManager;
	}
	
	/**
	 * 
	 * @author hxiong
	 * @date 2016年3月27日 下午11:14:40
	 */
	class KeySoundItem{
		 String itemTitle;
		 SeekBar mSeekBar;
		 TextView mHintView;
		 final int mEffectType;
		 int maxVolume;
		
		 KeySoundItem(String title,int effectType){
			this.itemTitle=title;
			this.mEffectType=effectType;
		}
	}
}
