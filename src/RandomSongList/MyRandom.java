package RandomSongList;

import java.util.*;

public class MyRandom {

  private Random rn;

  public MyRandom(boolean Seeded){
	  rn=new Random();
	  if(Seeded)rn.setSeed(100);
  }

  public  int rand(int lo, int hi) {
     int n = hi - lo + 1;
     int i = rn.nextInt() % n;
     if (i < 0) i = -i;
     return lo + i;
  }

  @SuppressWarnings("deprecation")
public  String nextString(int lo, int hi) {
     int n = rand(lo, hi);
     byte b[] = new byte[n];
     for (int i = 0; i < n; i++)
     b[i] = (byte)rand('a', 'z');
     return new String(b, 0);
  }

  public String nextString() {
     return nextString(5, 25);
  }
  
}