package RandomSongList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RandomSongListGenerator {
	
	public static void main(String[]args) throws MalformedURLException, IOException, ParseException{
		ArrayList<SongInfo> list = RandSongList();
		Random R = new Random();
/*		for (SongInfo s : list) {
			System.out.println(s);
			String toLog=s.genres.size()+" genres: ";//build out string to Log in while loop
            for(String str: s.genres){//append to toLog
                toLog+=str+", ";
            }
            System.out.println(toLog);
		}*/
		System.out.println(list.size());
		for (SongInfo s : list) {
			s.hot=R.nextBoolean();
		}
		
		ArrayList<SongInfo>suggeted = suggest(list,10);
		for (SongInfo s : suggeted) {
			System.out.println(s);
		}
		
	}
    private static ArrayList<SongInfo> suggest(ArrayList<SongInfo> list,int size) {
		ArrayList<SongInfo>hotSongs = new ArrayList<SongInfo>();
		ArrayList<SongInfo>toRet = new ArrayList<SongInfo>();
		//first get hot songs
		for (SongInfo s : list) {
			if(s.hot)hotSongs.add(s);
		}
		//this will hold top songs for each artist
		ArrayList<ArrayList<SongInfo>>ArtistTopSongs = new ArrayList<ArrayList<SongInfo>>();
		//now fill this up...only up to the desired size
		JSONParser jsonParser = new JSONParser();
		for (int j=0;j<Math.min(hotSongs.size(),size);j++){
			HttpURLConnection c;
			try {
				c = (HttpURLConnection) ((new URL("https://api.spotify.com/v1/artists/"+hotSongs.get(j).artists.get(0).id+"/top-tracks?country=US").openConnection()));
		        c.setRequestProperty("Content-Type", "application/json");
		        c.setRequestProperty("Accept", "application/json");
		        c.setRequestMethod("GET");
		        c.connect();
		        //Buffered Reader for the output of connection...needed for JSONParser.parse
		        Reader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
		        JSONObject response = (JSONObject) jsonParser.parse(reader);
		        //Log.d("JSON",json.toJSONString());
		        c.disconnect();
		        JSONArray tracks = (JSONArray)response.get("tracks");
		        ArrayList<SongInfo>topSongs = new ArrayList<SongInfo>();
		        for (Object o : tracks) {
		        	JSONObject track = (JSONObject) o; //need to cast here
		            JSONObject albumJSON = (JSONObject) track.get("album");
		            JSONArray artistArray = (JSONArray) track.get("artists");
		
		            //declare vars to be loaded with data and passed to the SongInfo Object
		            String name, album, trackurl, id, preview_url;
		            Set<String> genres;
		            ArrayList<SpotifyImage> albumArt = new ArrayList<SpotifyImage>();
		            ArrayList<ArtistInfo> artists = new ArrayList<ArtistInfo>();
		            long duration, popularity;
		            boolean explicit;
		
		            //start populating each tiem;
		            name = (String) track.get("name");
		            album = (String) albumJSON.get("name");
		            trackurl = (String) track.get("href");
		            id = (String) track.get("id");
		            preview_url = (String) track.get("preview_url");
		            duration = (long) track.get("duration_ms");
		            popularity = (long) track.get("popularity");
		            explicit = (boolean) track.get("explicit");
		
		            genres=new HashSet<String>();//to be loaded in artists foreach loop
		
		            //for album art objects need to iterate through each image in the JSONArray
		            for (Object image_obj : (JSONArray) albumJSON.get("images")) {
		                JSONObject image = (JSONObject) image_obj;
		                long h = (long) image.get("height");
		                long w = (long) image.get("width");
		                String url = (String) image.get("url");
		                albumArt.add(new SpotifyImage(w, h, url));
		            }
		
		            //for the artist object...need to iterate through the JSONArray
		            for (Object artist_obj : artistArray) {
		                JSONObject artist = (JSONObject) artist_obj;
		                String n = (String) artist.get("name");
		                String url = (String) artist.get("href");
		                String a_id = (String) artist.get("id");
		                artists.add(new ArtistInfo(n, url, a_id));
		            }
		            topSongs.add(new SongInfo(name, album, albumArt, artists, duration, explicit, trackurl, id, popularity, preview_url,genres));
				}
		        ArtistTopSongs.add(topSongs);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		//now iterate through the 
		int songidx=-1;
		for(int i=0;i<size;i++){
			int index = i%ArtistTopSongs.size();
			if(index==0)songidx++;
			toRet.add(ArtistTopSongs.get(index).get(songidx));
		}
		return toRet;
	}
	public static ArrayList<SongInfo> RandSongList() throws MalformedURLException, IOException, ParseException {
        ArrayList<SongInfo> toRet = new ArrayList<SongInfo>();
        JSONParser jsonParser = new JSONParser();
        MyRandom rand = new MyRandom(false);
        for(int i =0;i<5;i++){
	        int offset = rand.rand(0, 100001);
	        String randChar = rand.nextString(1, 1);
	        int listSize = 50;//toggled by user....however you work it...
	
	        //make connection to api...using randomized character and offset...limit is listSize
	        HttpURLConnection c = (HttpURLConnection) ((new URL("https://api.spotify.com/v1/search?q=" + randChar + "&type=track&offset=" + offset + "&limit=" + listSize).openConnection()));
	        c.setRequestProperty("Content-Type", "application/json");
	        c.setRequestProperty("Accept", "application/json");
	        c.setRequestMethod("GET");
	        c.connect();
	        //Buffered Reader for the output of connection...needed for JSONParser.parse
	        Reader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
	        JSONObject response = (JSONObject) jsonParser.parse(reader);
	        //Log.d("JSON",json.toJSONString());
	        c.disconnect();
	        //kill connection
	
	        //array containing each track JSON object
	        JSONArray tracks = (JSONArray) ((JSONObject) response.get("tracks")).get("items");
	
	        for (int j=0;j<tracks.size();j+=10) {   //iterate through every 10th item
	            //grab other JSONObjects and arrays we care about
	        	Object o = tracks.get(j);
	            JSONObject track = (JSONObject) o; //need to cast here
	            JSONObject albumJSON = (JSONObject) track.get("album");
	            JSONArray artistArray = (JSONArray) track.get("artists");
	
	            //declare vars to be loaded with data and passed to the SongInfo Object
	            String name, album, trackurl, id, preview_url;
	            Set<String> genres;
	            ArrayList<SpotifyImage> albumArt = new ArrayList<SpotifyImage>();
	            ArrayList<ArtistInfo> artists = new ArrayList<ArtistInfo>();
	            long duration, popularity;
	            boolean explicit;
	
	            //start populating each tiem;
	            name = (String) track.get("name");
	            album = (String) albumJSON.get("name");
	            trackurl = (String) track.get("href");
	            id = (String) track.get("id");
	            preview_url = (String) track.get("preview_url");
	            duration = (long) track.get("duration_ms");
	            popularity = (long) track.get("popularity");
	            explicit = (boolean) track.get("explicit");
	
	            genres=new HashSet<String>();//to be loaded in artists foreach loop
	
	            //for album art objects need to iterate through each image in the JSONArray
	            for (Object image_obj : (JSONArray) albumJSON.get("images")) {
	                JSONObject image = (JSONObject) image_obj;
	                long h = (long) image.get("height");
	                long w = (long) image.get("width");
	                String url = (String) image.get("url");
	                albumArt.add(new SpotifyImage(w, h, url));
	            }
	
	            //for the artist object...need to iterate through the JSONArray
	            for (Object artist_obj : artistArray) {
	                JSONObject artist = (JSONObject) artist_obj;
	                String n = (String) artist.get("name");
	                String url = (String) artist.get("href");
	                String a_id = (String) artist.get("id");
	                artists.add(new ArtistInfo(n, url, a_id));
	
	                //now ping api page for artist so we can get the genres
	/*                HttpURLConnection pingArtist = (HttpURLConnection) ((new URL((String)artist.get("href")).openConnection()));
	                pingArtist.setRequestProperty("Content-Type", "application/json");
	                pingArtist.setRequestProperty("Accept", "application/json");
	                pingArtist.setRequestMethod("GET");
	                pingArtist.connect();
	                Reader artist_reader = new BufferedReader(new InputStreamReader(pingArtist.getInputStream()));
	                JSONObject artist_response = (JSONObject) jsonParser.parse(artist_reader);
	                pingArtist.disconnect();
	                //get json array of genres
	                JSONArray genre_array= (JSONArray) artist_response.get("genres");
	                //now loop through those...adding to the HashSet
	                for (Object genre_obj:genre_array) {
	                     String genre=(String)genre_obj;//cast
	                     genres.add(genre);//add to HashSet
	                }*/
	            }
	
	            //load object into ArrayList
	            toRet.add(new SongInfo(name, album, albumArt, artists, duration, explicit, trackurl, id, popularity, preview_url,genres));
	        }
        }
        //now toRet is full of the SongInfo Objects
        return toRet;


    }
}
