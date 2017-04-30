package RandomSongList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SongInfo {
	String name;
	String album;
	ArrayList<SpotifyImage>albumArt;
	ArrayList<ArtistInfo>artists;
	long duration; //in ms
	boolean explicit,hot;
	String trackurl;
	String id;
	long popularity;
	String preview_url;
	Set<String>genres;
	public SongInfo(String n,String al, ArrayList<SpotifyImage>aa,ArrayList<ArtistInfo>ars,long d, boolean e, String trackurl, String id, long pop, String prev,Set<String>g){
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
		this.genres=g;
	}
	@Override
	public String toString(){
		return name+", by "+artists.get(0).name;
	}
}
