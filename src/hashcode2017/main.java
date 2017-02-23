package hashcode2017;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class main {

	// Input instances
	public static String[] instances = { "example" };

	// variables that are read from input
	public static long nRow, nCol, deadline, maxLoad, totalScore;
	public static int nType, nWarehouse, nOrders, nDrones, nCommands;
	public static long[] weight, available;

	// variables for making output
	public static BufferedWriter bw;

	public static void main(String[] args) throws IOException {
		for (String s : instances) {
			String inputDir = "/home/erik/Hashcode2016/" + s + ".in";
			String outDir = "/home/erik/Hashcode2016/" + s + ".out";
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
	}

	public static long evaluateSolution(String fileDir) {
		// Client-side evaluation of solution
		return 42;
	}
	
	public static void readInput(String fileDir) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileDir));
		String[] split;

		// Read input here

		br.close();
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