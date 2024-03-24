//

package classes;

import java.io.Serializable;
import java.util.Arrays;

public class Business implements Serializable {

    private String business_id;
    private String name;
    private String categories;

    private String[] categoriesArr;

    private String Rv_text;

    public String getRv_text() {
        return Rv_text;
    }

    public void setRv_text(String rv_text) {
        Rv_text = rv_text;
    }

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
                ", categories='" + categories + '\'' +
                ", categoriesArr=" + Arrays.toString(categoriesArr) +
                ", Rv_text='" + Rv_text + '\'' +
                '}';
    }
}