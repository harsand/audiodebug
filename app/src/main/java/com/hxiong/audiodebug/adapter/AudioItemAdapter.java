package com.hxiong.audiodebug.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hxiong.audiodebug.R;

import java.util.ArrayList;

/**
 * Created by hxiong on 2017/5/6 22:15.
 * Email 2509477698@qq.com
 */

public class AudioItemAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<AudioItem> mItemList;

    public AudioItemAdapter(Context context){
        this.mContext=context;
        this.mItemList=new ArrayList<AudioItem>();  //no allow null
        mItemList.add(new AudioItem());
        mItemList.add(new AudioItem());
        mItemList.add(new AudioItem());
    }


    public void setAudioItemList(ArrayList<AudioItem> list){
         if(list!=null){
             mItemList.clear();   //for gc ??
             mItemList=list;
         }
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.audio_list_item, null);
            holder=new ViewHolder();
            holder.mOrder=(TextView)convertView.findViewById(R.id.my_audio_item_order);
            holder.mName=(TextView)convertView.findViewById(R.id.my_audio_item_name);
            holder.mOther=(TextView)convertView.findViewById(R.id.my_audio_item_other);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        //AudioItem item=mItemList.get(position);
        holder.mOrder.setText("001");
        //holder.mName.setText(item.name);
        //holder.mOther.setText(item.singer+"|"+item.album);
        return convertView;
    }

    class ViewHolder{
        TextView mName;
        TextView mOrder;
        TextView mOther;
    }
}
