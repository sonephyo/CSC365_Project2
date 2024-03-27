import bTree.BTree;
import classes.Business;
import hashMap.CustomMap;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Clustering {

    private static BTree b;




    public static void main(String[] args) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("src/btreeOutput/output.ser");
        ObjectInputStream objectInputStream= new ObjectInputStream(fileInputStream);
        b = (BTree) objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();

        String[] mediodNames = {"1-z7wd860Rii4kbEMCT8DA.ser","2-XK9zDgSKqOwSyyMwgjzA.ser", "4cK4FDxVNZxGDK-TFHzw5g.ser"};


        String nonmediod = "2-XK9zDgSKqOwSyyMwgjzA.ser";

        CustomMap<String, Double> cluster1WeightedData =  weightedData(mediodNames[0]);
        CustomMap<String, Double> cluster2WeightedData =  weightedData(mediodNames[1]);
        CustomMap<String, Double> cluster3WeightedData =  weightedData(mediodNames[2]);

        // String - mediod one
        CustomMap<String, ArrayList<Double>> clusterAssignment = new CustomMap<>();

        Double[] test = new Double[3];
        test[0] = cluster1WeightedData.get(nonmediod);
        test[1] = cluster2WeightedData.get(nonmediod);
        test[2] = cluster3WeightedData.get(nonmediod);

        int maxIndex = 0;
        for (int i = 0; i < test.length; i++) {
            if (test[i] > test[maxIndex]) {
                maxIndex = i;
            }
        }

        ArrayList<Double> oldCluster =
                clusterAssignment.get(mediodNames[maxIndex]);
        oldCluster.add(test[maxIndex]);

        clusterAssignment.add(
                mediodNames[maxIndex],
                oldCluster
                );


    }

    public static void allocatingToSelectedCluster(String nonmediod, String[] mediodNames) {

    }



    public static CustomMap<String, Double> weightedData(String medoidFilename) throws IOException, ClassNotFoundException {

        // Pre categorizing for clustering
        CustomMap<String, Business> businessHashMap = new CustomMap<>();
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


        b.traverse();

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
                if (count % 100 == 0) {
                    System.out.println(count);
                }


            }
        }

        int i = 0;

        CustomMap<String, Double> weightValues = new CustomMap<>();
        for (String filename: b.getValues()) {
            totalWeightMap.add(filename, calculateWeight(countOfEachWordMap.get(filename), dfCount, businessHashMap.size()));
//            System.out.println(businessHashMap.get(filename).getName() + calculateWeight(countOfEachWordMap.get(filename), dfCount, businessHashMap.size()));

            weightValues.add(
                    businessHashMap.get(filename).getBusiness_id() + ".ser",
                    calculateWeight(countOfEachWordMap.get(filename), dfCount, businessHashMap.size()));

        }

        return weightValues;

//
//        int n = weights.length;
//        for (int k = 0; k < n; k++) {
//            double key = weights[k];
//            String value = businessNames[k];
//            int j = k - 1;
//
//            while (j >= 0 && weights[j] < key ) {
//                businessNames[j+1] = businessNames[j];
//                weights[j+1] = weights[j];
//                j = j -1;
//            }
//            weights[j+1] = key;
//            businessNames[j+1] = value;
//        }


    }




    public static Business findFile(String filename) throws IOException, ClassNotFoundException {

        FileInputStream fileIn = new FileInputStream("src/files/" + filename);
        ObjectInputStream in =  new ObjectInputStream(fileIn);

        return (Business) in.readObject();

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
