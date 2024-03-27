package hashMap;

import java.lang.reflect.Array;
import java.util.*;

public class CustomMap<K,V> {

    private ArrayList<CustomHashNode<K,V>> bucketArray;

    private int numBuckets; // capacity of arrayList

    private int size; // input size of arrayList

    public CustomMap() {
        bucketArray = new ArrayList<>();
        numBuckets = 10;
        size = 0;
        for (int i = 0; i < numBuckets; i++) {
            bucketArray.add(null);
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    private final int hashCode (K key) {
        return Objects.hashCode(key);
    }

    private int getBucketIndex(K key) {
        int hashCode = hashCode(key);
        int index = hashCode % numBuckets;
        index = index < 0 ? index * -1 : index;
        return index;
    }

    public void add(K key, V value) {
        int bucketIndex = getBucketIndex(key);
        int hashCode = hashCode(key);

        CustomHashNode<K,V> head = bucketArray.get(bucketIndex);


        // Getting through the node chain
        while (head != null) {

            if (head.key.equals(key) && head.hashCode == hashCode) {
                head.value = value;
                return;
            }
            head = head.next;
        }

        //Insert key
        size++;
        head = bucketArray.get(bucketIndex);
        CustomHashNode<K,V>  newNode = new CustomHashNode<>(key, value, hashCode);
        newNode.next = head;
        bucketArray.set(bucketIndex, newNode);

        if ((double)size / numBuckets >=0.7) {
            ArrayList<CustomHashNode<K,V>> temp = bucketArray;
            bucketArray = new ArrayList<>();
            numBuckets = numBuckets * 2;
            size = 0;

            for (int i = 0; i < numBuckets; i++) {
                bucketArray.add(null);
            }

            for (CustomHashNode<K,V> headNode: temp) {
                while (headNode != null) {
                    add(headNode.key, headNode.value);
                    headNode = headNode.next;
                }
            }
        }

    }

    public V get(K key) {
        int bucketIndex = getBucketIndex(key);
        int hashCode = hashCode(key);

        CustomHashNode<K,V> head = bucketArray.get(bucketIndex);

        while (head != null) {
            if (key.equals(head.key) && hashCode == head.hashCode) {
                return head.value;
            }
            head = head.next;
        }

        return null;
    }

    public ArrayList<V> getAllValues() {
        ArrayList<V> allValues = new ArrayList<>();

        for (CustomHashNode<K,V> node: bucketArray) {
            while (node != null) {
                allValues.add(node.value);
                node = node.next;
            }
        }

        return allValues;
    }

    public ArrayList<K> getAllKeys() {
        ArrayList<K> allKeys = new ArrayList<>();

        for (CustomHashNode<K,V> node: bucketArray) {
            while (node != null) {
                allKeys.add(node.key);
                node = node.next;
            }
        }
        return allKeys;
    }


    public static void main(String[] args) {

        Set<String> medoidNames2 = new HashSet<>(Set.of(
                "1-z7wd860Rii4kbEMCT8DA.ser",
                "2-XK9zDgSKqOwSyyMwgjzA.ser",
                "1bNU0VL7S7_-gVgLJAy9gQ.ser"));
        Set<String> medoidNames1 = new HashSet<>(Set.of(
                "2-XK9zDgSKqOwSyyMwgjzA.ser",
                "1-z7wd860Rii4kbEMCT8DA.ser",

                "1bNU0VL7S7_-gVgLJAy9gwQ.ser"));

        Set<Set<String>> tracking = new HashSet<>();
        tracking.add(medoidNames1);
        tracking.add(medoidNames2);
        System.out.println(tracking);
    }


}
