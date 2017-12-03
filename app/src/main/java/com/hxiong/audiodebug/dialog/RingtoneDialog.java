package com.hxiong.audiodebug.dialog;

import java.io.File;
import java.util.ArrayList;

import com.hxiong.audiodebug.R;
import com.hxiong.audiodebug.adapter.FileAdapter;
import com.hxiong.audiodebug.adapter.FileItem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class RingtoneDialog extends Dialog implements View.OnClickListener,OnItemClickListener{

	private Context mContext;
	private OnRingtoneSelect mSelectCallback;
	private int mRingType;
	private FileAdapter adapter;
	private boolean isScaning;
	private String currentDir;
	/**
	 * 构造函数
	 * @param context
	 */
	public RingtoneDialog(Context context){
		this(context,R.style.RingtoneDialogTheme);
	}
	
	public RingtoneDialog(Context context, int themeResId) {
		super(context, themeResId);
		this.mContext=context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_ringtone); //直接写死了，因为这里特殊用途
		findViewById(R.id.ringtone_cancle_button).setOnClickListener(this);
		ListView mListView=(ListView)findViewById(R.id.rington_dialog_listview);
		adapter=new FileAdapter(mContext);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);
		isScaning=false;
		currentDir="/";
		startScanDir(currentDir);  //一开始就需要加载文件目录
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		FileItem item=adapter.getFileItem(position);
		if(item.fileType==FileItem.TYPE_DIR){  //如果是文件夹，进到下一级目录
			String dir=currentDir+item.fileName+"/";
			startScanDir(dir);
		}else if(item.fileType==FileItem.TYPE_FILE){  //弹出对话框询问用户是否需要设置此文件为铃声
			
			confirmDialog(currentDir+item.fileName,mRingType);
		}else{ //返回上一级目录
			String dir=currentDir.substring(0, currentDir.lastIndexOf('/'));  //指挥去掉最后一个/
			dir=dir.substring(0, dir.lastIndexOf('/')+1);  //再去掉上一层目录名，加1是不要把上一层的/去掉
			startScanDir(dir);
		}
	}
	
	/**
	 * 弹出确认对话框，如果用户选择确定，则添加铃声
	 * @param ringtonePath
	 * @param ringtoneType
	 * @date 2016年4月11日 下午10:14:12
	 */
	private void confirmDialog(final String ringtonePath,final int ringtoneType){
		String title="添加铃声文件";
		switch(ringtoneType){
		case RingtoneManager.TYPE_RINGTONE:
			title="添加手机铃声文件";
			break;
		case RingtoneManager.TYPE_NOTIFICATION:
			title="添加提示音铃声文件";
			break;
		case RingtoneManager.TYPE_ALARM:
			title="添加闹铃铃声文件";
			break;
		default: 
			if(mSelectCallback!=null){
				mSelectCallback.onSelectResult(OnRingtoneSelect.RESULT_UNKNOWN, ringtoneType, null);
			}
			return;
		}
		new AlertDialog.Builder(mContext).setTitle(title) 
        .setIcon(R.drawable.dialog_icon) 
        .setMessage("铃声文件路径："+ringtonePath)
        .setPositiveButton("取消", new OnClickListener() {
            @Override 
            public void onClick(DialogInterface dialog, int which) { 
            // 点击“取消”后的操作 
            } 
        }) 
        .setNegativeButton("确定", new OnClickListener() {
            @Override 
            public void onClick(DialogInterface dialog, int which) { 
            	boolean[] ringtoneSet={false,false,false,false};
            	switch(ringtoneType){
        		case RingtoneManager.TYPE_RINGTONE:
        			ringtoneSet[0]=true;
        			break;
        		case RingtoneManager.TYPE_NOTIFICATION:
        			ringtoneSet[1]=true;
        			break;
        		case RingtoneManager.TYPE_ALARM:
        			ringtoneSet[2]=true;
        			break;
            	}
            	insertRingtone(ringtonePath, ringtoneType,ringtoneSet);
            } 
        }).show(); 
	}
	
	/**
	 * 正在实现插入铃声
	 * @param ringtonePath
	 * @param ringtoneType
	 * @date 2016年4月11日 下午10:29:29
	 */
	private void insertRingtone(final String ringtonePath,final int ringtoneType,final boolean[] ringtoneSet){
		try{
			  File ringtoneFile = new File(ringtonePath);
			  ContentValues values = new ContentValues();  
		      values.put(MediaStore.MediaColumns.DATA, ringtoneFile.getAbsolutePath());  
		      values.put(MediaStore.MediaColumns.TITLE, ringtoneFile.getName());  
		      values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");    
		      values.put(MediaStore.Audio.Media.IS_RINGTONE, ringtoneSet[0]);  
		      values.put(MediaStore.Audio.Media.IS_NOTIFICATION, ringtoneSet[1]);  
		      values.put(MediaStore.Audio.Media.IS_ALARM, ringtoneSet[2]);  
		      values.put(MediaStore.Audio.Media.IS_MUSIC, ringtoneSet[3]);
		      Uri uri = MediaStore.Audio.Media.getContentUriForPath(ringtoneFile.getAbsolutePath());  
		      Uri newUri = mContext.getContentResolver().insert(uri, values); 
		      
		      if(mSelectCallback!=null){
					mSelectCallback.onSelectResult(OnRingtoneSelect.RESULT_SUCCESS, ringtoneType, newUri);
				}
		}catch(Throwable error){
			if(mSelectCallback!=null){
				mSelectCallback.onSelectResult(OnRingtoneSelect.RESULT_EXCEPTION, ringtoneType, null);
			}
		}
	}
	/**
	 * 
	 * @param parentDir
	 * @date 2016年4月10日 下午10:57:53
	 */
	private void startScanDir(String parentDir){
		if(isScaning){
			return ;
		}
		isScaning=true;
		new FileScanner().execute(parentDir);
		currentDir=parentDir; //当前已经切换到这个文件目录下了
	}
	/**
	 * 点击取消的时候调用
	 */
	@Override
	public void onClick(View v) {
		if(this.isShowing())
		   this.dismiss();
		if(mSelectCallback!=null){
			mSelectCallback.onSelectResult(OnRingtoneSelect.RESULT_CANCLE, mRingType, null);
		}
	}
	/**
	 * 弹出选择对话框
	 * @param ringType
	 * @date 2016年4月10日 下午5:24:40
	 */
	public void showSelectDialog(int ringType){
		this.mRingType=ringType;
		this.show();
	}
	/**
	 * 添加回调函数
	 * @param callback
	 * @date 2016年4月10日 下午5:20:54
	 */
	public void setOnRingtoneSelect(OnRingtoneSelect callback){
		this.mSelectCallback=callback;
	}
	
	/**
	 * 一个一步扫描文件系统目录的类
	 * @author hxiong
	 * @date 2016年4月10日 下午10:48:35
	 */
	class FileScanner extends AsyncTask<String, Integer, ArrayList<FileItem>>{

		@Override
		protected ArrayList<FileItem> doInBackground(String... params) {
			ArrayList<FileItem> list=new ArrayList<FileItem>();
			try{
				String dir=params[0];
				if(!dir.equals("/")){
					list.add(new FileItem("返回上一级目录","",3));
				}
				File folder=new File(dir);
				File[] files=folder.listFiles();
				if(files!=null){
					for(File file:files){   //遍历这个目录下的文件和文件夹
						if(file.isDirectory()){
							initDirItem(list,file);
						}else if(file.isFile()){
							initFileItem(list,file);
						}
					}
				}
				
			}catch(Throwable error){
				error.printStackTrace();
			}
			return list;
		}
		
		@Override
		protected void onPostExecute(ArrayList<FileItem> result) {
			adapter.setFileItemList(result);
			adapter.notifyDataSetChanged();
			isScaning=false;
		}
		
		/**
		 * 
		 * @param list
		 * @param dir
		 * @date 2016年4月10日 下午11:10:14
		 */
		private void initDirItem(ArrayList<FileItem> list,File dir){
			try{
				String info="共有 "+dir.listFiles().length+" 项";
				list.add(new FileItem(dir.getName(), info, FileItem.TYPE_DIR));
			}catch(Throwable error){
				error.printStackTrace();
			}
		}
		/**
		 * 
		 * @param list
		 * @param file
		 * @date 2016年4月10日 下午11:10:22
		 */
		private void initFileItem(ArrayList<FileItem> list,File file){
            try{
            	String name=file.getName();
				/////需要判断这个文件是不是音频文件，这里只是简单判断后缀名
				if(name.endsWith(".ogg")||name.endsWith(".mp3")||name.endsWith(".wav")){
	            	String info="文件大小 "+file.length()+" byte";
					list.add(new FileItem(name, info, FileItem.TYPE_FILE));
				}
			}catch(Throwable error){
				error.printStackTrace();
			}
		}
	}
	/**
	 * 当选择了一个
	 * @author hxiong
	 * @date 2016年4月10日 下午4:33:53
	 */
	public interface OnRingtoneSelect{
		
		public static final int RESULT_SUCCESS=0;  //表示选择铃声并插入成功
		
		public static final int RESULT_CANCLE=1;   //表示取消选择铃声并插入
		
		public static final int RESULT_FAIL=2;     //表示选择铃声并插入失败
		
		public static final int RESULT_UNKNOWN=3;     //表示选择铃声类型未知
		
		public static final int RESULT_EXCEPTION=4;     //表示选择铃声类型未知
		
		void onSelectResult(int ringtoneResult, int ringtoneType, Uri ringtoneUri);
	}

}
