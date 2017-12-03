package com.hxiong.audiodebug.adapter;

import java.util.ArrayList;

import com.hxiong.audiodebug.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FileAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<FileItem> mItemList;
	
	public FileAdapter(Context context){
		this.mContext=context;
		mItemList=new ArrayList<FileItem>();//初始化，防止为空
	}
	
	/**
	 * 
	 * @param fileList
	 * @date 2016年4月10日 下午10:40:44
	 */
	public void setFileItemList(ArrayList<FileItem> fileList){
		if(fileList==null)
			return ;
		mItemList.clear();   //先清空
		mItemList=fileList;
	}
	
	public FileItem getFileItem(int which){
		return mItemList.get(which);
	}
	
	@Override
	public int getCount() {
		return mItemList.size();
	}

	@Override
	public Object getItem(int position) {
		return mItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			convertView=LayoutInflater.from(mContext).inflate(R.layout.dialog_file_item, null);
			holder=new ViewHolder();
			holder.fileTypeIcon=(TextView)convertView.findViewById(R.id.file_type_icon);
			holder.fileNameText=(TextView)convertView.findViewById(R.id.file_item_name);
			holder.fileInfoText=(TextView)convertView.findViewById(R.id.file_item_info);	
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		FileItem fileItem=mItemList.get(position);
		if(fileItem.fileType==FileItem.TYPE_DIR){
			holder.fileTypeIcon.setBackgroundResource(R.drawable.folder_icon);
		}else if(fileItem.fileType==FileItem.TYPE_FILE){
			holder.fileTypeIcon.setBackgroundResource(R.drawable.ringtone_icon);
		}else{  //返回上一级的一个item吧，图标暂时没有
			holder.fileTypeIcon.setBackgroundResource(R.drawable.ic_launcher);
		}
		holder.fileNameText.setText(fileItem.fileName);
		holder.fileInfoText.setText(fileItem.fileInfo);
		return convertView;
	}
    
	class ViewHolder{
		TextView fileTypeIcon;
		TextView  fileNameText;
		TextView  fileInfoText;
	}
}
