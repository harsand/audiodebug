package com.hxiong.audiodebug;

import android.app.Application;

/**
 * Application类是为了那些需要保存全局变量设计的基本类
 * 作用 1、通过Application传递数据  2、Application数据缓存
 * @author hxiong
 * @date 2016年4月9日 下午10:37:54
 */
public class AudioDebugApplication extends Application {
    
	/**
	 * 应用创建的时候调用这个方法
	 */
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	/**
	 * 应用退出的时候可能会调用这个方法
	 */
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
}
