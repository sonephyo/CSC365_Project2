package classes;

import java.io.Serializable;
import java.util.Arrays;

public class Business implements Serializable {
    private int busId;
    private String busName;
    private String[] categories;


    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String[] getAttributes() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "business{" +
                "busId=" + busId +
                ", busName='" + busName + "'" +
                ", categories=" + Arrays.toString(categories) +
                '}';
    }
}