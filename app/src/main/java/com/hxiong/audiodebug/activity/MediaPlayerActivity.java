package com.hxiong.audiodebug.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hxiong.audiodebug.R;
import com.hxiong.audiodebug.adapter.AudioItem;
import com.hxiong.audiodebug.adapter.AudioItemAdapter;
import com.hxiong.audiodebug.adapter.PlayerPagerAdapter;
import com.hxiong.audiodebug.util.MediaScanner;
import com.hxiong.audiodebug.widget.PagerTagLayout;

import java.util.ArrayList;

public class MediaPlayerActivity extends BaseModuleActivity implements AdapterView.OnItemClickListener{

    private PagerTagLayout mPagerTagLayout;
    private AudioItemAdapter mAudioItemAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_mediaplayer);
		initModuleTitle("播放音频");
        init();

	}

    private void init(){
        mPagerTagLayout=(PagerTagLayout)findViewById(R.id.player_switch_layout);
        ViewPager mViewPager=(ViewPager)findViewById(R.id.player_view_pager);

        ArrayList<View> views=new ArrayList<View>();

        View audioList=getLayoutInflater().inflate(R.layout.viewpager_audio_list,null);
        View audioDir=getLayoutInflater().inflate(R.layout.viewpager_audio_dir,null);
        views.add(audioList);
        views.add(audioDir);
        PlayerPagerAdapter adapter=new PlayerPagerAdapter(views);
        mViewPager.setAdapter(adapter);
        mPagerTagLayout.setViewPager(mViewPager);

        ListView mAudioList=(ListView)audioList.findViewById(R.id.my_audio_list);
        mAudioItemAdapter=new AudioItemAdapter(this);
        mAudioList.setAdapter(mAudioItemAdapter);
        mAudioList.setOnItemClickListener(this);
        AudioScannerTask task=new AudioScannerTask();
        task.execute("audio");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    class AudioScannerTask extends AsyncTask<String, Integer, ArrayList<AudioItem>>{

        @Override
        protected ArrayList<AudioItem> doInBackground(String... params) {
            ArrayList<AudioItem> retult=new ArrayList<AudioItem>();
            MediaScanner.queryAudioList(MediaPlayerActivity.this,retult);
            return retult;
        }

        @Override
        protected void onPostExecute(ArrayList<AudioItem> audioItems) {
            mAudioItemAdapter.setAudioItemList(audioItems);
            mAudioItemAdapter.notifyDataSetChanged();
        }
    }
}
