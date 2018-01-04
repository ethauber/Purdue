import java.io.*;

public class takeInput {
	public static void main(String[] args) {
		int i = 0;
		for (; i < args.length-1; i++) {
			System.out.printf(" args[%d]:%s", i, args[i]);
		}
		System.out.println();
		//file on the end
		System.out.printf("\nFile is: %s\n", args[i]);
		//open file and parse by one space and newlines
		try {
			FileInputStream fstream = new FileInputStream(args[i] + ".txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			i = 1;
			while ((strLine = br.readLine()) != null) {
				String[] tokens = strLine.split(" ");
				System.out.printf("%d:",i);
				for (int j = 0; j < tokens.length; j++) {
					System.out.printf(" %s", tokens[j]);
				}
				System.out.println("\n");
				i++;
			}
			br.close();
			in.close();
			fstream.close();
		}catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}