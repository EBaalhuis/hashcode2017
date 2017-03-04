package hashcode2017;

public class Option implements Comparable<Option> {

	public long profit, cost;
	public double fr;
	public Video v;
	
	Option(long _p, long _c, Video _v) {
		profit = _p;
		cost = _c;
		fr = (double) profit / (double) cost;
		
		v = _v;
	}

	public int compareTo(Option o) {
		return -1 * Double.compare(this.fr, o.fr);
	}
}
