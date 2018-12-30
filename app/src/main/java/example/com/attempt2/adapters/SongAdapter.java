package example.com.attempt2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import example.com.attempt2.R;
import example.com.attempt2.Song;

public class SongAdapter extends BaseAdapter {
    private ArrayList<Song> songs;
    private LayoutInflater SongInf;
    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LinearLayout songLay = (LinearLayout) SongInf.inflate(R.layout.song, viewGroup,false);
        TextView songView = songLay.findViewById(R.id.song_title);
        TextView artistView = songLay.findViewById(R.id.song_artist);
        Song currSong = songs.get(i);
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        songLay.setTag(i);
        return songLay;
    }
    public SongAdapter(Context c,ArrayList<Song> theSongs){
        songs = theSongs;
        SongInf = LayoutInflater.from(c);
        notifyDataSetChanged();
    }
}
