import classes.Business;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.*;
import java.io.FileReader;
import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        BufferedReader br = new BufferedReader(new FileReader("src/database/businesses.json"));

        // Building Gson
        GsonBuilder gb = new GsonBuilder();
        Gson gson = gb.create();
        String line;
        int busCount = 0;
        int busLength = 10000;

        ArrayList<String> businessName = new ArrayList<>();


        // Making the hashtable for the businesses
        Hashtable<String, Business> businessHashTable = new Hashtable<>();
        while ((line = br.readLine()) != null && busCount < busLength) {
            Business b1 = gson.fromJson(line, Business.class);
            if(!businessName.contains(b1.getName()) & b1.getCategories() != null){
                b1.setCategoriesArr(b1.getCategories().split(", "));

                businessName.add(b1.getName());
                System.out.println(b1);
                businessHashTable.put(b1.getBusiness_id(), b1);

                String fileName = b1.getBusiness_id() + ".ser";
                FileOutputStream fileOut = new FileOutputStream("src/files/" + fileName);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(b1);
                out.close();
                fileOut.close();
                System.out.println("saved");

                busCount++;
            }

        }



        File path = new File("src/files");
        File[] files = path.listFiles();


        // Deserializing
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            if (files[i].isFile()) {
                FileInputStream fileIn = new FileInputStream(files[i]);
                ObjectInputStream in =  new ObjectInputStream(fileIn);

                Business b1  = (Business) in.readObject();
                System.out.println(b1);
            }


    }











}
