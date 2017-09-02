package Evaluate;

public class Request {
	
	public int id, nr;
	public Video v;
	public End e;
	public long curLat;
	
	Request(int _id, Video _v, End _e, int _n, long lc) {
		id = _id;
		v = _v;
		e = _e;
		nr = _n;
		curLat = lc;
	}
}
