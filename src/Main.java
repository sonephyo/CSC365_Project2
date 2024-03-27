import classes.Business;
import classes.Review;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    private JComboBox<String> userInputField;
    private JButton searchButton;
    private JTextArea resultArea;

    private JButton cateButton;

    public Main(){
        setTitle("Business Recommendar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        comboBoxModel.addElement(("Moon's Kitchen Cafe"));
        comboBoxModel.addElement(("Thai Legacy Restaurant"));
        comboBoxModel.addElement(("Blues City Deli"));
        comboBoxModel.addElement(("New India's Kitchen"));
        comboBoxModel.addElement(("Enterprise Rent-A-Car"));
        comboBoxModel.addElement(("EATS! American Grill"));
        comboBoxModel.addElement(("Helena Avenue Bakery"));
        comboBoxModel.addElement(("Yolklore"));
        comboBoxModel.addElement(("Zimmer's Seafood"));
        comboBoxModel.addElement(("Target"));

        userInputField = new JComboBox<>(comboBoxModel);
        userInputField.setEditable(true);
        resultArea = new JTextArea();


        cateButton = new JButton("Cluster");
        searchButton = new JButton("Search");
        resultArea = new JTextArea();

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Enter a business name:  "));
        inputPanel.add(userInputField);
        inputPanel.add(searchButton);
        inputPanel.add(cateButton);
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        searchButton.addActionListener(e -> search());
        pack();
        setLocationRelativeTo(null);

        cateButton.addActionListener(e -> {
            try {
                cluster();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        pack();
        setLocationRelativeTo(null);


    }

    private void cluster() throws IOException, ClassNotFoundException {
        resultArea.setText("");
        resultArea.append("Hello! This is button for cluster");
        FileInputStream fileInputStream = new FileInputStream("src/files/0-Tim4ucmy8bNugnzKXG8g.ser");
        ObjectInputStream objectInputStream= new ObjectInputStream(fileInputStream);
        Business b = (Business) objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();
        System.out.println("Business: " + b.getName());
        System.out.println("Business ID : " + b.getBusiness_id());
        System.out.println("Business Category: " + Arrays.toString(b.getCategoriesArr()));

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
