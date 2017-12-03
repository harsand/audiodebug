package com.hxiong.audiodebug.activity;

import com.hxiong.audiodebug.R;
import com.hxiong.audiodebug.dialog.RingtoneDialog;
import com.hxiong.audiodebug.dialog.RingtoneDialog.OnRingtoneSelect;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RingtoneActivity extends BaseModuleActivity implements OnClickListener,OnLongClickListener{

	private final VolumeSettingItem2[] mVolumeSettingItems2={
		new VolumeSettingItem2("手机铃声",RingtoneManager.TYPE_RINGTONE),
		new VolumeSettingItem2("通知提示音",RingtoneManager.TYPE_NOTIFICATION),
		new VolumeSettingItem2("闹钟声音",RingtoneManager.TYPE_ALARM)
		//new VolumeSettingItem2("所有声音",RingtoneManager.TYPE_ALL)
	};
	
	//从文件选择铃声的对话框
	private RingtoneDialog ringDialog;
	
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ringtone);
		
		initModuleTitle("系统铃声设置");
		
		/**
		 * 获取显示每个item的布局控件
		 */
		LinearLayout mLinearLayout=(LinearLayout)findViewById(R.id.ringtone_setting_layout);
	
		/**
		 *  初始化显示的ui
		 */
		initVolumeSettingItem2(mLinearLayout,mVolumeSettingItems2);

         /**
          * 查看是否需要申请权限
          */
         chekcAndRequestPermissions(Manifest.permission.WRITE_SETTINGS);
	 }
	 
	 /**
		 * 
		 * @param layout
		 * @param items
		 * @date 2016年4月6日 下午11:35:39
		 */
		@SuppressLint("InflateParams")
		private void initVolumeSettingItem2(final LinearLayout layout,VolumeSettingItem2[] items){
			if(items==null||items.length<-1){  //防止为空的情况
				return;
			}
			
			for(int i=0;i<items.length;i++){
				View itemView=getLayoutInflater().inflate(R.layout.activity_volume_setting_item2, null);
				final TextView titleView=(TextView)itemView.findViewById(R.id.volume_setting_item_title);
				items[i].ringView=(TextView)itemView.findViewById(R.id.volume_setting_item_content);
				items[i].rootItemView=itemView.findViewById(R.id.volume_setting_item);
				titleView.setText(items[i].itemTitle);
				String ringtoneName=getRingtoneName(items[i].ringType);
				items[i].ringView.setText(ringtoneName);
				items[i].rootItemView.setOnClickListener(this); //设置监听器
				items[i].rootItemView.setOnLongClickListener(this);
				printLog("initVolumeSettingItem2() ringType:"+items[i].ringType+" ringName:"+ringtoneName);
				layout.addView(itemView);
			}
		}
		
		@Override
		public void onClick(View v) {
			for(VolumeSettingItem2 item:mVolumeSettingItems2){
				if(item.rootItemView==v){
					handleClickItem(item);
					return;
				}
			}	
		}
		
		/**
		 * 处理点击事件
		 * @param item
		 * @date 2016年4月7日 上午12:03:24
		 */
		private void handleClickItem(final VolumeSettingItem2 item){
			
			Intent intent=new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,item.ringType);  
	        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "选择"+item.itemTitle);  
	        startActivityForResult(intent, item.ringType);  //弹出铃声选择界面
		}
		
		/**
		 * 处理选择的铃声
		 * @param requestCode
		 * @param data
		 * @date 2016年4月10日 上午12:23:13
		 */
		private void handleChooseRingtone(int requestCode,Intent data){
			printLog("enter handleChooseRingtone() requestCode is "+requestCode);
			try{
				for(VolumeSettingItem2 item:mVolumeSettingItems2){
					if(requestCode==item.ringType){
						Uri pickedUri=data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
						//将我们选择的铃声选择成默认  
	                    if(pickedUri!=null){ 
	                    	RingtoneManager.setActualDefaultRingtoneUri(this, item.ringType, pickedUri);
	                    	Ringtone ringtone=RingtoneManager.getRingtone(this, pickedUri);
	                    	String ringName=ringtone.getTitle(this);
	                    	item.ringView.setText(ringName);
	                    	printLog("handleChooseRingtone() ringType:"+item.ringType+" ringName:"+ringName+" Uri:"+pickedUri.toString());
	                    }
						return ;
					}
				}
			}catch(Throwable error){
				printLog("handleChooseRingtone error:"+error.getMessage());
			}
		}
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if(resultCode==RESULT_OK){
				handleChooseRingtone(requestCode,data);
				return ;
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
		/**
		 * 
		 * @param type
		 * @return
		 * @date 2016年4月7日 上午12:12:12
		 */
		private String getRingtoneName(int type){
			  Uri ringtoneUri=RingtoneManager.getActualDefaultRingtoneUri(this, type);
			  Ringtone ringtone=RingtoneManager.getRingtone(this, ringtoneUri);
			  return ringtone.getTitle(this);
		}
		
		/**
		 * 长按时调用
		 */
		@Override
		public boolean onLongClick(View v) {
			for(VolumeSettingItem2 item:mVolumeSettingItems2){
				if(item.rootItemView==v){
					handleLongClickItem(item);
					return true;
				}
			}	
			return false;
		}
		
		/**
		 * 出来长按事件
		 * @param item
		 * @date 2016年4月10日 下午3:58:32
		 */
		private void handleLongClickItem(final VolumeSettingItem2 item){
			if(ringDialog==null){
				ringDialog=new RingtoneDialog(this);
				ringDialog.setOnRingtoneSelect(new OnRingtoneSelect() {
					@Override
					public void onSelectResult(int ringtoneResult, int ringtoneType, Uri ringtoneUri) {
						switch(ringtoneResult){
						case OnRingtoneSelect.RESULT_SUCCESS:
							showRingtoneResult(ringtoneType,ringtoneUri);
							printLog("onSelectResult ringtoneResult is "+ringtoneResult+" ringtoneType is "+ringtoneType+" ringtoneUri is "+ringtoneUri);
							break;
						default:
							printLog("onSelectResult ringtoneResult is "+ringtoneResult+" ringtoneType is "+ringtoneType);
							break;
						}
					}
				});
			}
			ringDialog.showSelectDialog(item.ringType);
			printLog("handleLongClickItem had been called...");
		}
		
		/**
		 * 将出来新添加的铃声进行显示，并且播放
		 * @param ringtoneType  铃声类型
		 * @param ringtoneUri  铃声的uri
		 * @date 2016年4月10日 下午5:33:17
		 */
		private void showRingtoneResult(int ringtoneType, Uri ringtoneUri){
			try{
				for(VolumeSettingItem2 item:mVolumeSettingItems2){
					if(item.ringType==ringtoneType){
						if(ringtoneUri!=null){
	                    	RingtoneManager.setActualDefaultRingtoneUri(this, item.ringType, ringtoneUri);
	                    	Ringtone ringtone=RingtoneManager.getRingtone(this, ringtoneUri);
	                    	String ringName=ringtone.getTitle(this);
	                    	item.ringView.setText(ringName);
	                    	printLog("handleChooseRingtone() ringType:"+item.ringType+" ringName:"+ringName+" Uri:"+ringtoneUri.toString());
	                    	ringtone.play();    //播放铃声
						}
                    	return ;  //返回
					}
				}
			}catch(Throwable error){
				printLog("showRingtoneResult error:"+error.getMessage());
			}
		}
		/**
		 * 选择铃声、提示音对应的设置栏
		 * @author hxiong
		 * @date 2016年4月6日 下午11:33:41
		 */
		class VolumeSettingItem2{
			String itemTitle;
			View rootItemView;
			TextView ringView;
			final int ringType;
			
			VolumeSettingItem2(String title,int type){
				this.itemTitle=title;
				this.ringType=type;
			}
		}

}
