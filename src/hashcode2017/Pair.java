package hashcode2017;

public class Pair implements Comparable<Pair> {

	public Video v;
	public End e;
	public Cache closest;
	public int id, curLat;
	public double cst;
	public double score;
	
	Pair(Video _v, End _e, int _curLat, int _id) {
		v=  _v;
		e = _e;
		curLat = _curLat;
		id = _id;
		
		cst = (double) e.vidReqs[v.id] / (double) v.size;
		score = Double.MIN_VALUE;
	}
	
	public int compareTo(Pair o) {
		if (this.score != o.score) {
			return Double.compare(this.score, o.score);
		} else {
			return Integer.compare(this.id, o.id);
		}
	}
	
}
