import classes.Business;
import classes.Review;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import javax.swing.*;

public class Main {

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader("src/database/businesses.json"));

        // Building Gson
        GsonBuilder gb = new GsonBuilder();
        Gson gson = gb.create();
        String line;
        int busCount = 0;
        int busLength = 10000;

        ArrayList<String> businessName = new ArrayList<>();


        // Making the hashtable for the businesses
        Map<String, Business> businessHashMap = new HashMap<>();
        while ((line = br.readLine()) != null && busCount < busLength) {
            Business b1 = gson.fromJson(line, Business.class);
            if(!businessName.contains(b1.getName())){
                businessName.add(b1.getName());
                businessHashMap.put(b1.getBusiness_id(), b1);

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
        //System.out.println(businessHashMap

    }










}
