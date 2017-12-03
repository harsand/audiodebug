package com.hxiong.audiodebug.dialog;

import java.io.File;
import java.util.ArrayList;

import com.hxiong.audiodebug.R;
import com.hxiong.audiodebug.adapter.FileAdapter;
import com.hxiong.audiodebug.adapter.FileItem;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class RecordListDialog extends Dialog implements OnItemClickListener{

	private Context mContext;
	private String recordListDir;  //保存录音文件的目录
	private FileAdapter adapter;
	private RecordItemListener mRecordItemListener;
	/**
	 * 构造函数
	 * @param context
	 */
	public RecordListDialog(Context context){
		this(context,R.style.RecordListDialogTheme);
	}
	
	public RecordListDialog(Context context, int themeResId) {
		super(context, themeResId);
		this.mContext=context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_record_list);
		ListView mListView=(ListView)findViewById(R.id.record_dialog_listview);
		adapter=new FileAdapter(mContext);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);
	}
	
	/**
	 * 设置监听器
	 * @param l
	 * @date 2016年5月31日 下午11:37:47
	 */
	public void setRecordItemListener(RecordItemListener l){
		this.mRecordItemListener=l;
	}
	/**
	 * 显示所有录音文件
	 * @param recordDir
	 * @date 2016年5月31日 下午11:14:42
	 */
	public void showRecordList(String recordDir){
		this.recordListDir=recordDir;
		show();  //显示对话框
		ArrayList<FileItem> result=scanRecordDir(recordListDir);
		adapter.setFileItemList(result);
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * 扫描录音文件夹下的所有文件
	 * @param dir 录音文件夹
	 * @return 扫描后的文件列表
	 * @date 2016年5月31日 下午11:12:48
	 */
	private ArrayList<FileItem> scanRecordDir(String dir){
		ArrayList<FileItem> list=new ArrayList<FileItem>();
		File folder=new File(dir);
        if(!folder.exists()){
            return list;
        }
		File[] files=folder.listFiles();
		for(File file:files){
			if(file.isFile()){
				String info="文件大小 "+file.length()+" byte";
				list.add(new FileItem(file.getName(), info, FileItem.TYPE_FILE));
			}else{
				String info="共有 "+file.listFiles().length+" 项";
				list.add(new FileItem(file.getName(), info, FileItem.TYPE_DIR));
			}
		}
		return list;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String audioPath=recordListDir+"/"+adapter.getFileItem(position).fileName;
        if(mRecordItemListener!=null){
        	mRecordItemListener.onClickRecordItem(audioPath);
        }
	}
    
	public interface RecordItemListener{
		void onClickRecordItem(String audioPath);
	}
}
