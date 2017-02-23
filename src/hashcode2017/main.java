package hashcode2017;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class main {

	// Input instances
	public static String[] instances = { "kittens", "trending_today", "me_at_the_zoo", "videos_worth_spreading" };

	// variables that are read from input
	public static long nRow, nCol, deadline, maxLoad, totalScore;
	public static int nVideos, nEnds, nReqs, nServers, cap;
	public static long[] weight, available;
	public static Video[] videos;
	public static End[] ends;
	public static Request[] reqs;
	public static Cache[] caches;
	public static String outDir; 

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
			bw.close();

			// Evaluate solution locally, if needed
			long result = evaluateSolution(outDir);
			System.err.printf("Result for instance %s: ", s);
			System.err.println(result);
		}

	}

	public static void sol1(String fileDir) throws IOException {
		// Solution method goes here

		writeOutput();
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

			ends[i] = new End(i, lC, lat);

		}

		// Requests
		reqs = new Request[nReqs];
		for (int j = 0; j < nReqs; j++) {
			split = br.readLine().split(" ");
			int vidId = Integer.parseInt(split[0]);
			int endId = Integer.parseInt(split[1]);
			int nr = Integer.parseInt(split[2]);
			reqs[j] = new Request(j, vidId, endId, nr);
		}

		// Caches
		caches = new Cache[nServers];
		for (int j = 0; j < nServers; j++) {
			caches[j] = new Cache(j, cap);
		}

		br.close();
	}

	public static void writeOutput() throws IOException {
		int nLines = 0;
		for (int i = 0; i < nServers; i++) {
			if (!caches[i].vids.isEmpty()) {
				nLines++;
				StringBuffer sb = new StringBuffer();
				for (Video v : caches[i].vids) {
					boolean flag = false;
					if (flag) {
						sb.append(" ");
					} else {
						flag = true;
					}
					sb.append(String.format("%d", v.id));
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