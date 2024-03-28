package bTree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

//properties of B-tree node
//methods for insertion, splitting, traversal, values retrieval, and searching
//each node can hold multiple keys and values
class BTreeNode implements Serializable { //represent a node in a B-Tree
    String[] keys; //array to store keys
    String[] values; //array to store corresponding values associated with keys
    int t; //minimum degree
    BTreeNode[] C; //to store child nodes
    int n; //number of keys stored in the node
    boolean leaf; //indicate whether the node is a leaf node




    public BTreeNode(int t, boolean leaf) { //constructor
        this.keys = new String[2 * t - 1];
        this.values = new String[2 * t - 1];
        this.t = t;
        this.C = new BTreeNode[2 * t];
        this.n = 0;
        this.leaf = leaf;
    }

    void insertNonFull(String k, String j) { //insertion
        int i = n - 1;
        if (leaf) {
            // put the number in the node to its order
            while (i >= 0 && keys[i].compareTo(k) > 0) {
                keys[i + 1] = keys[i];
                values[i + 1] = values[i];
                i--;
            }
            keys[i + 1] = k;
            values[i + 1] = j;
            n++;
        } else {
            while (i >= 0 && keys[i].compareTo(k) > 0) {
                i--;
            }
            if (C[i + 1].n == 2 * t - 1) {
                splitChild(i + 1, C[i + 1]);
                if (keys[i+1].compareTo(k) > 0) {
                    i++;
                }
            }
            C[i + 1].insertNonFull(k, j);
        }
    }

    void splitChild(int i, BTreeNode y) {
        // The right part and populating it with the right elements
        BTreeNode z = new BTreeNode(y.t, y.leaf);
        z.n = t - 1;
        for (int j = 0; j < t - 1; j++) {
            z.keys[j] = y.keys[j + t];
            z.values[j] = y.values[j + t];
        }

        if (!y.leaf) {
            for (int j = 0; j < t; j++) {
                z.C[j] = y.C[j + t];
            }
        }
        y.n = t - 1;

        for (int j = n; j > i; j--) {
            C[j + 1] = C[j];
        }
        // the right btree node become the child of the parent
        C[i + 1] = z;

        for (int j = n - 1; j >= i; j--) {
            keys[j + 1] = keys[j];
            values[j + 1] = values[j];
        }
        keys[i] = y.keys[t - 1];
        values[i] = y.values[t - 1];
        n++;
    }

    void traverse() { //traverses
        for (int i = 0; i < n; i++) {
            if (!leaf) {
                C[i].traverse();
            }
            System.out.print("[" + keys[i] + ", " + values[i] + "]");
            System.out.println();
        }
        if (!leaf) {
            C[n].traverse();
        }
    }


    void getValues(Set<String> mainArrayset) { //retrieves all the values in subtree
        for (int i = 0; i < n; i++) {
            if (!leaf) {
                C[i].getValues(mainArrayset);
            }
            mainArrayset.add(values[i]);
        }
        if (!leaf) {
            C[n].getValues(mainArrayset);
        }
    }


    BTreeNode search(String k) { //search method, search for k key in the subtree
        int i = 0;
        while (i < n && k.compareTo(keys[i]) > 0)  {
            i++;
        }
        if (i < n && k.equalsIgnoreCase(keys[i])) {
            return this;
        }
        if (leaf) {
            return null;
        }
        return C[i].search(k);
    }
}
