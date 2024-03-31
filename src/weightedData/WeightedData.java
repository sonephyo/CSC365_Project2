package weightedData;

import java.io.Serializable;

public class WeightedData implements Serializable { //getting key and its weighted data
    private String key; //key(Names)
    private double value; //weighted data value

    public WeightedData(String key, double value) { //constructor
        this.key = key;
        this.value = value;
    }


    //setter and getter
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() { //toString method
        return "WeightedData{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
