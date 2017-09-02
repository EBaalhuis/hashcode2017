package Evaluate;

import java.util.ArrayList;

public class Video {
	
	public int id, size;
	public ArrayList<Request> reqs;
	
	Video(int _id, int _s) {
		id = _id;
		size = _s;
		reqs = new ArrayList<>();
	}
	
	public void addRequest(Request r) {
		reqs.add(r);
	}
	
}
