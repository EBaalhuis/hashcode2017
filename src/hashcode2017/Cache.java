package hashcode2017;

import java.util.ArrayList;

public class Cache {
	
	public int id, cap, space;
	public ArrayList<Video> vids;
	public long[] totalReqs;
	public double[] avgLat;
	
	
	
	Cache(int _id, int _cap) {
		id = _id;
		cap = _cap;
		vids = new ArrayList<>();
		space = cap;
	}
	
	public void addVideo(Video v) {
		vids.add(v);
		space -= v.size;
	}
}
