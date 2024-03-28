import bTree.BTree;
import classes.Business;
import hashMap.CustomMap;
import weightedData.WeightedData;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class Clustering {

    private static BTree b;

    private static Set<Set<String>> tracking = new HashSet<>();
    private static CustomMap<String, Business> businessHashMap = new CustomMap<>();



    public static void main(String[] args) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("src/btreeOutput/output.ser");
        ObjectInputStream objectInputStream= new ObjectInputStream(fileInputStream);
        b = (BTree) objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();

        String medoid1 = "1-z7wd860Rii4kbEMCT8DA.ser";
        String medoid2 = "DJim4p_UABTUcIKsZTxARg.ser";
        String medoid3 = "4cK4FDxVNZxGDK-TFHzw5g.ser";

        Set<String> medoidNames1 = new HashSet<>(Set.of(
                medoid1,
                medoid2,
                medoid3));

        ArrayList<String> bTreeValues = b.getValues();

        CustomMap<String, ArrayList<WeightedData>> chosen = allocatingToSelectedCluster(medoidNames1.toArray(new String[0]));

        int iteration_count = 0;

        for (String i: bTreeValues) {


            Set<String> medoidNames2 = changeOneValueFromSet(medoidNames1, bTreeValues);

            // String is the mediod names and value is the array of selected values
            CustomMap<String, ArrayList<WeightedData>> dataAllocatedToClusters2 = allocatingToSelectedCluster(medoidNames2.toArray(new String[0]));


            chosen = compareCluster(chosen, dataAllocatedToClusters2);
            System.out.println("Current chosen: " + calculateTotalCost(chosen));

            if (iteration_count >= 20) {
                break;
            }
            iteration_count++;

        }

        ArrayList<String> chosenKeys = chosen.getAllKeys();
        ArrayList<ArrayList<WeightedData>> chosenValues = chosen.getAllValues();

        for (int i = 0 ; i < chosenKeys.size(); i++) {
            System.out.println(businessHashMap.get(chosenKeys.get(i)));
            System.out.println(calculateTotalCost(chosen));
            ArrayList<WeightedData> output = chosenValues.get(i);
            output.sort(Comparator.comparingDouble(WeightedData::getValue));
            System.out.println(chosenValues.get(i));
            System.out.println("------------------------");
            System.out.println("------------------------");
            System.out.println("------------------------");
            System.out.println("------------------------");
            System.out.println("------------------------");
        }

    }

    public static Set<String> changeOneValueFromSet(Set<String> originalSet, ArrayList<String> values) {
        Random r = new Random();
        int randomIndex = r.nextInt(originalSet.size());
        int randomValue = r.nextInt(values.size());

       String[] arr =  originalSet.toArray(new String[0]);
       arr[randomIndex] = values.get(randomValue);

       if (tracking.contains( new HashSet<>(Arrays.asList(arr)))) {
           return changeOneValueFromSet(originalSet, values);
       } else {
           return new HashSet<>(Arrays.asList(arr));
       }
    }

    private static CustomMap<String, ArrayList<WeightedData>> compareCluster(
            CustomMap<String, ArrayList<WeightedData>> dataAllocatedToClusters1,
            CustomMap<String, ArrayList<WeightedData>> dataAllocatedToClusters2) {

        double cluster1cost = calculateTotalCost(dataAllocatedToClusters1);
        double cluster2cost = calculateTotalCost(dataAllocatedToClusters2);
        if (cluster1cost > cluster2cost) {
            return dataAllocatedToClusters1;
        } else if (cluster2cost > cluster1cost) {
            return dataAllocatedToClusters2;
        }
        return dataAllocatedToClusters1;
    }

    private static double calculateTotalCost(CustomMap<String, ArrayList<WeightedData>> dataAllocatedToClusters) {

        ArrayList<ArrayList<WeightedData>> a = dataAllocatedToClusters.getAllValues();
        double total = 0;
        for(ArrayList<WeightedData> b: a) {
            double sumPart = 0;
            for (WeightedData d: b) {
                sumPart += d.getValue();
            }
            total = total + sumPart;
        }

        return total;
    }

    public static CustomMap<String, ArrayList<WeightedData>> allocatingToSelectedCluster(String[] medoidNames) throws IOException, ClassNotFoundException {

        tracking.add(new HashSet<>(List.of(medoidNames)));

        CustomMap<String, Double> cluster1WeightedData =  weightedData(medoidNames[0]);
        CustomMap<String, Double> cluster2WeightedData =  weightedData(medoidNames[1]);
        CustomMap<String, Double> cluster3WeightedData =  weightedData(medoidNames[2]);


        // String - medoid one
        CustomMap<String, ArrayList<WeightedData>> clusterAssignment = new CustomMap<>();

        for (String filename: b.getValues()) {
            Double[] test = new Double[3];
            test[0] = cluster1WeightedData.get(filename);
            test[1] = cluster2WeightedData.get(filename);
            test[2] = cluster3WeightedData.get(filename);

            int maxIndex = 0;
            for (int i = 0; i < test.length; i++) {
                if (test[i] > test[maxIndex]) {
                    maxIndex = i;
                }
            }

            ArrayList<WeightedData> oldClusterAdd = clusterAssignment.get(medoidNames[maxIndex]) !=null ? clusterAssignment.get(medoidNames[maxIndex]) : new ArrayList<>();
            WeightedData wd = new WeightedData(filename,test[maxIndex]);
            oldClusterAdd.add(wd);

            clusterAssignment.add(
                    medoidNames[maxIndex],
                    oldClusterAdd
            );
        }
        return clusterAssignment;
    }


    public static CustomMap<String, Double> weightedData(String medoidFilename) throws IOException, ClassNotFoundException {

        // Pre categorizing for clustering
        CustomMap<String, String> businessReviewMap = new CustomMap<>();
        CustomMap<String, int[]> countOfEachWordMap = new CustomMap<>();
        CustomMap<String, boolean[]> containsWordMap = new CustomMap<>();
        CustomMap<String, Double> totalWeightMap = new CustomMap<>();

        // Making hashmap with all businesses
        for (String i: b.getValues()) {
            Business b1 = findFile(i);
            businessHashMap.add(i, b1);
            businessReviewMap.add(i, b1.getRv_text());
        }

        // function for calculating weight depending on medoid and one review
        String medoidReview = businessHashMap.get(medoidFilename).getRv_text();

        String[] medoidSplit = cleanString(medoidReview);

        // Note: DF means Total Number of reviews that contain that word
        int[] dfCount = new int[medoidSplit.length];


        for (String i: b.getValues()) {
            int[] countOfEachWord = new int[medoidSplit.length];
            Arrays.fill(countOfEachWord, 0);
            countOfEachWordMap.add(i, countOfEachWord);


            boolean[] containsWord = new boolean[medoidSplit.length];
            Arrays.fill(containsWord, false);
            containsWordMap.add(i, containsWord);
        }


        int count = 0;

        for(String filename: b.getValues()) {
            if (!filename.equalsIgnoreCase(medoidFilename)) {

                String[] cleanReviewData = cleanString(businessHashMap.get(filename).getRv_text());

                for (String i: cleanReviewData) {
                    for (int j = 0; j < medoidSplit.length; j++) {
                        if (medoidSplit[j].equalsIgnoreCase(i)) {
                            countOfEachWordMap.get(filename)[j]++;
                            containsWordMap.get(filename)[j] = true;
                        }
                    }
                }

                // Calculating the df values from true or false values from frequency table
                for (int i = 0; i < containsWordMap.get(filename).length; i++) {
                    if(containsWordMap.get(filename)[i]) {
                        dfCount[i]++;
                    }
                }

                count++;
                if (count % 5000 == 0) {
                    System.out.println(count);
                }


            }
        }

        CustomMap<String, Double> weightValues = new CustomMap<>();
        CustomMap<String, String> weightValuesNames = new CustomMap<>();
        for (String filename: b.getValues()) {
            totalWeightMap.add(filename, calculateWeight(countOfEachWordMap.get(filename), dfCount, businessHashMap.size()));
//            System.out.println(businessHashMap.get(filename).getName() + calculateWeight(countOfEachWordMap.get(filename), dfCount, businessHashMap.size()));

            weightValues.add(
                    businessHashMap.get(filename).getBusiness_id() + ".ser",
                    calculateWeight(countOfEachWordMap.get(filename), dfCount, businessHashMap.size()));
            weightValuesNames.add(
                    businessHashMap.get(filename).getBusiness_id() + ".ser",
                    businessHashMap.get(filename).getName()
            );

        }

        return weightValues;

    }




    public static Business findFile(String filename) throws IOException, ClassNotFoundException {

        FileInputStream fileIn = new FileInputStream("src/files/" + filename);
        ObjectInputStream in =  new ObjectInputStream(fileIn);
        Object findOutput = in.readObject();
        fileIn.close();
        in.close();

        return (Business) findOutput;

    }

    // Cleaning the user input string and outputting a String array
    private static String[] cleanString(String rawString) throws IOException {
        rawString = rawString.replaceAll("[^a-zA-Z']", " ");
        rawString = rawString.toLowerCase();

        String wordTxt = Files.readString(Paths.get("Library/eng.txt"), Charset.defaultCharset());
        String[] words = wordTxt.split("\\s");
        for (String word : words){
            word = word.toLowerCase();
            rawString = rawString.replaceAll("\\b" + word +"\\b", "");
        }
        return Arrays.stream(rawString.split("\\s+")).filter(s -> !s.isEmpty()).toArray(String[]::new);
    }

    private static double calculateWeight(int[] tfData,int[] dfData, int totalReview) {
        double total = 0;
        for (int i = 0; i < tfData.length; i++) {
            total += Math.log10(1+tfData[i])*((double) totalReview /(dfData[i]+1));
        }
        return total;
    }


}
