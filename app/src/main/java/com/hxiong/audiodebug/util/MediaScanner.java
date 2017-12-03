package com.hxiong.audiodebug.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.hxiong.audiodebug.adapter.AudioItem;

import java.util.ArrayList;

/**
 * Created by hxiong on 2017/5/6 23:37.
 * Email 2509477698@qq.com
 */

public class MediaScanner {

      public static boolean queryAudioList(Context context, ArrayList<AudioItem> audioList){
          Log.d("MediaScanner", "TqueryAudioList start....");
          Cursor cursor = null;
          try {
              cursor = context.getContentResolver().query(
                      MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                      MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
              if(cursor == null) {
                  Log.d("MediaScanner", "The cursor count is null.");
                  return false;
              }
              int count= cursor.getCount();
              if(count <= 0) {
                  Log.d("MediaScanner", "The getMediaList cursor count is 0.");
                  return false;
              }
              AudioItem audioItem = null;
//          String[] columns = cursor.getColumnNames();
              while (cursor.moveToNext()) {
                  audioItem = new AudioItem();
                  long id = cursor.getLong(cursor
                          .getColumnIndex(MediaStore.Audio.Media._ID));   //音乐id
                  String title = cursor.getString((cursor
                          .getColumnIndex(MediaStore.Audio.Media.TITLE))); // 音乐标题
                  String artist = cursor.getString(cursor
                          .getColumnIndex(MediaStore.Audio.Media.ARTIST)); // 艺术家
                  String album = cursor.getString(cursor
                          .getColumnIndex(MediaStore.Audio.Media.ALBUM)); //专辑
                  String displayName = cursor.getString(cursor
                          .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)); //显示名称
                  long albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                  long duration = cursor.getLong(cursor
                          .getColumnIndex(MediaStore.Audio.Media.DURATION)); // 时长
                  long size = cursor.getLong(cursor
                          .getColumnIndex(MediaStore.Audio.Media.SIZE)); // 文件大小
                  String url = cursor.getString(cursor
                          .getColumnIndex(MediaStore.Audio.Media.DATA)); // 文件路径
                  int isMusic = cursor.getInt(cursor
                          .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)); // 是否为音乐
                  Log.d("MediaScanner","id="+id+" title="+title+" artist="+artist+" album="+album+" displayName="+displayName+
                          " duration="+duration+" size="+size+" url="+url);
                  audioList.add(audioItem);
              }
          } catch (Exception e) {
              Log.d("MediaScanner", e.getMessage());
          } finally {
              if(cursor != null) {
                  cursor.close();
              }
          }
          return true;
      }

    /**
     * 根据时间和大小，来判断所筛选的media 是否为音乐文件，具体规则为筛选小于30秒和1m一下的
     */
    private static boolean isMusic(int time, long size) {
        if(time <= 0 || size <= 0) {
            return  false;
        }

        time /= 1000;
        int minute = time / 60;
//  int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        if(minute <= 0 && second <= 30) {
            return  false;
        }
        if(size <= 1024 * 1024){
            return false;
        }
        return true;
    }
}
