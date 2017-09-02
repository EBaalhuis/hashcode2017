package Evaluate;

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
import java.util.Comparator;
import java.util.TreeSet;

public class main_evaluate {

	// Input instances
	// { "videos_worth_spreading", "kittens", "me_at_the_zoo", "trending_today"
	// };
	public static String[] instances = { "kittens" };

	// variables that are read from input
	public static long nRow, nCol, deadline, maxLoad, totalScore;
	public static int nVideos, nEnds, nReqs, nServers, cap;
	public static long[] weight, available;
	public static Video[] videos;
	public static End[] ends;
	public static Request[] reqs;
	public static Cache[] caches;
	public static String outDir;
	public static int cacheCount = 0;
	public static long[] cost;
	public static double startTime;
	public static double bestOverall = 0;
	public static String tempDir = "C:\\Users\\Erik\\git\\hashcode2017\\data\\temp";

	// variables for making output
	public static BufferedWriter bw;

	public static void main(String[] args) throws IOException {
		for (String s : instances) {
			String inputDir = "C:\\Users\\Erik\\git\\hashcode2017\\data\\" + s + ".in";
			outDir = "C:\\Users\\Erik\\git\\hashcode2017\\src\\" + s + ".out";
			readInput(inputDir);

			// Solution method here
			solEval();
			
			// Evaluate solution locally, if needed
			double result = evaluateSolution();
			System.out.printf("Final result for instance %s: ", s);
			System.out.printf("%.0f\n", Math.floor(result));

			if (result > 1021767) {
				writeOutput();
				bw.close();
			}
			
		}

	}

	public static void solEval() {
		TreeSet<Placement> set = new TreeSet<Placement>(new Order());

		for (Cache c : caches) {
			for (Video v : videos) {
				Placement p = new Placement(v, c);
				if (p.profit != 0) {
					set.add(new Placement(v, c));
				}
			}
		}

		while (!set.isEmpty()) {
			if (set.size() % 5 == 0) {
				System.out.println(set.size());
			}
			Placement p = set.last();
			set.remove(p);

			// Check if placement fits
			if (p.c.space < p.v.size) {
				continue;
			}

			// See if value update is needed
			double first = (double) p.profit / p.v.size;
			// System.out.printf("Got placement with profit %d, size %d, value
			// %f\n", p.profit, p.v.size,
			// (double) p.profit / p.v.size);
			p.updateProfit();
			double updated = (double) p.profit / p.v.size;
			// System.out.printf("Now it's placement with profit %d, size %d,
			// value %f\n", p.profit, p.v.size,
			// (double) p.profit / p.v.size);

			if (updated != first && updated > 0) {
				// Still positive value, but it was updated so put it back
				set.add(p);
			} else if (updated > 0) {
				// Not updated, so can be placed
				doPlacement(p);
			}

		}

	}

	public static void doPlacement(Placement p) {
		p.c.addVideo(p.v);

		for (Request r : p.v.reqs) {
			r.curLat = Math.min(r.curLat, r.e.lat[p.c.id]);
		}
	}

	static class Order implements Comparator<Placement> {

		public int compare(Placement x, Placement y) {
			if ((double) x.profit / x.v.size != (double) y.profit / y.v.size) {
				return Double.compare((double) x.profit / x.v.size, (double) y.profit / y.v.size);
			} else if (x.v.id != y.v.id) {
				return Integer.compare(x.v.id, y.v.id);
			} else {
				return Integer.compare(x.c.id, y.c.id);
			}
		}

	}

	public static double evaluateSolution() {
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
			End e = r.e;
			totalReqs += r.nr;
			long lowestLat = e.latC;
			for (Cache c : caches) {
				if (e.connect[c.id] && c.vids.contains(r.v)) {
					lowestLat = Math.min(lowestLat, e.lat[c.id]);
				}
			}

			long saved = (e.latC - lowestLat) * r.nr;
			total += saved;
		}

		double result = ((double) total * 1000) / (double) totalReqs;
		return Math.floor(result);
	}

	public static void readInput(String fileDir) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileDir));
		String[] split;

		// Read input here
		split = br.readLine().split(" ");
		nVideos = Integer.parseInt(split[0]);
		nEnds = Integer.parseInt(split[1]);
		nReqs = Integer.parseInt(split[2]);
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
			reqs[j] = new Request(j, videos[vidId], ends[endId], nr, ends[endId].latC);
			reqs[j].v.addRequest(reqs[j]);
		}

		// Caches
		caches = new Cache[nServers];
		for (int j = 0; j < nServers; j++) {
			caches[j] = new Cache(j, cap);
		}

		br.close();
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