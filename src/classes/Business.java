package classes;

import java.io.Serializable;
import java.util.Arrays;

public class Business implements Serializable {

    private String business_id;
    private String name;
    private String categories;

    private String[] categoriesArr;


    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String[] getCategoriesArr() {
        return categoriesArr;
    }

    public void setCategoriesArr(String[] categoriesArr) {
        this.categoriesArr = categoriesArr;
    }

    @Override
    public String toString() {
        return "Business{" +
                "business_id='" + business_id + '\'' +
                ", name='" + name + '\'' +
                ", categoriesArr=" + Arrays.toString(categoriesArr) +
                '}';
    }
}