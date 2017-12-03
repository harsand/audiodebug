package com.hxiong.audiodebug.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.hxiong.audiodebug.R;
import com.hxiong.audiodebug.dialog.RecordListDialog;
import com.hxiong.audiodebug.dialog.RecordListDialog.RecordItemListener;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MediaRecorderActivity extends BaseModuleActivity {

	static final int HANDLER_START_RECORD = 1;  //开始录像
	static final int HANDLER_STOP_RECORD = 2;   //停止录像
	static final long RECORD_TIME_DELAY = 1000;  //每隔一秒钟，发一次消息

	private TextView recordTime;
	private ImageView recordButton;
	private TextView recordText;

	private RecordListDialog mRecordList;
	private MediaRecorder mMediaRecorder;

	//////控制变量
	private boolean isRecording;
	private boolean isAvailable;
	private String outputDir;

	//handler 对象
	private RecordHandler recordHandler;
	private int recordSecTime;
	private int recordMinTime;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_mediarecorder);

		initModuleTitle("MediaRecorder录音");

		init();

	}

	/**
	 * 初始化控件
	 *
	 * @date 2016年5月29日 下午6:20:28
	 */
	@SuppressLint("HandlerLeak")
	private void init() {
		recordTime = (TextView) findViewById(R.id.record_time_view);
		recordButton = (ImageView) findViewById(R.id.image_view_record);
		recordText = (TextView) findViewById(R.id.text_view_record);

		isRecording = false;
		isAvailable = true;
		outputDir = getMediaRecorderDir();
		recordHandler = new RecordHandler();
	}

	/**
	 * 点击了设置按钮
	 *
	 * @param v
	 * @date 2016年5月29日 下午6:14:45
	 */
	public void onClickSetting(View v) {

	}

	public void onClickRecord(View v) {
		if (isAvailable) {  //防止频繁点击导致错误
			isAvailable = false;
			if (isRecording) {
				stopRecord();
			} else {
				startRecord();
			}
		} else {
			printLog("onClickRecord is unavailable.");
		}
	}

	public void onClickList(View v) {
		if (mRecordList == null) {
			mRecordList = new RecordListDialog(this);
			mRecordList.setRecordItemListener(new RecordItemListener() {
				@Override
				public void onClickRecordItem(String audioPath) {
					try {
						printLog("onClickRecordItem audio path:" + audioPath);
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(Uri.parse(audioPath), "audio/mp3");
						startActivity(intent);
					} catch (Throwable error) {
						printLog("onClickRecordItem error:" + error.getMessage());
					}
				}
			});
		}
		mRecordList.showRecordList(outputDir);  //显示录音列表
	}

	/**
	 * 开始录音
	 *
	 * @date 2016年5月30日 下午11:15:33
	 */
	private void startRecord() {
		try {
			initMediaRecorder();
			mMediaRecorder.prepare();
			mMediaRecorder.start();
		} catch (IllegalStateException e) {
			printLog("startRecord exception:" + e.getMessage());
		} catch (Exception e) {
			printLog("startRecord exception:" + e.getMessage());
		}
		setRecordStatus(R.drawable.status_stop, R.string.record_status_stop_text);
		recordSecTime = 0;
		recordMinTime = 0;
		recordHandler.sendEmptyMessage(HANDLER_START_RECORD);
		isRecording = true;
		isAvailable = true;
	}

	/**
	 * 停止录音
	 *
	 * @date 2016年5月30日 下午11:15:45
	 */
	private void stopRecord() {
		try {
			mMediaRecorder.stop();
			mMediaRecorder.release();
		} catch (Exception e) {
			printLog("stopRecord exception:" + e.getMessage());
		}
		setRecordStatus(R.drawable.status_start, R.string.record_status_start_text);
		recordHandler.sendEmptyMessage(HANDLER_STOP_RECORD);
		isRecording = false;
		isAvailable = true;
	}

	/**
	 * 初始化相关设置
	 *
	 * @date 2016年5月30日 下午11:57:10
	 */
	private void initMediaRecorder() {
		mMediaRecorder = new MediaRecorder();
		// 设置MediaRecorder的音频源为麦克风
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// 设置MediaRecorder录制的音频格式
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		// 设置MediaRecorder录制音频的编码为acc.
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		// 设置录制好的音频文件保存路径
		mMediaRecorder.setOutputFile(getOutputPath());
	}

	/**
	 * @param imageId
	 * @param strId
	 * @date 2016年5月31日 上午12:05:59
	 */
	@SuppressWarnings("deprecation")
	private void setRecordStatus(int imageId, int strId) {
		BitmapDrawable bDrawable = (BitmapDrawable) getResources().getDrawable(imageId);
		recordButton.setImageBitmap(bDrawable.getBitmap());
		recordText.setText(getResources().getString(strId));
	}

	/**
	 * 生成音频文件的路径
	 *
	 * @return
	 * @date 2016年5月30日 下午11:52:56
	 */
	@SuppressLint("SimpleDateFormat")
	private String getOutputPath() {
		SimpleDateFormat mDateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
		String outputFile = outputDir + "/" + mDateFormat.format(new Date()) + ".3gpp";
		return outputFile;
	}



	@Override
	public void onStart() {
		super.onStart();


	}

	@Override
	public void onStop() {
		super.onStop();


	}

	@SuppressLint("HandlerLeak")
	class RecordHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case HANDLER_START_RECORD:
					handleStartRecord();
					break;
				case HANDLER_STOP_RECORD:
					handleStopRecord();
					break;
				default:
					super.handleMessage(msg);
					break;
			}
		}
	}

	/**
	 * 处理启动录音的消息
	 *
	 * @date 2016年6月6日 下午8:19:11
	 */
	private void handleStartRecord() {
		recordSecTime++;     //秒钟
		if (recordSecTime > 59) {
			recordMinTime++; //分钟
			recordSecTime = 0; //重新为0
		}
		String time = "";
		if (recordMinTime < 10) {  //不够十秒的时候
			time = time + "0";
		}
		time = time + recordMinTime + ":";
		if (recordSecTime < 10) {  //不够十分钟的时候
			time = time + "0";
		}
		time = time + recordSecTime;
		recordTime.setText(time);
		recordHandler.sendEmptyMessageDelayed(HANDLER_START_RECORD, RECORD_TIME_DELAY);
	}

	/**
	 * 处理停止录音消息
	 *
	 * @date 2016年6月6日 下午8:19:54
	 */
	private void handleStopRecord() {
		recordHandler.removeMessages(HANDLER_START_RECORD);
	}

	@Override
	protected void onDestroy() {
		try {
			if (isRecording) {
				mMediaRecorder.stop();
			}
			if (mMediaRecorder != null) {
				mMediaRecorder.release();
			}
		} catch (Exception e) {
			printLog("stopRecord exception:" + e.getMessage());
		}
		super.onDestroy();
	}
}
