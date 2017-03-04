package hashcode2017;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;

public class main2 {

	// Input instances
	public static String[] instances = {  "videos_worth_spreading" };
//	 public static String[] instances = { "me_at_the_zoo" };

	// variables that are read from input
	public static long nRow, nCol, deadline, maxLoad, totalScore;
	public static int nVideos, nEnds, nReqs, nServers, cap;
	public static long[] weight, available;
	public static Video[] videos;
	public static End[] ends;
	public static Request[] reqs;
	public static Cache[] caches;
	public static String outDir;
	public static Pair[][] pairs;
	public static int cacheCount = 0;

	// variables for making output
	public static BufferedWriter bw;

	public static void main(String[] args) throws IOException {
		for (String s : instances) {
			String inputDir = "/home/erik/git/hashcode2017/data/" + s + ".in";
			outDir = "/home/erik/git/hashcode2017/data/" + s + ".out";
			readInput(inputDir);
			initWriter(outDir);

			// Solution method here
			sol1(outDir);

			// Write output
			writeOutput();
			bw.close();

			// Evaluate solution locally, if needed
			long result = evaluateSolution(outDir);
			System.err.printf("Result for instance %s: ", s);
			System.err.println(result);
		}

	}

	public static void sol1(String fileDir) throws IOException {
		// Solution method goes here

		long[] cost = new long[nVideos];
		for (int i = 0; i < cost.length; i++) {
			cost[i] = videos[i].size;
		}

		for (Cache c : caches) {
			cacheCount++;
			System.out.println(cacheCount);
			// Make list of options
			TreeSet<Option> opts = new TreeSet<>();
			long[] profit = new long[nVideos];

			for (Request r : reqs) {
				End e = ends[r.endId];
				if (e.connect[c.id]) {
					// Replace e.latC!!
					long curProfit = (e.latC - e.lat[c.id]) * r.nr;
					profit[r.vidId] += curProfit;
				}
			}

			for (int i = 0; i < nVideos; i++) {
				opts.add(new Option(profit[i], cost[i], videos[i]));
			}

			doKnapsack(opts, c);

		}

	}

	public static void doKnapsack(TreeSet<Option> opts, Cache c) {
		int nItems = opts.size();
		int[] profit = new int[nItems];
		int[] cost = new int[nItems];
		Option[] options = new Option[nItems];

		int index = 0;
		for (Option o : opts) {
			profit[index] = (int) o.profit;
			cost[index] = (int) o.cost;
			options[index] = o;
			index++;
		}

		int[] dp = new int[cap + 1];
		int[] point = new int[cap + 1];
		Arrays.fill(point, -1);
		ArrayList<Integer>[] l = new ArrayList[cap+1];
		for (int i = 0; i < l.length; i++) {
			l[i] = new ArrayList<>();
		}
		
		for (int j = 0; j < nItems; j++) {
			
			for (int j2 = cap; j2 >= cost[j]; j2--) {
				if (dp[j2 - cost[j]] + profit[j] > dp[j2]) {
					dp[j2] = dp[j2 - cost[j]] + profit[j];
					l[j2] = (ArrayList<Integer>) l[j2-cost[j]].clone();
					l[j2].add(j);
				}
			}
		}

		// Write shit
		HashSet<Integer> used = new HashSet<Integer>();

		for (int i : l[cap]) {
			addVideo(options[i].v, c);
		}
		
		

		//
		// for (Option o : opts) {
		// // Put video in cache
		// if (c.space >= o.v.size && !used.contains(o.v.id)) {
		// used.add(o.v.id);
		// addVideo(videos[o.v.id], c);
		// }
		// }
	}

	public static long evaluateSolution(String fileDir) {
		// Client-side evaluation of solution
		return 42;
	}

	public static void readInput(String fileDir) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileDir));
		String[] split;

		// Read input here
		split = br.readLine().split(" ");
		nVideos = Integer.parseInt(split[0]);
		nEnds = Integer.parseInt(split[1]);
		nReqs = Integer.parseInt(split[2]);
		;
		nServers = Integer.parseInt(split[3]);
		cap = Integer.parseInt(split[4]);

		// Second line
		split = br.readLine().split(" ");
		videos = new Video[nVideos];
		for (int i = 0; i < nVideos; i++) {
			videos[i] = new Video(i, Integer.parseInt(split[i]));
		}

		// Endpoints
		ends = new End[nEnds];
		for (int i = 0; i < nEnds; i++) {
			split = br.readLine().split(" ");
			int lC = Integer.parseInt(split[0]);
			int k = Integer.parseInt(split[1]);
			int[] lat = new int[nServers];

			for (int j = 0; j < k; j++) {
				split = br.readLine().split(" ");
				int id = Integer.parseInt(split[0]);
				int curLat = Integer.parseInt(split[1]);
				lat[id] = curLat;
			}

			ends[i] = new End(i, lC, lat, nVideos);

		}

		// Requests
		reqs = new Request[nReqs];
		for (int j = 0; j < nReqs; j++) {
			split = br.readLine().split(" ");
			int vidId = Integer.parseInt(split[0]);
			int endId = Integer.parseInt(split[1]);
			int nr = Integer.parseInt(split[2]);
			reqs[j] = new Request(j, vidId, endId, nr);
			ends[endId].vidReqs[vidId] += nr;
		}

		// Caches
		caches = new Cache[nServers];
		for (int j = 0; j < nServers; j++) {
			caches[j] = new Cache(j, cap);
		}

		// Pairs
		pairs = new Pair[nVideos][nEnds];

		br.close();
	}

	public static void addVideo(Video v, Cache c) {
		c.addVideo(v);
		// Update ends!
	}

	public static void writeOutput() throws IOException {
		int nLines = 0;
		for (int i = 0; i < nServers; i++) {
			if (!caches[i].vids.isEmpty()) {
				nLines++;
				StringBuffer sb = new StringBuffer();
				sb.append(i);
				for (Video v : caches[i].vids) {
					sb.append(String.format(" %d", v.id));
				}

				writeLine(sb.toString());
			}
		}

		filePrepend(String.format("%d\n", nLines), outDir);
	}

	public static void writeLine(String s) {
		try {
			bw.write(s);
			bw.write("\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void filePrepend(String line, String fileDir) throws IOException {
		bw.flush();
		String tempDir = "/home/erik/Hashcode2016/temp";
		Files.copy(Paths.get(fileDir), Paths.get(tempDir), StandardCopyOption.REPLACE_EXISTING);
		initWriter(fileDir);
		bw.write(line);
		BufferedReader br = new BufferedReader(new FileReader(tempDir));
		String tempLine = "";
		while ((tempLine = br.readLine()) != null) {
			bw.write(tempLine);
			bw.write("\n");
		}
		br.close();
		bw.close();
	}

	public static void initWriter(String fileDir) throws UnsupportedEncodingException, FileNotFoundException {
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileDir), "utf-8"));
	}

}