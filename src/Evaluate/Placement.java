package Evaluate;

public class Placement {

	public Video v;
	public Cache c;
	public long profit;
	
	Placement(Video _v, Cache _c) {
		v = _v;
		c = _c;
		updateProfit();
	}
	
	public void updateProfit() {
		// Update the profit that this placement would give
		profit = 0;
		for (Request r : v.reqs) {
			if (r.e.connect[c.id] && r.curLat > r.e.lat[c.id]) {
				long dif = r.curLat - r.e.lat[c.id];
				profit += r.nr * dif;
			}
		}
	}
}
