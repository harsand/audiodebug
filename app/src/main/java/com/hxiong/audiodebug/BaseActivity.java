package com.hxiong.audiodebug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * 提供左进右出和右进左出的动画效果
 * @author hxiong
 * @date 2016年3月22日 下午9:09:34
 */
public class BaseActivity extends Activity {
   
	/**
	 * 只有子类才能使用这个属性
	 */
	protected static final String DEBUG_TAG="xtc_debug_tag";

	/**
	 * 是否禁止打log
	 */
	protected static final boolean isDisableLog =false;
	/**
	 * 是否禁止打log
	 */
	protected static final int PERMISSIONS_REQUEST=1;
	
	/**
	 * 在user版本上，通过Log的方法无法打印log这时候我们需要将log保存到我们自己创建的文件中
	 */
	private static FileOutputStream fous;
	
	/**
	 * 时间的格式
	 */
	protected static final String DATE_FORMAT_PATTERN="yyyy_MM_dd-HH_mm_ss";
	
	/**
	 * log 的时间戳格式
	 */
	private static final String DATE_FORMAT_LOG="yyyy-MM-dd HH:mm:ss";
	
	/**
	 *   时间格式
	 */
	private static SimpleDateFormat logDateFormat;


	protected void onCreate(Bundle savedInstanceState) {
		//requestWindowFeature(Window.FEATURE_NO_TITLE);//以代码的形式去掉标题栏
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.right_enter, R.anim.left_exit);
	}
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.left_enter, R.anim.right_exit);
	}
	
	/**
	 * 控制打log的函数，只能在子类中使用
	 * @param log  要打印的log，字符串形式
	 * @date 2016年3月23日 下午11:30:24
	 */
	protected void printLog(String log){
		if(isDisableLog||log==null)
			return;
		if(!Build.TYPE.equals("user")){ //如果是eng版本就通过Log的方法打印
			Log.d(DEBUG_TAG, log);
		}else{  //否则输出到自定义log文件中
			printLogToFile(DEBUG_TAG, log);
		}
	}
	
	/**
	 * 输出log到文件中
	 * @param tag log的标签
	 * @param log log的内容
	 * @date 2016年4月9日 下午11:33:47
	 */
	protected static void printLogToFile(String tag,String log){
		if(!openLogFile()){
			Log.e(DEBUG_TAG, "open log file fail.");
			return ;
		}
		String logStr=getLogDate()+"\t"+tag+"\t"+log+"\n"; //文件中每一行需要显示的log
		try {
			fous.write(logStr.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 反复这样获取时间，可能会影响效率
	 * @return 
	 * @date 2016年4月9日 下午11:22:22
	 */
	@SuppressLint("SimpleDateFormat")
	private static String getLogDate(){
		if(logDateFormat==null){
			logDateFormat=new SimpleDateFormat(DATE_FORMAT_LOG);
		}
		return logDateFormat.format(new Date());
	}
	
	/**
	 * 创建一个文件，用于保存log
	 * @date 2016年4月9日 下午11:03:32
	 */
	private static boolean openLogFile(){
		//closeLogFile(); //可能需要将原来打开的文件关掉
		if(fous!=null){
			return true;
		}
		try {
			fous=new FileOutputStream(getLogFilePath());
			return true;
		} catch (FileNotFoundException e) {
			fous=null;  //出现异常需要清空
			Log.d(DEBUG_TAG, "openLogFile error:"+e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 生成一个保存log的文件名，是完整的文件路径
	 * @return 完整的文件路径
	 * @date 2016年4月9日 下午11:06:21
	 */
	@SuppressLint({ "SimpleDateFormat"})
	private static String getLogFilePath(){
		Date mDate=new Date();
		SimpleDateFormat mDateFormat=new SimpleDateFormat(DATE_FORMAT_PATTERN);
		return getLogFileDir()+"/log_"+mDateFormat.format(mDate)+".txt";   //不知道这个路径是否安全
	}
	
	/**
	 * 生成保存log文件的目录
	 * @return
	 * @date 2016年4月10日 下午2:59:51
	 */
	@SuppressLint("SdCardPath")
	private static String getLogFileDir(){
		String logDir="";
		try{
			logDir=Environment.getExternalStorageDirectory().getPath();
			logDir=logDir+"/audio_debug_log";
		}catch(Throwable error){
			logDir="/sdcard/audio_debug_log";
			error.printStackTrace();
		}
		File file=new File(logDir);
		if(!file.exists()){
			file.mkdir();
		}
		return logDir;
	}
	
	/**
	 * 这个方法应该在最后退出应用的时候调用
	 * @date 2016年4月9日 下午10:59:20
	 */
	protected static void closeLogFile(){
		if(fous==null)
			return ;
		try{
			fous.flush();  //强制输出后关闭
			fous.close();
			fous = null;   //置为null，方便GC回收？？？？
		}catch(IOException e){
			e.printStackTrace();
		}
	}

    /**
     * 检测是否已经获取相关权限
     * @param permission
     * @return  如果已经得到授权，返回true，否则返回false
     * @date   2017/1/9 23:32
      */
	protected boolean chekcAndRequestPermissions(String permission){
		if(ContextCompat.checkSelfPermission(this,permission)== PackageManager.PERMISSION_GRANTED){
			  return true;
		}
        printLog("request Permissions:"+permission);
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission)){
            printLog("show request Permissions dialog.");
        }else {
            printLog("request Permissions direct.");
            ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSIONS_REQUEST);
        }
		return false;
	}

    /**
     *  请求权限的结果回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @date   2017/1/9 23:36
      */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        printLog("onRequestPermissionsResult requestCode:"+requestCode);
        if(requestCode==PERMISSIONS_REQUEST){
            //如果获取权限成功，继续执行，否则终止当前Activity
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                printLog("permissions granted.");
            }else{
                printLog("request permissions fail.");
                finish();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
