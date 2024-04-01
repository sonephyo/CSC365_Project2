import bTree.BTree;
import classes.Business;
import classes.Review;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hashMap.CustomMap;
import weightedData.WeightedData;

import javax.swing.border.LineBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    private JComboBox<String> userInputField;
    private JButton searchButton;
    private JTextArea resultArea;

    private JButton cateButton;
    private JPanel titleBarPanel;

    public Main(){
        setTitle("Business Recommendar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        comboBoxModel.addElement("Sukho Thai");
        comboBoxModel.addElement("Direct Energy Solar");
        comboBoxModel.addElement("The Silverspoon");
        comboBoxModel.addElement("Denny's");
        comboBoxModel.addElement("Lily");
        comboBoxModel.addElement("Suite Six");
        comboBoxModel.addElement("Helena Avenue Bakery");
        comboBoxModel.addElement("All About Hair");
        comboBoxModel.addElement("Plush");
        comboBoxModel.addElement("Sake House");

        userInputField = new JComboBox<>(comboBoxModel);
        userInputField.setEditable(true);
        searchButton = new JButton("Search");
        searchButton.setBackground(Color.BLUE);
        cateButton = new JButton("Cluster");
        resultArea = new JTextArea();
        resultArea.setEditable(false);

        // Input Panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(new JLabel("Enter a business name: "), BorderLayout.WEST);
        searchPanel.add(userInputField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        inputPanel.add(searchPanel, BorderLayout.CENTER);
        inputPanel.add(cateButton, BorderLayout.EAST);

        // Frame
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Action Listeners
        searchButton.addActionListener(e -> search());
        cateButton.addActionListener(e -> {
            try {
                cluster();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        // Frame properties
        setSize(600, 400); // Set initial size
        setLocationRelativeTo(null); // Center the frame on the screen

    }

    private void cluster() throws IOException, ClassNotFoundException {

        resultArea.setText("");

        //Clustering c1 = new Clustering();
        //CustomMap<String, ArrayList<WeightedData>> output = c1.run();

//        ArrayList<String> keys = output.getAllKeys();
//        CustomMap<String, Business>  businessMap = c1.getBusinessHashMap();
//
//        resultArea.append("Total cost: " + c1.calculateTotalCost(output) + "\n");
//        for (String key: keys) {
//            resultArea.append("-------"  + "\n");
//            resultArea.append("Medoid business name: " + businessMap.get(key).getName()  + "\n");
//            resultArea.append("The keys in the cluster for above medoid: " + "\n");
//            ArrayList<WeightedData> weightedDataArrayList = output.get(key);
//            weightedDataArrayList.sort(Comparator.comparingDouble(WeightedData::getValue).reversed());
//            for (int i = 0; i < 3; i++) {
//                resultArea.append(businessMap.get(weightedDataArrayList.get(i).getKey()).getName() + " : " + weightedDataArrayList.get(i).getValue() + "\n");
//            }
//        }
//        resultArea.append();
        FileInputStream fileIn = new FileInputStream("src/clusterOutput/clusters.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Object obj = in.readObject();
        fileIn.close();
        in.close();

        FileInputStream fileInputStream = new FileInputStream("src/btreeOutput/output.ser");
        ObjectInputStream objectInputStream= new ObjectInputStream(fileInputStream);
        BTree b = (BTree) objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();

        CustomMap<String, Business> businessHashMap = new CustomMap<>();


        //Populate the businessHashmap with business objects from BTree
        //businessReviewMap with review text
        for (String i: b.getValues()) {
            Business b1 = findFile(i);
            businessHashMap.add(i, b1);
        }

        CustomMap <String, ArrayList<WeightedData>> clusterList = (CustomMap<String, ArrayList<WeightedData>>) obj;
        System.out.println(clusterList.getAllValues());
//        ArrayList values = clusterList.getAllValues();
        String userInput = Objects.requireNonNull(userInputField.getSelectedItem()).toString();

        String userInputSername = "";
        for (String i: businessHashMap.getAllKeys()) {
            if (businessHashMap.get(i).getName().equalsIgnoreCase(userInput)) {
                userInputSername = i;
                System.out.println(userInputSername);
            }
        }

        String clusterContainingUserInput = ""; //store the cluster containing userInput
        ArrayList<String> keys = clusterList.getAllKeys(); //retrieve all keys
        for (String key: keys){
            ArrayList<WeightedData> cluster = clusterList.get(key); //retrieve the associated cluster
            for (WeightedData i : cluster){ //each element in cluster
                if(i.getKey().equals(userInputSername)){ //if matches the userinput
                    clusterContainingUserInput = key; //then the cluster containing the userinput
                }

            }
        }
        System.out.println("Usernput: " + userInput + "\n");
        System.out.println(clusterContainingUserInput);
        System.out.println("The medoid name associated with the cluster userinput belong to: " + businessHashMap.get(clusterContainingUserInput).getName());



    }

    private void search() {
        resultArea.setText("");
        try {

            BufferedReader br = new BufferedReader(new FileReader("src/database/businesses.json"));

            // Building Gson
            GsonBuilder gb = new GsonBuilder();
            Gson gson = gb.create();
            String line;


            // Making the hashtable for the businesses to connect reviews and businesses with id
            Hashtable<String, Business> businessHashtable = new Hashtable<>();
            while ((line = br.readLine()) != null) {
                Business b1 = gson.fromJson(line, Business.class);
                businessHashtable.put(b1.getBusiness_id(), b1);
            }

            // Building Gson for review
            BufferedReader brReview = new BufferedReader(new FileReader("src/database/reviews.json"));
            GsonBuilder gbReview = new GsonBuilder();
            Gson gsonReview = gbReview.create();

            // Making a table for reviews and taking track of the total number of reviews
            String lineReview;
            int reviewcount = 0; // Number of reviews, n
            int reviewLengthToParse = 10000;

            Review[] reviewList = new Review[reviewLengthToParse];

            while ((lineReview = brReview.readLine()) != null && reviewcount < reviewLengthToParse) {
                Review r1 = gsonReview.fromJson(lineReview, Review.class);
                r1.setBusiness_name(businessHashtable.get(r1.getBusiness_id()).getName());
                reviewList[reviewcount] = r1;
                reviewcount++;
            }


            // Getting user input and cleaning the string
            String userInput = userInputField.getSelectedItem().toString() ;
            String userInputStoreReview = searchForStore(reviewList, userInput);

            reviewList = Arrays.stream(reviewList)
                    .filter(s -> !(s.getBusiness_name().equalsIgnoreCase(userInput)))
                    .toArray(Review[]::new); // Removing the same store name that user inputs from the review list

            String[] inputSplit = cleanString(userInputStoreReview);

            // initializing for document frequency(df)
            // Note: DF means Total Number of reviews that contain that word
            int[] dfCount = new int[inputSplit.length];

            // Populating the frequency table with tf and (True/False) values for df
            for (Review review : reviewList) {
                review.init_FreqTableForEachReview(inputSplit); // initialize the frequency table depending on the user input
                String[] cleanReviewData = cleanString(review.getReview_text());

                for (String i : cleanReviewData) {
                    for (int j = 0; j < inputSplit.length; j++) {
                        if (inputSplit[j].equalsIgnoreCase(i)) {
                            review.incrementCountOfEach(j);
                            review.setTrueContainsWord(j);
                        }
                    }
                }

                // Calculating the df values from true or false values from frequency table
                for (int i = 0; i < review.getContainsWord().length; i++) {
                    if (review.getContainsWord()[i]) {
                        dfCount[i]++;
                    }
                }
            }

            // Calculating the total weights for each review
            for (Review r : reviewList) {
                r.setTotalWeight(calculateWeight(r.getCountOfEachWord(), dfCount, reviewLengthToParse));
            }

            // Sorting the reviews by their total weight in descending orders
            Arrays.sort(reviewList, new Comparator<Review>() {
                @Override
                public int compare(Review r1, Review r2) {
                    return Double.compare(r2.getTotalWeight(), r1.getTotalWeight());
                }
            });


            // Outputting two businesses with the highest total weight values.
            int outputNumber = 10;
            for (int i = 0; i < outputNumber; i++) {
//                System.out.println("__________");
//                System.out.println(reviewList[i].getBusiness_name());
//                System.out.println(reviewList[i].getTotalWeight());
//                System.out.println(reviewList[i].getReview_text());
                Business businessOutput = businessHashtable.get(reviewList[i].getBusiness_id());
                resultArea.append("Business name: " + businessOutput.getName() + "\n");
            }

        } catch (Exception e){
            resultArea.append("Store not found! Choose another one!");
        }
    }

    // Method for calculating total weight
    private static double calculateWeight(int[] tfData,int[] dfData, int totalReview) {
        double total = 0;
        for (int i = 0; i < tfData.length; i++) {
            total += Math.log10(1+tfData[i])*((double) totalReview /(dfData[i]+1));
        }
        return total;
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

    // Check if the user inputted store exists in the dataset
    private static String searchForStore(Review[] reviewList, String userInput) {
        for (Review r: reviewList) {
            if (r.getBusiness_name().equalsIgnoreCase(userInput)) {
                return r.getReview_text();
            }
        }
        return null;
    }

    private static Business findFile(String filename) throws IOException, ClassNotFoundException {
        //file path
        FileInputStream fileIn = new FileInputStream("src/files/" + filename);
        ObjectInputStream in =  new ObjectInputStream(fileIn);
        Object findOutput = in.readObject();
        fileIn.close();
        in.close();

        return (Business) findOutput;

    }

    // To run the GUI
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                new Main().setVisible(true);
            }
        });

    }
}
