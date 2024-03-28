import bTree.BTree;
import classes.Business;
import hashMap.CustomMap;
import weightedData.WeightedData;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Clustering {

    private  BTree b;

    private Set<Set<String>> tracking = new HashSet<>();

    //customMap is used to store business data where the key is a string and values is a Business
    private CustomMap<String, Business> businessHashMap = new CustomMap<>();

    //Map business IDs to review texts
    private CustomMap<String, String> businessReviewMap = new CustomMap<>();

    //track index for random medoid
    private Set<Integer> trackingSerFilesRandomIndex = new HashSet<>();


    public CustomMap<String, Business> getBusinessHashMap() {
        return businessHashMap;
    }


    public CustomMap<String, ArrayList<WeightedData>> run() throws IOException, ClassNotFoundException {
        //reads data from a serialized file
        FileInputStream fileInputStream = new FileInputStream("src/btreeOutput/output.ser");
        ObjectInputStream objectInputStream= new ObjectInputStream(fileInputStream);
        b = (BTree) objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();

        //Populate the businessHashmap with business objects from BTree
        //businessReviewMap with review text
        for (String i: b.getValues()) {
            Business b1 = findFile(i);
            businessHashMap.add(i, b1);
            businessReviewMap.add(i, b1.getRv_text());
        }
        
        ArrayList<String> allSerFiles =  b.getValues();

        //call getRandomSerFile to randomly select 5 medoids
        String medoid1 = getRandomSerFile(allSerFiles);
        String medoid2 = getRandomSerFile(allSerFiles);
        String medoid3 = getRandomSerFile(allSerFiles);
        String medoid4 = getRandomSerFile(allSerFiles);
        String medoid5 = getRandomSerFile(allSerFiles);

        Set<String> medoidNames1 = new HashSet<>(Set.of(
                medoid1,
                medoid2,
                medoid3,
                medoid4,
                medoid5
        ));

        ArrayList<String> bTreeValues = b.getValues();

        // Clustering
        // String - medoid
        CustomMap<String, ArrayList<WeightedData>> chosen = allocatingToSelectedCluster(medoidNames1.toArray(new String[0]));

        int iteration_count = 0;

        for (String i: bTreeValues) {


            Set<String> medoidNames2 = changeOneValueFromSet(medoidNames1, bTreeValues);

            // String is the mediod names and value is the array of selected values
            CustomMap<String, ArrayList<WeightedData>> dataAllocatedToClusters2 = allocatingToSelectedCluster(medoidNames2.toArray(new String[0]));


            chosen = compareCluster(chosen, dataAllocatedToClusters2);
            System.out.println("Current chosen: " + calculateTotalCost(chosen));
            //System.out.println(calculateTotalCost(chosen));

            if (iteration_count >=2) {
                break;
            }
            iteration_count++;

        }

        return chosen;

    }

    //getting random medorid from the ser files
    public String getRandomSerFile(ArrayList<String> allSerFiles) {
        Random r = new Random();
        int randomSerIndex = r.nextInt(allSerFiles.size());
        //checks if the index already in the set, if so find another one
        while (trackingSerFilesRandomIndex.contains(randomSerIndex)) {
            randomSerIndex = r.nextInt(allSerFiles.size());
        }
        //if the index found, add to the set so that it won't be used again
        trackingSerFilesRandomIndex.add(randomSerIndex);
        return allSerFiles.get(randomSerIndex);
    }


    //change the value to do swapping for medoid
    public Set<String> changeOneValueFromSet(Set<String> originalSet, ArrayList<String> values) {
        Random r = new Random();
        //variables for original index and values from the array list
        int randomIndex = r.nextInt(originalSet.size());
        int randomValue = r.nextInt(values.size());

        //convert set to array list, replaces the random from arr with random value from values
        String[] arr =  originalSet.toArray(new String[0]);
        arr[randomIndex] = values.get(randomValue);

        //check if the tracking set contains a hash set from the modidifed array arr
        //if yes, generate a new set
        if (tracking.contains( new HashSet<>(Arrays.asList(arr)))) {
            return changeOneValueFromSet(originalSet, values);
        } else {
            return new HashSet<>(Arrays.asList(arr));
        }
    }

    //selecting closet cluster based on the cost
    private CustomMap<String, ArrayList<WeightedData>> compareCluster(
            CustomMap<String, ArrayList<WeightedData>> dataAllocatedToClusters1,
            CustomMap<String, ArrayList<WeightedData>> dataAllocatedToClusters2) {

        //call total cost method to calculate total cost of the clusters
        double cluster1cost = calculateTotalCost(dataAllocatedToClusters1);
        double cluster2cost = calculateTotalCost(dataAllocatedToClusters2);
        //if the non-medoid is added to the cluster with highest total cost
        if (cluster1cost > cluster2cost) {
            return dataAllocatedToClusters1;
        } else if (cluster2cost > cluster1cost) {
            return dataAllocatedToClusters2;
        }
        return dataAllocatedToClusters1;
    }

    //calcuating total cost using weighted data, take custompmap which has keys and a list of weighted data values
    public double calculateTotalCost(CustomMap<String, ArrayList<WeightedData>> dataAllocatedToClusters) {

        //retrieves the values(list of weightedData) from dataAllocatedToClusters
        //store them in the array list
        ArrayList<ArrayList<WeightedData>> a = dataAllocatedToClusters.getAllValues();

        //calculate total cost
        double total = 0;
        //iterate over each cluster represented by the lists of "b" in the arrayList "a"
        //for each b, added to sumPart
        for(ArrayList<WeightedData> b: a) {
            double sumPart = 0;
            for (WeightedData d: b) {
                sumPart += d.getValue();
            }
            total = total + sumPart;
        }
        //return the sum after the loop
        return total;
    }

    //Map with string and a list of weightedData values
    //allocated non-medoid to clusters
    public CustomMap<String, ArrayList<WeightedData>> allocatingToSelectedCluster(String[] medoidNames) throws IOException, ClassNotFoundException {
        //to keep track of medoid names to prevent them being used again
        tracking.add(new HashSet<>(List.of(medoidNames)));

        //call weightedData method to calculate which medoid is the most revelant
        CustomMap<String, Double> cluster1WeightedData =  weightedData(medoidNames[0]);
        CustomMap<String, Double> cluster2WeightedData =  weightedData(medoidNames[1]);
        CustomMap<String, Double> cluster3WeightedData =  weightedData(medoidNames[2]);
        CustomMap<String, Double> cluster4WeightedData =  weightedData(medoidNames[3]);
        CustomMap<String, Double> cluster5WeightedData =  weightedData(medoidNames[4]);


        // to store businesses belong to each cluster
        CustomMap<String, ArrayList<WeightedData>> clusterAssignment = new CustomMap<>();

        //for each filename in b tree
        //calculate the weight data of the business related to each medoid
        for (String filename: b.getValues()) {
            Double[] test = new Double[medoidNames.length];

            test[0] = cluster1WeightedData.get(filename);
            test[1] = cluster2WeightedData.get(filename);
            test[2] = cluster3WeightedData.get(filename);
            test[3] = cluster4WeightedData.get(filename);
            test[4] = cluster5WeightedData.get(filename);

            //determine the index of the medoid with the highest weighted value for current business
            int maxIndex = 0;
            for (int i = 0; i < test.length; i++) {
                if (test[i] > test[maxIndex]) {
                    maxIndex = i;
                }
            }

            //retrieve the list of weightedData objects associated with the medoid
            //if already exist, assigns it to oldClusterAdd
            //if not a new arrayList and assigns it to oldClusterAdd
            ArrayList<WeightedData> oldClusterAdd = clusterAssignment.get(medoidNames[maxIndex]) !=null ? clusterAssignment.get(medoidNames[maxIndex]) : new ArrayList<>();
            WeightedData wd = new WeightedData(filename,test[maxIndex]);
            oldClusterAdd.add(wd);

            //updates the clusterAssignment map with modified cluster info
            clusterAssignment.add(
                    medoidNames[maxIndex],
                    oldClusterAdd
            );
        }

        //return the final assignment
        return clusterAssignment;
    }


    //Method that takes medoid filename
    //return a map where keys are filenames and values(weightedData) are double
    public CustomMap<String, Double> weightedData(String medoidFilename) throws IOException, ClassNotFoundException {

        //CustomMap objects for count of each word in the review text for each filename
        //check if the words contain in the review text
        //stores the total weighted value for each file name
        CustomMap<String, int[]> countOfEachWordMap = new CustomMap<>();
        CustomMap<String, boolean[]> containsWordMap = new CustomMap<>();
        CustomMap<String, Double> totalWeightMap = new CustomMap<>();



        // function for calculating weight depending on medoid and one review
        String medoidReview = businessHashMap.get(medoidFilename).getRv_text();

        String[] medoidSplit = cleanString(medoidReview);

        // Note: DF means Total Number of reviews that contain that word
        int[] dfCount = new int[medoidSplit.length];

        //traverse b tree
        //for each file name in b tree, count occurance of each word,
        // track if exist in the review
        for (String i: b.getValues()) {
            int[] countOfEachWord = new int[medoidSplit.length];
            Arrays.fill(countOfEachWord, 0);
            countOfEachWordMap.add(i, countOfEachWord);


            boolean[] containsWord = new boolean[medoidSplit.length];
            Arrays.fill(containsWord, false);
            containsWordMap.add(i, containsWord);
        }


        int count = 0;

        //for each file names, preprocess the review text
        //do count of word
        // check if the review has the word
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
                if (count % 1000 == 0) {
                    System.out.println(count);
                }


            }
        }

        CustomMap<String, Double> weightValues = new CustomMap<>();
        CustomMap<String, String> weightValuesNames = new CustomMap<>();
        //for each file name in b trre
        for (String filename: b.getValues()) {

            //calculate the weighted value using calculateWeight method using countOfEachWordMap, dfCount, the total number of review
            //add the result value to tatalWeightMap
            totalWeightMap.add(filename, calculateWeight(countOfEachWordMap.get(filename), dfCount, businessHashMap.size()));
//            System.out.println(businessHashMap.get(filename).getName() + calculateWeight(countOfEachWordMap.get(filename), dfCount, businessHashMap.size()));

            //add the value to weightValues Map
            //add the business name and its associated value to weightValuesNames map
            weightValues.add(
                    businessHashMap.get(filename).getBusiness_id() + ".ser",
                    calculateWeight(countOfEachWordMap.get(filename), dfCount, businessHashMap.size()));
            weightValuesNames.add(
                    businessHashMap.get(filename).getBusiness_id() + ".ser",
                    businessHashMap.get(filename).getName()
            );

        }
        //return weightValues map containing filenames and associated weighted values
        return weightValues;

    }



    //method to deserialize the ser files
    public Business findFile(String filename) throws IOException, ClassNotFoundException {
        //file path
        FileInputStream fileIn = new FileInputStream("src/files/" + filename);
        ObjectInputStream in =  new ObjectInputStream(fileIn);
        Object findOutput = in.readObject();
        fileIn.close();
        in.close();

        return (Business) findOutput;

    }

    // Cleaning the user input string and outputting a String array
    //reusing cleanString from project 1
    private String[] cleanString(String rawString) throws IOException {
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


    //reusing calculateWeight from project 1
    private double calculateWeight(int[] tfData,int[] dfData, int totalReview) {
        double total = 0;
        for (int i = 0; i < tfData.length; i++) {
            total += Math.log10(1+tfData[i])*((double) totalReview /(dfData[i]+1));
        }
        return total;
    }


}
