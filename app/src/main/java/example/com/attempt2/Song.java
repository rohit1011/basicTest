package example.com.attempt2;

import android.net.Uri;

public class Song {
    private long id;
    private String title;
    private String artist;
    private Uri Music_Art;

    private String data;

    public Song(long id, String title, String artist, Uri music_Art, String data) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        Music_Art = music_Art;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Uri getMusic_Art() {
        return Music_Art;
    }

    public void setMusic_Art(Uri music_Art) {
        Music_Art = music_Art;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}