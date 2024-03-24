package classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class Review implements Serializable {

    private String business_id;

    private double stars;
    private String text;

    private String business_name;


    private int[] countOfEachWord;

    private boolean[] containsWord;

    private double totalWeight;

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public int[] getCountOfEachWord() {
        return countOfEachWord;
    }

    public boolean[] getContainsWord() {
        return containsWord;
    }

    public String getBusiness_id() {;
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public double getStars() {
        return stars;
    }

    public String getReview_text() {
        return text;
    }

    public void setTrueContainsWord(int changeIndex) {
        this.containsWord[changeIndex] = true;
    }



    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    // Frequency Table Initializer
    public void init_FreqTableForEachReview(String[] userInputData) {
        countOfEachWord = new int[userInputData.length];
        Arrays.fill(countOfEachWord, 0);

        containsWord = new boolean[userInputData.length];
        Arrays.fill(containsWord, false);
    }

    // Incrementing word count in frequency table
    public void incrementCountOfEach(int incrementIndex) {
        this.countOfEachWord[incrementIndex]++;
    }

    @Override
    public String toString() {
        return "Review{" +
                ", business_name='" + business_name + '\'' +
                "business_id='" + business_id + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}