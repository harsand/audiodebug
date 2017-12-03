package com.hxiong.audiodebug.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by hxiong on 2017/4/26 22:26.
 * Email 2509477698@qq.com
 */

public class PlayerPagerAdapter extends PagerAdapter {

    private ArrayList<View> mViews;


    public PlayerPagerAdapter(ArrayList<View> views){
        mViews=views;
        if(mViews==null) // new to create instance
            mViews=new ArrayList<View>();
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //return super.instantiateItem(container, position);
        ((ViewPager)container).addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        ((ViewPager)container).removeView(mViews.get(position));
    }
}
