package hashcode2017;

public class End {
	
	public int id, latC;
	public int[] lat;
	public boolean[] connect;
	
	End(int _id, int _latC, int[] _lat) {
		id = _id;
		latC = _latC;
		lat = _lat.clone();
		connect = new boolean[lat.length];
		
		for (int i = 0; i < lat.length; i++) {
			if (lat[i] != 0) {
				connect[i] = true; 
			}
		}
	}
	
}
