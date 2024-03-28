package bTree;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


//B-tree data structure
//insertion, search, traversal, and conversion to a serialized file
public class BTree implements Serializable {

    //Attributes
    BTreeNode root; //the root node of B tree
    int t; //the minimum degree

    Set<String> valuesSet = new HashSet<>();//store values from B-tree

    //Constructor
    public BTree(int t) { //initializing a B-tree with a root and minimum degree t
        this.root = null;
        this.t = t;
    }

    public void traverse() { //traverse the B-tree, traversel operations to the root
        if (root != null) {
            root.traverse();
        }
    }

    //method to retrieves the values stored in the B-tree
    public ArrayList<String> getValues() {
        if (root != null) {
            //call getValues of the root to collect all the values into a set(ValueSet)
            //return a new array list of valuesSet
            root.getValues(valuesSet);
            return new ArrayList<>(valuesSet);
        }
        return null;
    }

    //search for a key "k" in the B-tree
    BTreeNode search(String k) {
        return root == null ? null : root.search(k);
    }

    public void insert(String k, String j) { //inserting method, key-value pair to tree
        if (root == null) {
            // when first element is added
            root = new BTreeNode(t, true);
            root.keys[0] = k;
            root.values[0] = j;
            root.n = 1;
        } else {
            if (root.n == 2 * t - 1) {
                // when the node is fulled already
                BTreeNode s = new BTreeNode(t, false);
                s.C[0] = root;
                s.splitChild(0, root);
                int i = 0;
                if (s.keys[0].compareTo(k) < 0) {
                    i++;
                }
                s.C[i].insertNonFull(k, j);
                root = s;
            } else {
                // insert to the non-full node
                root.insertNonFull(k,j);
            }
        }
    }

    public void convertTreeToSerFile() throws IOException { //serialize B-tree objects to a file
        FileOutputStream fileOut = new FileOutputStream("src/btreeOutput/output.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(this);
        out.close();
        fileOut.close();

    }
}
