package bTree;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BTree implements Serializable {
    BTreeNode root;
    int t;

    Set<String> valuesSet = new HashSet<>();

    public BTree(int t) {
        this.root = null;
        this.t = t;
    }

    public void traverse() {
        if (root != null) {
            root.traverse();
        }
    }

    public Set<String> getValues() {
        if (root != null) {
            root.getValues(valuesSet);
            return valuesSet;
        }
        return null;
    }

    BTreeNode search(String k) {
        return root == null ? null : root.search(k);
    }

    public void insert(String k, String j) {
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

    public void convertTreeToSerFile() throws IOException {
        FileOutputStream fileOut = new FileOutputStream("src/btreeOutput/output.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(this);
        out.close();
        fileOut.close();

    }
}
