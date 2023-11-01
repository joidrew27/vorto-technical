import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class VehicleRoutingProblem {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide the path of the input file as the first argument.");
            System.exit(1);
        }
        String inputFilePath = args[0];
        boolean printResult = true;
        boolean printCost = false;
        if(args.length > 1){
            if(args[1].equals("cost")){
                printCost= true;
            }
        }
        if(args.length > 2){
            if(args[2].equals("only")){
                printResult = false;
            }
        }
        // Read the content of the file
        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(inputFilePath)));
        } catch (IOException e) {
            e.printStackTrace();
            content = "";
        }
        // Parse the input string into input data structure
        List<double[]> inputs = parseInput(content);
        // compute the result
        List<List<Integer>> res = vehicleRoutingProblem(inputs, printCost);
        if(printResult){
            printResult(res);
        }
    }

    /** print each inner list in a single line. print all int as one greater */
    public static void printResult(List<List<Integer>> res){
        for(List<Integer> list : res){
            System.out.print("[");
            for(int i = 0; i < list.size(); i ++){
                System.out.print((list.get(i) + 1));
                if(i != list.size() - 1){
                    System.out.print(", ");
                }
            }
            System.out.print("]\n");
        }
    }

    private static List<double[]> parseInput(String content) {
        List<double[]> inputs = new ArrayList<>();
        try {
            // Split the string by newline
            String[] lines = content.split("\n");
            // Process each line
            for (int i = 0; i < lines.length; i++) {
                if(i == 0) continue; // neglect first line of input file
                String line = lines[i];
                String[] loadNumber_pickup_dropoff = line.split(" ");
                String pickup = loadNumber_pickup_dropoff[1];
                pickup = pickup.substring(1, pickup.length() - 1);
                pickup.replace("(","");
                pickup.replace(")",""); 
                String dropoff = loadNumber_pickup_dropoff[2];
                dropoff = dropoff.substring(1, dropoff.length() - 2);
                dropoff.replace("(",""); 
                dropoff.replace(")",""); 
                double[] pickupDropoff = new double[4];
                pickupDropoff[0] = Double.parseDouble(pickup.split(",")[0]);
                pickupDropoff[1] = Double.parseDouble(pickup.split(",")[1]);
                pickupDropoff[2] = Double.parseDouble(dropoff.split(",")[0]);
                pickupDropoff[3] = Double.parseDouble(dropoff.split(",")[1]);
                inputs.add(pickupDropoff);
            }
        } catch (Exception e) {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
        }
        return inputs;
    }

    /** load: double[4] representing loading x coord, loading y coord, dropping x cord, dropping y coord
     * input: list of loads, load.get[i] is the i-th load
     * 
     * run: list of int representing index of loads taken by the truck
     * return: list of run
     */
    public static List<List<Integer>> vehicleRoutingProblem(List<double[]> input, boolean printCost){
        // put all index into a set
        List<Integer> loadToDo = new ArrayList<>();
        for(int i = 0; i < input.size(); i ++){
            loadToDo.add(i);
        }
        // option A: sort the index by (large to small) distance to (0, 0)
        // sortByFutherest(input, loadToDo);
        // option B: sort the index by (small to large) distance to (0, 0)
        sortOnDistance(input, loadToDo, 0, 0);
        double totalTime = 0;
        List<List<Integer>> res = new ArrayList<>();
        while(!loadToDo.isEmpty()){
            List<Integer> cur = new ArrayList<>();
            int index = loadToDo.remove(0);
            double time = findinitialTime(input.get(index)[0], input.get(index)[1], input.get(index)[2], input.get(index)[3]);
            cur.add(index);

            if(loadToDo.isEmpty()){
                res.add(cur);
                totalTime += time;
                break;
            }
            findClosestNext(input, loadToDo, input.get(index)[2], input.get(index)[3]);

            int nextIndex = loadToDo.get(0);
            double extraTime = findextraTime(input.get(index)[2], input.get(index)[3], 
            input.get(nextIndex)[0], input.get(nextIndex)[1], input.get(nextIndex)[2], input.get(nextIndex)[3]);
            
            while(time + extraTime <= 720){
                time += extraTime;
                cur.add(nextIndex);
                loadToDo.remove(0);
                if(loadToDo.isEmpty()){
                    break;
                }
                index = nextIndex;
                findClosestNext(input, loadToDo, input.get(index)[2], input.get(index)[3]);
                nextIndex = loadToDo.get(0);
                extraTime = findextraTime(input.get(index)[2], input.get(index)[3], 
                input.get(nextIndex)[0], input.get(nextIndex)[1], input.get(nextIndex)[2], input.get(nextIndex)[3]);
            }
            sortOnDistance(input, loadToDo, input.get(index)[2], input.get(index)[3]);
            // check for second closest, see if it can fit in this car
            // for better run time, use i <= 1
            for(int i = 1; i <= loadToDo.size() / 3 ; i ++){
                if(loadToDo.size() > i){
                    int skipIndex = loadToDo.get(i);
                    extraTime = findextraTime(input.get(index)[2], input.get(index)[3], 
                    input.get(skipIndex)[0], input.get(skipIndex)[1], input.get(skipIndex)[2], input.get(skipIndex)[3]);
                    if(time + extraTime <= 720){
                        time += extraTime;
                        cur.add(skipIndex);
                        loadToDo.remove(i);
                        index = skipIndex;
                    }
                }
            }
            res.add(cur);
            totalTime += time;
        }
        if(printCost){
            System.out.println((totalTime + res.size() * 500)); // cost = time + 500 * trucks
        }
        return res;
    }

    public static double calculateEuclideanDistance(double startX, double startY, double endX, double endY){
        return Math.sqrt(Math.pow(startX - endX, 2) + Math.pow(startY - endY, 2));
    }

    public static double findextraTime(double dropAX, double dropAY, double pickBX, double pickBY, double dropBX, double dropBY){
        return calculateEuclideanDistance(dropAX, dropAY, pickBX, pickBY) 
        + calculateEuclideanDistance(pickBX, pickBY, dropBX, dropBY) 
        + calculateEuclideanDistance(0, 0, dropBX, dropBY)
        - calculateEuclideanDistance(0, 0, dropAX, dropAY);
    }

    public static double findinitialTime(double pickX, double pickY, double dropX, double dropY){
        return calculateEuclideanDistance(0, 0, pickX, pickY) 
        + calculateEuclideanDistance(pickX, pickY, dropX, dropY) 
        + calculateEuclideanDistance(0, 0, dropX, dropY);
    }

    public static void sortOnDistance(List<double[]> input, List<Integer> loadToDo, double x, double y){
        Collections.sort(loadToDo, (a, b) -> {
            double distanceA = calculateEuclideanDistance(x, y, input.get(a)[0], input.get(a)[1]);
            double distanceB = calculateEuclideanDistance(x, y, input.get(b)[0], input.get(b)[1]);
            return distanceA < distanceB ? -1 : 1;
        });
    }

    //  Collections.sort(lst, (a, b)->{ return (a < b) ? 1 : -1;});
    public static void sortByFutherest(List<double[]> input, List<Integer> loadToDo){
        Collections.sort(loadToDo, (a, b) -> {
            double distanceA = calculateEuclideanDistance(0, 0, input.get(a)[0], input.get(a)[1]);
            double distanceB = calculateEuclideanDistance(0, 0, input.get(b)[0], input.get(b)[1]);
            return distanceA < distanceB ? 1 : -1;
        });
    }

    // assumption: loadToDo is not empty
    // return: load of closest distance will be put at index 0
    public static void findClosestNext(List<double[]> input, List<Integer> loadToDo, double x, double y){
        double minDistance = Double.MAX_VALUE;
        int minIndex = -1;
        for(int i = 0; i < loadToDo.size(); i ++){
            double distance = calculateEuclideanDistance(x, y, input.get(loadToDo.get(i))[0], input.get(loadToDo.get(i))[1]);
            if(distance < minDistance){
                minDistance = distance;
                minIndex = i;
            }
        }
        loadToDo.add(0, loadToDo.remove(minIndex));
    }
}