
package bTree;

import java.io.IOException;
import java.io.*;
import java.util.*;
import java.io.FileReader;
import java.io.IOException;
import classes.Business;
import java.io.Serializable;
import classes.Review;




public class Deserial implements Serializable {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("src/files/0-3kCit8mt8cCjiQXDyg8w.ser");
        ObjectInputStream objectInputStream= new ObjectInputStream(fileInputStream);
        Review r = (Review) objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();
        System.out.println("Business id: " + r.getBusiness_id());
        System.out.println(("Business name: " + r.getBusiness_name()));
        System.out.println("Review Text: " + r.getReview_text());

    }

}
