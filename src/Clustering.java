import bTree.BTree;
import classes.Business;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Clustering {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("src/btreeOutput/output.ser");
        ObjectInputStream objectInputStream= new ObjectInputStream(fileInputStream);
        BTree b = (BTree) objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();


        // Pre categorizing for clustering
        Map<String, Business> businessHashMap = new HashMap<>();
        Map<String, String> businessReviewMap = new HashMap<>();
        Map<String, int[]> countOfEachWordMap = new HashMap<>();
        Map<String, boolean[]> containsWordMap = new HashMap<>();
        Map<String, Double> totalWeightMap = new HashMap<>();

        // Making hashmap with all businesses
        for (String i: b.getValues()) {
            Business b1 = findFile(i);
            businessHashMap.put(i, b1);
            businessReviewMap.put(i, b1.getRv_text());
        }


        // function for calculating weight depending on medoid and one review

        String medoidReview = businessHashMap.get("1-z7wd860Rii4kbEMCT8DA.ser").getRv_text();

        String[] medoidSplit = cleanString(medoidReview);

        // Note: DF means Total Number of reviews that contain that word
        int[] dfCount = new int[medoidSplit.length];


        for (String i: b.getValues()) {
            int[] countOfEachWord = new int[medoidSplit.length];
            Arrays.fill(countOfEachWord, 0);
            countOfEachWordMap.put(i, countOfEachWord);


            boolean[] containsWord = new boolean[medoidSplit.length];
            Arrays.fill(containsWord, false);
            containsWordMap.put(i, containsWord);
        }


        b.traverse();

        for(String filename: b.getValues()) {
            if (!filename.equalsIgnoreCase("1-z7wd860Rii4kbEMCT8DA.ser")) {

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


            }
        }

        for (String filename: b.getValues()) {
            totalWeightMap.put(filename, calculateWeight(countOfEachWordMap.get(filename), dfCount, businessHashMap.size()));
            System.out.println(businessHashMap.get(filename).getName() + calculateWeight(countOfEachWordMap.get(filename), dfCount, businessHashMap.size()));
        }


        System.out.println(medoidReview);


        List<Map.Entry<String, Double>> entries = new ArrayList<>(totalWeightMap.entrySet());

        entries.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

        // Get the first three entries
        List<Map.Entry<String, Double>> topThreeEntries = entries.subList(0, Math.min(3, entries.size()));

        // Print the top 3 entries
        System.out.println("Top 3 entries:");
        for (Map.Entry<String, Double> entry : topThreeEntries) {
            System.out.println(businessHashMap.get(entry.getKey()).getName() + ": " + entry.getValue());
        }
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
