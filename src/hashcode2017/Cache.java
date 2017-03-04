package hashcode2017;

import java.util.HashSet;

public class Cache {
	
	public int id, cap, space;
	public HashSet<Video> vids;
	public long[] totalReqs;
	public double[] avgLat;
		
	
	Cache(int _id, int _cap) {
		id = _id;
		cap = _cap;
		vids = new HashSet<>();
		space = cap;
	}
	
	public void addVideo(Video v) {
		vids.add(v);
		space -= v.size;
	}
	
	public void removeVideo(Video v) {
		vids.remove(v);
		space += v.size;
	}
}
