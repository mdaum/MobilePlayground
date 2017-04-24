package RandomSongList;

import java.util.ArrayList;

public class SongInfo {
	String name;
	String album;
	ArrayList<SpotifyImage>albumArt;
	ArrayList<ArtistInfo>artists;
	long duration; //in ms
	boolean explicit;
	String trackurl;
	String id;
	long popularity;
	String preview_url;
	public SongInfo(String n,String al, ArrayList<SpotifyImage>aa,ArrayList<ArtistInfo>ars,long d, boolean e, String trackurl, String id, long pop, String prev){
		this.name=n;
		this.album=al;
		this.albumArt=aa;
		this.artists=ars;
		this.duration=d;
		this.explicit=e;
		this.trackurl=trackurl;
		this.id=id;
		this.popularity=pop;
		this.preview_url=prev;
	}
	@Override
	public String toString(){
		return name+", by "+artists.get(0).name;
	}
}
