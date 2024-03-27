package hashMap;

public class CustomHashNode<K,V> {
    K key;
    V value;
    final int hashCode;

    CustomHashNode<K,V> next;

    public CustomHashNode(K key, V value, int hashCode) {
        this.key = key;
        this.value = value;
        this.hashCode = hashCode;
    }
}
