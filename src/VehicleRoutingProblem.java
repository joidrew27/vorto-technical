import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class VehicleRoutingProblem {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Usage: java VRPSolver <input_file_path>");
            System.exit(1);
        }
        String inputFilePath = args[0];
//        String inputFilePath = "/Users/joidrew27/IdeaProjects/takehomes/vorto-technical/src/input.txt";

        // Read the content of the file
        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(inputFilePath)));
        } catch (IOException e) {
            e.printStackTrace();
            content = "";
        }
        // Print the content
        // System.out.println("Content of the file '" + inputFilePath + "':");
        // System.out.println(content);
        List<double[]> inputs = parseInput(content);
        List<List<Integer>> res = vehicleRoutingProblem(inputs);
        printResult(res);
    }

    private static List<double[]> parseInput(String content) {
        List<double[]> inputs = new ArrayList<>();
        try {
            // Split the string by newline
            String[] lines = content.split("\n");

            // Process each line
            for (int i = 0; i < lines.length; i++) {
                if (i == 0) continue; // loadNumber pickup dropoff
                String line = lines[i];
                // System.out.println(line);
                String[] loadNumber_pickup_dropoff = line.split(" ");
                String pickup = loadNumber_pickup_dropoff[1];
                pickup = pickup.substring(1, pickup.length() - 1);
                pickup.replace("(", "");
                pickup.replace(")", "");
                String dropoff = loadNumber_pickup_dropoff[2];
                dropoff = dropoff.substring(1, dropoff.length() - 2);
                dropoff.replace("(", "");
                dropoff.replace(")", "");
                double[] pickupDropoff = new double[4];
                pickupDropoff[0] = Double.parseDouble(pickup.split(",")[0]);
                pickupDropoff[1] = Double.parseDouble(pickup.split(",")[1]);
                pickupDropoff[2] = Double.parseDouble(dropoff.split(",")[0]);
                pickupDropoff[3] = Double.parseDouble(dropoff.split(",")[1]);
//                System.out.println(pickupDropoff[0] + "," + pickupDropoff[1] +"," + pickupDropoff[2] +"," + pickupDropoff[3]);
                inputs.add(pickupDropoff);
            }
        } catch (Exception e) {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
        }
        return inputs;
    }


    public static void printResult(List<List<Integer>> res) {
        for (List<Integer> list : res) {
            System.out.println(list);
        }
    }

    /**
     * load: double[4] representing loading x coord, loading y coord, dropping x cord, dropping y coord
     * input: array of loads, load[i] is the i-th load
     * <p>
     * run: int[] representing index of loads taken by the truck
     * return: run[]
     */
    public static List<List<Integer>> vehicleRoutingProblem(List<double[]> input) {
        // put all index into a set
        List<Integer> loadToDo = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            loadToDo.add(i);
        }
        List<List<Integer>> res = new ArrayList<>();
        while (!loadToDo.isEmpty()) {
            List<Integer> cur = new ArrayList<>();
            int index = loadToDo.remove(0);
            double time = findinitialTime(input.get(index)[0], input.get(index)[1], input.get(index)[2], input.get(index)[3]);
            cur.add(index);

            if (loadToDo.isEmpty()) {
                res.add(cur);
                return res;
            }

            sortOnDistance(input, loadToDo, input.get(index)[2], input.get(index)[3]);

            int nextIndex = loadToDo.get(0);
            double extraTime = findextraTime(input.get(index)[2], input.get(index)[3],
                    input.get(nextIndex)[0], input.get(nextIndex)[1], input.get(nextIndex)[2], input.get(nextIndex)[3]);

            while (time + extraTime <= 1200) {
                time += extraTime;
                cur.add(nextIndex);
                loadToDo.remove(0);
                if (loadToDo.isEmpty()) {
                    res.add(cur);
                    return res;
                }
                index = nextIndex;
                sortOnDistance(input, loadToDo, input.get(index)[2], input.get(index)[3]);
                nextIndex = loadToDo.get(0);
                extraTime = findextraTime(input.get(index)[2], input.get(index)[3],
                        input.get(nextIndex)[0], input.get(nextIndex)[1], input.get(nextIndex)[2], input.get(nextIndex)[3]);
            }
            res.add(cur);
        }
        return res;
    }

    public static double calculateEuclideanDistance(double startX, double startY, double endX, double endY) {
        return Math.sqrt(Math.pow(startX - endX, 2) + Math.pow(startY - endY, 2));
    }

    public static double findextraTime(double dropAX, double dropAY, double pickBX, double pickBY, double dropBX, double dropBY) {
        return calculateEuclideanDistance(dropAX, dropAY, pickBX, pickBY)
                + calculateEuclideanDistance(pickBX, pickBY, dropBX, dropBY)
                + calculateEuclideanDistance(0, 0, dropBX, dropBY)
                - calculateEuclideanDistance(0, 0, dropAX, dropAY);
    }

    public static double findinitialTime(double pickX, double pickY, double dropX, double dropY) {
        return calculateEuclideanDistance(0, 0, pickX, pickY)
                + calculateEuclideanDistance(pickX, pickY, dropX, dropY)
                + calculateEuclideanDistance(0, 0, dropX, dropY);
    }

    public static void sortOnDistance(List<double[]> input, List<Integer> loadToDo, double x, double y) {
        Collections.sort(loadToDo, (a, b) -> {
            double distanceA = calculateEuclideanDistance(x, y, input.get(a)[0], input.get(a)[1]);
            double distanceB = calculateEuclideanDistance(x, y, input.get(b)[0], input.get(b)[1]);
            return distanceA < distanceB ? -1 : 1;
        });
    }

}
