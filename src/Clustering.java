import bTree.BTree;
import classes.Business;
import hashMap.CustomMap;

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

        String medoidReview = businessHashMap.get("ew_Hhp12Silh3qjoPaW9IA.ser").getRv_text();

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
            if (!filename.equalsIgnoreCase("ew_Hhp12Silh3qjoPaW9IA.ser")) {

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
        String[] businessNames = new String[businessHashMap.size()];
        double[] weights = new double[businessHashMap.size()];
        for (String filename: b.getValues()) {
            totalWeightMap.add(filename, calculateWeight(countOfEachWordMap.get(filename), dfCount, businessHashMap.size()));
//            System.out.println(businessHashMap.get(filename).getName() + calculateWeight(countOfEachWordMap.get(filename), dfCount, businessHashMap.size()));


            businessNames[i] = businessHashMap.get(filename).getName();
            weights[i] = calculateWeight(countOfEachWordMap.get(filename), dfCount, businessHashMap.size());
            i++;

        }

        int n = weights.length;
        for (int k = 0; k < n; k++) {
            double key = weights[k];
            String value = businessNames[k];
            int j = k - 1;

            while (j >= 0 && weights[j] < key ) {
                businessNames[j+1] = businessNames[j];
                weights[j+1] = weights[j];
                j = j -1;
            }
            weights[j+1] = key;
            businessNames[j+1] = value;
        }

        for (int a = 0; a < 10; a++) {
            System.out.println(businessNames[a] + " and " + weights[a]);
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
