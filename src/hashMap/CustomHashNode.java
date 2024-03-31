package hashMap;

import java.io.Serializable;

//custom hash map implementation
public class CustomHashNode<K,V> implements Serializable {
    K key; //the key associated with node
    V value; //the value associated with key
    final int hashCode; //hash code of the key

    CustomHashNode<K,V> next; //incase of collision

    public CustomHashNode(K key, V value, int hashCode) { //constructor
        this.key = key;
        this.value = value;
        this.hashCode = hashCode;
    }
}
