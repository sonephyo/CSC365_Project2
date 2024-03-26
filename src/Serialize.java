import bTree.BTree;
import classes.Business;
import classes.Review;
import classes.Business;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.*;
import java.io.FileReader;
import java.io.IOException;


public class Serialize {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        createSerFiles();

        File path = new File("src/files");
        File[] files = path.listFiles();


        BTree btree = new BTree(3);
        // Deserializing and importing in BTree
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            if (files[i].isFile()) {
                FileInputStream fileIn = new FileInputStream(files[i]);
                ObjectInputStream in =  new ObjectInputStream(fileIn);

                Business b1  = (Business) in.readObject();



                String fileNameString = String.valueOf(files[i]).replaceAll("src/files/","");

                btree.insert(b1.getName(), fileNameString);

                fileIn.close();
                in.close();
            }
        }

        btree.convertTreeToSerFile();

        FileInputStream fileIn = new FileInputStream("src/btreeOutput/output.ser");
        ObjectInputStream in =  new ObjectInputStream(fileIn);
        BTree b = (BTree) in.readObject();

        b.traverse();




    }



    public static void createSerFiles() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("src/database/businesses.json"));

        // Building Gson
        GsonBuilder gb = new GsonBuilder();
        Gson gson = gb.create();
        String line;


        // Making the hashtable for the businesses
        Hashtable<String, Business> businessHashtable = new Hashtable<>();
        while ((line = br.readLine()) != null) {
            Business b1 = gson.fromJson(line, Business.class);
            if (b1.getCategories() != null) {
                b1.setCategoriesArr(b1.getCategories().split(", "));
                b1.setCategories(null);
            }
            businessHashtable.put(b1.getBusiness_id(), b1);
        }

        // For review
        BufferedReader brReview = new BufferedReader(new FileReader("src/database/reviews.json"));

        GsonBuilder gbReview = new GsonBuilder();
        Gson gsonReview = gbReview.create();

        String lineReview;
        int reviewcount = 0;
        int reviewLengthToParse = 10000;

        Review[] reviewList = new Review[reviewLengthToParse];
        Set<String> uniqueBusinessNames = new HashSet<>(); // to keep track of unique business names

        while ((lineReview = brReview.readLine()) != null && reviewcount < reviewLengthToParse) {
            Review r1 = gsonReview.fromJson(lineReview, Review.class);
            Business business = businessHashtable.get(r1.getBusiness_id());
            if (business != null && !uniqueBusinessNames.contains(business.getName())) {
                business.setRv_text(r1.getReview_text());

                uniqueBusinessNames.add(business.getName());
                reviewList[reviewcount] = r1;

                String fileName = business.getBusiness_id() + ".ser";
                FileOutputStream fileOut = new FileOutputStream("src/files/" + fileName);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(business);
                out.close();
                fileOut.close();
                System.out.println("saved");

                reviewcount++;
                //uniqueBusinessNames.add(business.getName());
            }
        }



    }

}





