package com.hxiong.audiodebug.adapter;

public class DebugItem {
    
	public int iconId;
	public String moduleTitle;
	public Class<?> mClass;
	
	public DebugItem(){  }
	
	public DebugItem(int icon,String title,Class<?> cls){
		this.iconId=icon;
		this.moduleTitle=title;
		this.mClass=cls;
	}
}
