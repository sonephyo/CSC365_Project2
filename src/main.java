import bTree.BTree;
import classes.Business;
import classes.Review;
import java.io.*;
import java.util.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class main implements Serializable {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("src/files/0-3kCit8mt8cCjiQXDyg8w.ser");
        ObjectInputStream objectInputStream= new ObjectInputStream(fileInputStream);
        Business r = (Business) objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();
        System.out.println("Business id: " + r.getBusiness_id());
        System.out.println(r.getRv_text());

    }

}



















