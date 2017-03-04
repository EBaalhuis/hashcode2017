package hashcode2017;

import java.util.Arrays;

public class End {
	
	public int id, latC;
	public int[] lat;
	public boolean[] connect;
	public long[] vidReqs;
	public int[] curLat;
	public int closestCacheId;
	public int[] curCache;
	
	End(int _id, int _latC, int[] _lat, int nVids) {
		id = _id;
		latC = _latC;
		lat = _lat.clone();
		connect = new boolean[lat.length];
		
		for (int i = 0; i < lat.length; i++) {
			if (lat[i] != 0) {
				connect[i] = true; 
			}
		}
		
		vidReqs = new long[nVids];
		curLat = new int[nVids];
		Arrays.fill(curLat, latC);
		curCache = new int[nVids];
		Arrays.fill(curCache, -1);
		
		int lowest = Integer.MAX_VALUE;
		int lowestInd = -1;
		for (int i = 0; i < lat.length; i++) {
			if (lat[i] > 0 && lat[i] < lowest) {
				lowest = lat[i];
				lowestInd = i;
			}
		}
		closestCacheId = lowestInd;
		
	
	}
	
	
}
