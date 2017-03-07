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
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class main3 {

	// Input instances
	// public static String[] instances = { "videos_worth_spreading", "kittens",
	// "me_at_the_zoo", "trending_today" };
	// public static String[] instances = { "" };
	public static String[] instances = { "me_at_the_zoo" };

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
	public static long[] cost;
	public static double startTime;
	public static double bestOverall = 0;

	// variables for making output
	public static BufferedWriter bw;

	public static void main(String[] args) throws IOException {
		startTime = System.nanoTime();
		for (String s : instances) {
			String inputDir = "/home/erik/git/hashcode2017/data/" + s + ".in";
			outDir = "/home/erik/git/hashcode2017/data/" + s + ".out";
			readInput(inputDir);

			// Solution method here

			// sol1(outDir);
			loadOptimal();

			// And then do it again
			// Score on zoo: 501555
			double cur = 0;
			double updated = 1;
			int count = 0;
			while (cur != updated) {
				count++;
				cur = evaluateSolution();
				repeatSol1(count);
				double res = evaluateSolution();
				System.out.printf("Result for instance is now %s: ", s);
				System.out.printf("%.0f\n", Math.floor(res));
				updated = evaluateSolution();
			}

			// Do random refills
			double best = 0;

			for (int nCachesRefill = 2; nCachesRefill < 10; nCachesRefill++) {
				for (int i = 0; i < 100000; i++) {
					if (i % 100 == 0) {
						System.out.printf("Refilling %d caches for the %dth time\n", nCachesRefill, i);
					}

					refillRandom(nCachesRefill);

					double result = evaluateSolution();
					if (i % 30 == 0) {
						System.out.println(Math.floor(result));
						loadOptimal();
					}
					if (result > best) {
						best = result;
						System.out.printf("Result for instance is now %s: ", s);
						System.out.printf("%.0f\n", Math.floor(best));
					}
				}
			}

			System.out.printf("Best found: %.0f\n", bestOverall);
			// Write output
			// writeOutput();
			bw.close();

			// Evaluate solution locally, if needed
			double result = evaluateSolution();
			System.out.printf("Final result for instance %s: ", s);
			System.out.printf("%.0f", Math.floor(result));

		}

	}

	public static void refillRandom(int nRefills) throws IOException {
		// Empty and refill random cache
		HashSet<Integer> ids = new HashSet<>();
		ArrayList<Cache> list = new ArrayList<>();
		for (int i = 0; i < nRefills; i++) {
			int randomNum = ThreadLocalRandom.current().nextInt(0, nServers);
			if (!ids.contains(randomNum)) {
				ids.add(randomNum);
				list.add(caches[randomNum]);
			}
		}

		double oldValue = evaluateSolution();

		// Save old configuration
		ArrayList<Integer>[] old = new ArrayList[nServers];
		for (Integer id : ids) {
			old[id] = new ArrayList<>();
			for (Video v : caches[id].vids) {
				old[id].add(v.id);
			}
		}

		for (Cache c : list) {
			// Empty cache
			emptyCache(c);
		}

		// Refill
		for (Cache c : list) {
			// Make list of options
			ArrayList<Option> opts = new ArrayList<>();
			long[] profit = new long[nVideos];

			for (Request r : reqs) {
				End e = ends[r.endId];
				if (e.connect[c.id]) {
					// Replace e.latC!! -> done
					long curProfit = Math.max((e.curLat[r.vidId] - e.lat[c.id]), 0) * r.nr;
					profit[r.vidId] += curProfit;
				}
			}

			for (int i = 0; i < nVideos; i++) {
				long p = profit[i];
				long cs = cost[i];
				Video v = videos[i];
				opts.add(new Option(profit[i], cost[i], videos[i]));
			}

			doKnapsack(opts, c);
		}

		// See if new config is better
		double newValue = evaluateSolution();
		if (newValue < oldValue) {
			int r = ThreadLocalRandom.current().nextInt(1, 101);
			if (r > 20) {
				for (Cache c : list) {
					emptyCache(c);
					for (int id : old[c.id]) {
						addVideo(videos[id], c);
					}
				}
			}
		}
	}

	public static void repeatSol1(int count) throws IOException {
		// Empty caches and refill them one by one
		System.out.printf("Repetition %d...\n", count);
		cacheCount = 0;
		double prev = Math.floor(evaluateSolution());
		
		for (Cache c : caches) {
			cacheCount++;
			System.out.printf("Rep %d cache %d\n", count, cacheCount);
			// Make list of options
			ArrayList<Option> opts = new ArrayList<>();
			long[] profit = new long[nVideos];

			// Empty cache
			emptyCache(c);

			for (Request r : reqs) {
				End e = ends[r.endId];
				if (e.connect[c.id]) {
					// Replace e.latC!! -> done
					long curProfit = Math.max((e.curLat[r.vidId] - e.lat[c.id]), 0) * r.nr;
					profit[r.vidId] += curProfit;
				}
			}

			for (int i = 0; i < nVideos; i++) {
				opts.add(new Option(profit[i], cost[i], videos[i]));
			}

			doKnapsack2(opts, c);

			double res = Math.floor(evaluateSolution());
			System.out.printf("Result for instance is now: ");
			System.out.printf("%.0f\n", Math.floor(res));
			
			if (res != prev) {
				break;
			}
			prev = res;
		}
	}

	public static void sol1(String fileDir) throws IOException {
		// Solution method goes here

		for (Cache c : caches) {
			cacheCount++;
			System.out.printf("Doing cache %d, time elapsed %f sec\n", cacheCount,
					(System.nanoTime() - startTime) / 1000000000);
			// Make list of options
			ArrayList<Option> opts = new ArrayList<>();
			long[] profit = new long[nVideos];

			for (Request r : reqs) {
				End e = ends[r.endId];
				if (e.connect[c.id]) {
					// Replace e.latC!! -> done
					long curProfit = Math.max((e.curLat[r.vidId] - e.lat[c.id]), 0) * r.nr;
					profit[r.vidId] += curProfit;
				}
			}

			for (int i = 0; i < nVideos; i++) {
				opts.add(new Option(profit[i], cost[i], videos[i]));
			}

			doKnapsack(opts, c);

			double res = evaluateSolution();
			System.out.printf("Result for instance is now: ");
			System.out.printf("%.0f\n", Math.floor(res));
		}
	}

	public static void doKnapsack(ArrayList<Option> opts, Cache c) {
		Option[] arr = opts.toArray(new Option[0]);
		Arrays.sort(arr);

		for (int i = 0; i < arr.length; i++) {
			if (c.space >= arr[i].v.size) {
				addVideo(arr[i].v, c);
			}
		}
	}

	public static void doKnapsack2(ArrayList<Option> opts, Cache c) {
		int nItems = opts.size();
		long[] profit = new long[nItems];
		int[] cost = new int[nItems];
		Option[] options = new Option[nItems];

		int index = 0;
		for (Option o : opts) {
			profit[index] = (long) o.profit;
			cost[index] = (int) o.cost;
			options[index] = o;
			index++;
		}

		long[] dp = new long[cap + 1];
		int[] point = new int[cap + 1];
		Arrays.fill(point, -1);
		ArrayList<Integer>[] l = new ArrayList[cap + 1];
		for (int i = 0; i < l.length; i++) {
			l[i] = new ArrayList<>();
		}

		for (int j = 0; j < nItems; j++) {
			for (int j2 = cap; j2 >= cost[j]; j2--) {
				if (dp[j2 - cost[j]] + profit[j] > dp[j2]) {
					dp[j2] = dp[j2 - cost[j]] + profit[j];
					l[j2] = (ArrayList<Integer>) l[j2 - cost[j]].clone();
					l[j2].add(j);
				}
			}
		}

		// Write shit
		// long totalProfit = 0;
		// long totalCost = 0;
		for (int i : l[cap]) {
			addVideo(options[i].v, c);
			// totalProfit += options[i].profit;
			// totalCost += options[i].cost;
		}
		// System.out.println(totalProfit);
		// System.out.println(totalCost);
		// System.out.println("stop");
	}

	public static void loadOptimal() throws IOException {
		try {
			BufferedReader br = new BufferedReader(new FileReader(outDir));
			String[] split;
			split = br.readLine().split(" ");

			int nLines = Integer.parseInt(split[0]);

			for (int i = 0; i < nLines; i++) {
				split = br.readLine().split(" ");
				int cacheId = Integer.parseInt(split[0]);
				emptyCache(caches[cacheId]);
				for (int j = 1; j < split.length; j++) {
					int videoId = Integer.parseInt(split[j]);
					addVideo(videos[videoId], caches[cacheId]);
				}
			}

			double res = evaluateSolution();
			System.out.printf("Loaded solution from file with value %.0f\n", Math.floor(res));

		} catch (NullPointerException e) {
			System.out.println("No old solution found, generating from scratch...");
			sol1(outDir);
		} catch (FileNotFoundException e) {
			System.out.println("No old solution found, generating from scratch...");
			sol1(outDir);
		}
	}

	public static double evaluateSolution() throws IOException {
		// Check if solution is allowed
		for (Cache c : caches) {
			long space = cap;
			for (Video v : c.vids) {
				space -= v.size;
			}
			// System.out.printf("Space remaining in cache %d: %d\n", c.id,
			// space);
			if (space < 0) {
				System.out.println("Invalid solution!");
				System.out.printf("Cache %d has space %d\n", c.id, space);
				System.exit(0);
			}
		}

		// Client-side evaluation of solution
		long total = 0;
		long totalReqs = 0;

		for (Request r : reqs) {
			End e = ends[r.endId];
			totalReqs += r.nr;
			long lowestLat = e.latC;
			// for (Cache c : caches) {
			// if (e.connect[c.id] && c.vids.contains(videos[r.vidId])) {
			// lowestLat = Math.min(lowestLat, e.lat[c.id]);
			// }
			// }
			lowestLat = Math.min(lowestLat, e.curLat[r.vidId]);

			long saved = (e.latC - lowestLat) * r.nr;
			total += saved;
		}

		double result = ((double) total * 1000) / (double) totalReqs;
		if (result > bestOverall) {
			System.out.printf("New overall optimum: %.0f\n", Math.floor(result));
			bestOverall = result;
			writeOutput();
		}
		return result;
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

		cost = new long[nVideos];
		for (int i = 0; i < cost.length; i++) {
			cost[i] = videos[i].size;
		}

		br.close();
	}

	public static void emptyCache(Cache c) {
		HashSet<Video> temp = new HashSet<>();

		for (Video v : c.vids) {
			temp.add(v);
		}

		for (Video v : temp) {
			removeVideo(v, c);
		}
	}

	public static void removeVideo(Video v, Cache c) {
		c.removeVideo(v);

		for (End e : ends) {
			if (e.curCache[v.id] == c.id) {
				// Was using this cache, find next closest
				int id = -1;
				int lat = e.latC;
				for (Cache c2 : caches) {
					if (e.connect[c2.id] && c2.vids.contains(v) && e.lat[c2.id] < lat) {
						lat = e.lat[c2.id];
						id = c2.id;
					}
				}
				e.curCache[v.id] = id;
				e.curLat[v.id] = Math.min(lat, e.latC);
			}
		}
	}

	public static void addVideo(Video v, Cache c) {
		c.addVideo(v);

		// Update latency on ends
		for (End e : ends) {
			if (e.connect[c.id] && e.curLat[v.id] > e.lat[c.id]) {
				e.curLat[v.id] = e.lat[c.id];
				e.curCache[v.id] = c.id;
			}
		}
	}

	public static void writeOutput() throws IOException {
		initWriter(outDir);
		bw.close();
		initWriter(outDir);

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
		String tempDir = "/home/erik/git/hashcode2017/data/temp";
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
		bw.flush();
	}

	public static void initWriter(String fileDir) throws UnsupportedEncodingException, FileNotFoundException {
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileDir), "utf-8"));
	}

}