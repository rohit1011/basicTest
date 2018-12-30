package example.com.attempt2;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import example.com.attempt2.adapters.SongListAdapter;
import example.com.attempt2.services.MusicService;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import example.com.attempt2.adapters.SongAdapter;
import example.com.attempt2.services.MusicService;

import static java.lang.Integer.reverse;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101;//
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    private ArrayList<Song> songList;
    private MusicService musicSrv;
    private Intent playIntent;
    private Boolean musicBound=false;
    private Uri thispath;
    private RecyclerView recyclerView;
    private ImageView imageViewMain,imageViewSlide;
    private SeekBar seekBar;
    private TextView textTitleMain, textTitleSlide,textTimeStart,textTimeEnd;
    private Handler mHandler = new Handler();


    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.song_list);
        imageViewMain = findViewById(R.id.album_cover_main);
        imageViewSlide = findViewById(R.id.album_cover_slide);
        textTitleMain = findViewById(R.id.song_title_main);
        textTitleSlide = findViewById(R.id.song_title_slide);
        seekBar = findViewById(R.id.seekBar);
        setSeekBar();

        //Naviagation Button
        ImageButton buttonNext = findViewById(R.id.nextMain);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        });
        ImageButton buttonPrevious = findViewById(R.id.previousMain);
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        ImageButton buttonSeekForward = findViewById(R.id.seekforward);
        buttonSeekForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forward(getCurrentTime());
            }
        });
        ImageButton buttonseekReverse = findViewById(R.id.seekback);
        buttonseekReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverse(getCurrentTime());
            }
        });
        ImageButton buttonPausePlayMain = findViewById(R.id.pause_play_main);
        buttonPausePlayMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying()) {
                    pause();
                } else
                    start();
            }
        });


        songList = new ArrayList<>();
        checkPermission();
        sort();


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SongListAdapter musicListAdapter = new SongListAdapter(this);
        recyclerView.setAdapter(musicListAdapter);
        musicListAdapter.setArraylist(songList, MainActivity.this);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        } else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        getSongList();
    }

    public void getSongList(){
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(
                musicUri, // Uri
                null,
                null,
                null,
                null
        );
        if (musicCursor!=null&& musicCursor.moveToFirst()){
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int isMusicColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC);
            int isalbumidColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

            do {
                String thisMusic = musicCursor.getString(isMusicColumn);
                if (thisMusic.equals("1")){

                    String thisTitle = musicCursor.getString(titleColumn);
                    String thisData = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    Long thisId = musicCursor.getLong(idColumn);
                    Long thisAlbumId = musicCursor.getLong(isalbumidColumn);
                    String thisArtist = musicCursor.getString(artistColumn);
                    Cursor cursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                            new String[]{MediaStore.Audio.Albums._ID,MediaStore.Audio.Albums.ALBUM_ART},
                            MediaStore.Audio.Albums._ID+"=?",
                            new String[]{String.valueOf(thisAlbumId)},null);
                    if (cursor!=null && cursor.moveToFirst()){
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                        if (path==null){
                            thispath = Uri.parse("android.resource://"+BuildConfig.APPLICATION_ID+"drawable/ic_audiotrack_black_24dp");
                        }
                        else {
                            thispath = Uri.parse(path);
                            cursor.close();
                        }
                        Song mSong = new Song(thisId,thisTitle,thisArtist,thispath,thisData);
                        songList.add(mSong);
                    }

                }
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }

    }

    private void sort() {
        Collections.sort(songList, new Comparator<Song>() {
            @Override
            public int compare(Song song, Song t1) {
                return song.getTitle().compareTo(t1.getTitle());
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent==null){
            playIntent = new Intent(this,MusicService.class);
            bindService(playIntent,musicConnection,Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }



    public void songPicked(int i){
        musicSrv.setSong(i);
        musicSrv.playSong();
        updateUI(i);
        Log.i("RRRR","msh"+i);
    }

    private void updateUI(int i) {
        imageViewMain.setImageURI(songList.get(i).getMusic_Art());
        imageViewSlide.setImageURI(songList.get(i).getMusic_Art());
        textTitleMain.setText(songList.get(i).getTitle());
        textTitleSlide.setText(songList.get(i).getTitle());


    }

    private void forward(int currentTime) {
        if (currentTime>=(getTotalTime()-5000)){
            currentTime =getTotalTime();
        }
        else
            currentTime = currentTime+5000;
        musicSrv.seek(currentTime);
    }

    private void reverse(int CurrentTime){
        if (CurrentTime<=5000){
            CurrentTime =0;
        }
        else CurrentTime = CurrentTime-5000;
        musicSrv.seek(CurrentTime);
    }

    private int getTotalTime() {
       if( musicSrv != null && musicBound && musicSrv.isPng()){

    return musicSrv.getDur();
            }
            else return 0;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(playIntent);
        unbindService(musicConnection);
        musicSrv=null;
        super.onDestroy();
    }


    private void setSeekBar() {
        final String[] a = new String[1];
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (musicSrv!= null){
                    if (musicSrv.isPng() && musicBound){
                        int mTotalTime = musicSrv.getDur()/1000;
                        int mCurrentPosition = musicSrv.getCTime()/1000;
                        seekBar.setMax(mTotalTime);
                        String ab = String.valueOf(mTotalTime);
                        textTimeEnd.setText(ab);
                        seekBar.setProgress(mCurrentPosition);
                        a[0] = String.valueOf(mCurrentPosition);
                        textTimeStart.setText(a[0]);

                    }
                    else if (musicSrv != null && !musicSrv.isPng()){
                        musicSrv.playNext();
                        updateUI(musicSrv.getSongId());
                    }
                }
                mHandler.postDelayed(this,1000);
            }
        });
    }

    private void playPrev() {
        musicSrv.playPrev();
        updateUI(musicSrv.getSongId());
    }

    private void playNext() {
        musicSrv.playNext();
        updateUI(musicSrv.getSongId());
    }
    public int getCurrentTime() {
        if (musicSrv != null && musicBound && musicSrv.isPng()) {
            return musicSrv.getCTime();
        }
        return 0;
    }
    public boolean isPlaying() {
        if (musicSrv != null && musicBound)
            return musicSrv.isPng();
        return false;
    }
    public void pause() {
        musicSrv.pausePlayer();
    }

    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    public void start() {
        musicSrv.go();
    }


}

