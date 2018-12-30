package example.com.attempt2.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import example.com.attempt2.MainActivity;
import example.com.attempt2.R;
import example.com.attempt2.Song;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {
    private Context mContext;
    private List<Song> songLists;
    private MainActivity mainActivity;

    public SongListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setArraylist(List<Song> songLists,MainActivity mainActivity1) {
        this.songLists = songLists;
        this.mainActivity = mainActivity1;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.songs_list_recycle,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final int j = i;
      viewHolder.songName.setText(songLists.get(j).getTitle());
      viewHolder.songArtist.setText(songLists.get(j).getArtist());
      viewHolder.songName.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            mainActivity.songPicked(j);
          }
      });

    }

    @Override
    public int getItemCount() {
        if (songLists==null) {
            return 0;
        }
        else return songLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView songName,songArtist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.song_title);
            songArtist = itemView.findViewById(R.id.song_artist);
        }
    }
}
