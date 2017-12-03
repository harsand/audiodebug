package com.hxiong.audiodebug.adapter;

import com.hxiong.audiodebug.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DebugItemAdapter extends BaseAdapter {
     
	 /**
	  * 防止为空，导致异常
	  */
	 private DebugItem[] items={};
	 
	 /**
	  * 上下文对象，用于解析布局
	  */
	 private Context mContext;
	 
	 public DebugItemAdapter(Context context,DebugItem[] debugItem){
		 this.mContext=context;
		 if(debugItem!=null) //防止为空，导致异常
			 items=debugItem;
	 }
	
	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public Object getItem(int position) {
		return items[position];
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
        	convertView=LayoutInflater.from(mContext).inflate(R.layout.activity_module_item, null);
        	holder=new ViewHolder();
        	holder.moduleIcon=(ImageView)convertView.findViewById(R.id.module_item_icon);
        	holder.muduleTitle=(TextView)convertView.findViewById(R.id.module_item_title);		
        	convertView.setTag(holder);
        }else{
        	holder=(ViewHolder)convertView.getTag();
        }
        /**
         * 修改item的内容
         */
        holder.moduleIcon.setBackgroundResource(items[position].iconId);
        holder.muduleTitle.setText(items[position].moduleTitle);
		return convertView;
	}

	/**
	 * 用于保存解析过的view，对象池用法
	 * @author hxiong
	 * @date 2016年3月26日 下午6:11:50
	 */
	class ViewHolder{
		ImageView moduleIcon;
		TextView  muduleTitle;
	}
}
