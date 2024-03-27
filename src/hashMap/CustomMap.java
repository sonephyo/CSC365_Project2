package hashMap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

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



}
