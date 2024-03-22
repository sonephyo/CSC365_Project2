//package classes;
//
//class BTreeNode {
//    Business[] keys;
//    int t;
//    BTreeNode[] C;
//    int n;
//    boolean leaf;
//
//    public BTreeNode(int t, boolean leaf) {
//        this.keys = new Business[2 * t - 1];
//        this.t = t;
//        this.C = new BTreeNode[2 * t];
//        this.n = 0;
//        this.leaf = leaf;
//    }
//
//    void insertNonFull(Business k) {
//        int i = n - 1;
//        if (leaf) {
//            boolean nameCompare = keys[i].getName().compareToIgnoreCase(k.getName()) > 0 ;
//            while (i >= 0 && nameCompare) {
//                keys[i + 1] = keys[i];
//                i--;
//            }
//            keys[i + 1] = k;
//            n++;
//        } else {
//            boolean nameCompare = keys[i].getName().compareToIgnoreCase(k.getName()) > 0 ;
//
//            while (i >= 0 && nameCompare) {
//                i--;
//            }
//            if (C[i + 1].n == 2 * t - 1) {
//                splitChild(i + 1, C[i + 1]);
//                if (keys[i + 1].getName().compareToIgnoreCase(k.getName()) < 0) {
//                    i++;
//                }
//            }
//            C[i + 1].insertNonFull(k);
//        }
//    }
//
//    void splitChild(int i, BTreeNode y) {
//        BTreeNode z = new BTreeNode(y.t, y.leaf);
//        z.n = t - 1;
//        for (int j = 0; j < t - 1; j++) {
//            z.keys[j] = y.keys[j + t];
//        }
//        if (!y.leaf) {
//            for (int j = 0; j < t; j++) {
//                z.C[j] = y.C[j + t];
//            }
//        }
//        y.n = t - 1;
//        for (int j = n; j > i; j--) {
//            C[j + 1] = C[j];
//        }
//        C[i + 1] = z;
//        for (int j = n - 1; j >= i; j--) {
//            keys[j + 1] = keys[j];
//        }
//        keys[i] = y.keys[t - 1];
//        n++;
//    }
//
//    void traverse() {
//        for (int i = 0; i < n; i++) {
//            if (!leaf) {
//                C[i].traverse();
//            }
//            System.out.print(" " + keys[i]);
//        }
//        if (!leaf) {
//            C[n].traverse();
//        }
//    }
//
//    BTreeNode search(int k) {
//        int i = 0;
//        while (i < n && k > keys[i]) {
//            i++;
//        }
//        if (i < n && k == keys[i]) {
//            return this;
//        }
//        if (leaf) {
//            return null;
//        }
//        return C[i].search(k);
//    }
//}
//
//class BTree {
//    BTreeNode root;
//    int t;
//
//    public BTree(int t) {
//        this.root = null;
//        this.t = t;
//    }
//
//    void traverse() {
//        if (root != null) {
//            root.traverse();
//        }
//    }
//
//    BTreeNode search(int k) {
//        return root == null ? null : root.search(k);
//    }
//
//    void insert(Business k) {
//        if (root == null) {
//            root = new BTreeNode(t, true);
//            root.keys[0] = k;
//            root.n = 1;
//        } else {
//            if (root.n == 2 * t - 1) {
//                BTreeNode s = new BTreeNode(t, false);
//                s.C[0] = root;
//                s.splitChild(0, root);
//                int i = 0;
//                if (s.keys[0] < k) {
//                    i++;
//                }
//                s.C[i].insertNonFull(k);
//                root = s;
//            } else {
//                root.insertNonFull(k);
//            }
//        }
//    }
//}
//
//class Main {
//    public static void main(String[] args) {
//        BTree bt = new BTree(4);
//        bt.insert(10);
//        bt.insert(5);
//        bt.insert(9);
//
//    }
//}


