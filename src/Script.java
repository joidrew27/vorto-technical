import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Script {
    private static double parseInput(String content) {
        try {
            // Split the string by newline
            String[] lines = content.split("\n");
            // Process each line
            double sum = 0;
            for (int i = 0; i < lines.length; i++) {
                sum += Double.parseDouble(lines[i]);
            }
            double average = sum / lines.length;
            return average;
        } catch (Exception e) {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
        }
        return 0;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java VRPSolver <input_file_path>");
            System.exit(1);
        }
        String inputFilePath = args[0];
        // Read the content of the file
        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(inputFilePath)));
        } catch (IOException e) {
            e.printStackTrace();
            content = "";
        }
        double average = parseInput(content);
        System.out.println(average);
    }
}