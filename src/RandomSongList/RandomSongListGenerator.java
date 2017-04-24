package RandomSongList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RandomSongListGenerator {
	
	public static void main(String[]args) throws MalformedURLException, IOException, ParseException{
		ArrayList<SongInfo> list = RandSongList();
		for (SongInfo s : list) {
			System.out.println(s);
		}
	}
	public static ArrayList<SongInfo> RandSongList() throws MalformedURLException, IOException, ParseException{
		ArrayList<SongInfo>toRet = new ArrayList<SongInfo>();
		JSONParser j = new JSONParser();
		MyRandom rand = new MyRandom(false);
		int offset = rand.rand(0, 100001);
		String randChar=rand.nextString(1, 1);
		int listSize=50;//toggled by user....however you work it...
		HttpURLConnection c = (HttpURLConnection)((new URL("https://api.spotify.com/v1/search?q="+randChar+"&type=track&offset="+offset+"&limit="+listSize).openConnection()));
		c.setRequestProperty("Content-Type", "application/json");
		c.setRequestProperty("Accept", "application/json");
		c.setRequestMethod("GET");
		c.connect();
		Reader r = new BufferedReader(new InputStreamReader(c.getInputStream()));
		JSONObject json = (JSONObject)j.parse(r);
		System.out.println(json.toJSONString());
		c.disconnect();
		JSONArray items =(JSONArray) ((JSONObject)json.get("tracks")).get("items");
		for (Object o : items) {
			JSONObject track = (JSONObject)o;
			JSONObject albumJSON=(JSONObject)track.get("album");
			JSONArray artistArray=(JSONArray)track.get("artists");
			String name,album,trackurl,id,preview_url;
			ArrayList<SpotifyImage>albumArt=new ArrayList<SpotifyImage>();
			ArrayList<ArtistInfo>artists=new ArrayList<ArtistInfo>();
			long duration,popularity;
			boolean explicit;
			name=(String)track.get("name");
			album=(String)albumJSON.get("name");
			trackurl=(String)track.get("href");
			id=(String)track.get("id");
			preview_url=(String)track.get("preview_url");
			for(Object image_obj:(JSONArray)albumJSON.get("images")){
				JSONObject image=(JSONObject)image_obj;
				long h=(long)image.get("height");
				long w = (long)image.get("width");
				String url = (String)image.get("url");
				albumArt.add(new SpotifyImage(w,h,url));
			}
			for(Object artist_obj: artistArray){
				JSONObject artist = (JSONObject)artist_obj;
				String n=(String) artist.get("name");
				String url=(String) artist.get("href");
				String a_id = (String) artist.get("id");
				artists.add(new ArtistInfo(n,url,a_id));
			}
			duration=(long) track.get("duration_ms");
			popularity=(long) track.get("popularity");
			explicit=(boolean) track.get("explicit");
			toRet.add(new SongInfo(name,album,albumArt,artists,duration,explicit,trackurl,id,popularity,preview_url));
		}
		return toRet;
		
		
	}
}
