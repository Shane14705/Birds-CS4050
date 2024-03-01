package assignment.countries;

public class OrderedDictionary implements OrderedDictionaryADT {

    Node root;

    OrderedDictionary() {
        root = new Node();
    }

    /**
     * Returns the Record object with key k, or it returns null if such a record
     * is not in the dictionary.
     *
     * @param k
     * @return
     * @throws assignment/birds/DictionaryException.java
     */
    @Override
    public CountryRecord find(DataKey k) throws DictionaryException {
        Node current = root;
        int comparison;
        if (root.isEmpty()) {         
            throw new DictionaryException("There is no record matches the given key");
        }

        while (true) {
            comparison = current.getData().getDataKey().compareTo(k);
            if (comparison == 0) { // key found
                return current.getData();
            }
            if (comparison == 1) {
                if (current.getLeftChild() == null) {
                    // Key not found
                    throw new DictionaryException("There is no record matches the given key");
                }
                current = current.getLeftChild();
            } else if (comparison == -1) {
                if (current.getRightChild() == null) {
                    // Key not found
                    throw new DictionaryException("There is no record matches the given key");
                }
                current = current.getRightChild();
            }
        }

    }

    /**
     * Inserts r into the ordered dictionary. It throws a DictionaryException if
     * a record with the same key as r is already in the dictionary.
     *
     * @param r
     * @throws birds.DictionaryException
     */
    @Override
    public void insert(CountryRecord r) throws DictionaryException {
        if (r == null || r.getDataKey() == null) {
            throw new IllegalArgumentException("Cannot insert null CountryRecord or CountryRecord with null DataKey.");
        }
        System.out.println("[insert] Attempting to insert CountryRecord: " + r.getDataKey().getBirdName());
        if (root.getData().getDataKey() == null) {
            System.out.println("[insert] Initializing root with: " + r.getDataKey().getBirdName());
            root = new Node(r);
        } else {
            System.out.println("[insert] Inserting new node.");
            insertRec(root, r);
        }
    }

    private Node insertRec(Node current, CountryRecord r) throws DictionaryException {
        if (current == null) {
            System.out.println("[insertRec] Inserting new node for CountryRecord!");
            return new Node(r);
        }

        int comparison = current.getData().getDataKey().compareTo(r.getDataKey());
        System.out.println("BIRD KEY:" + r.getDataKey().getBirdName());
        if (comparison > 0) {
            current.setLeftChild(insertRec(current.getLeftChild(), r));
        } else if (comparison < 0) {
            current.setRightChild(insertRec(current.getRightChild(), r));
        } else {
            throw new DictionaryException("A record with the same key already exists: " + r.getDataKey().getBirdName());
        }

        return current;
    }










    /**
     * Removes the record with Key k from the dictionary. It throws a
     * DictionaryException if the record is not in the dictionary.
     *
     * @param k
     * @throws birds.DictionaryException
     */
    @Override
    public void remove(DataKey k) throws DictionaryException {
        root = removeRec(root, k);
    }

    private Node removeRec(Node current, DataKey k) throws DictionaryException {
        if (current == null) {
            throw new DictionaryException("No record exists with the given key.");
        }

        int comparison = k.compareTo(current.getData().getDataKey());
        if (comparison < 0) {
            current.setLeftChild(removeRec(current.getLeftChild(), k));
        } else if (comparison > 0) {
            current.setRightChild(removeRec(current.getRightChild(), k));
        } else {
            // Node to be deleted found; now proceed with its removal

            // Case 1: Node with only one child or no child
            if (current.getLeftChild() == null) {
                return current.getRightChild();
            } else if (current.getRightChild() == null) {
                return current.getLeftChild();
            }

            // Case 2: Node with two children:
            // Get the in-order successor (smallest in the right subtree)
            current.setData(findSmallest(current.getRightChild()));

            // Delete the in-order successor
            current.setRightChild(removeRec(current.getRightChild(), current.getData().getDataKey()));
        }

        return current;
    }

    private CountryRecord findSmallest(Node root) {
        while (root.getLeftChild() != null) {
            root = root.getLeftChild();
        }
        return root.getData();
    }

    /**
     * Returns the successor of k (the record from the ordered dictionary with
     * smallest key larger than k); it returns null if the given key has no
     * successor. The given key DOES NOT need to be in the dictionary.
     *
     * @param k
     * @return
     * @throws birds.DictionaryException
     */
    @Override
    public CountryRecord successor(DataKey k) throws DictionaryException {
        Node current = root;
        Node successor = null;

        while (current != null) {
            if (current.getData().getDataKey().compareTo(k) > 0) {
                successor = current;
                current = current.getLeftChild();
            } else {
                current = current.getRightChild();
            }
        }

        if (successor == null) {
            return null; // No successor found
        } else {
            return successor.getData();
        }
    }



    /**
     * Returns the predecessor of k (the record from the ordered dictionary with
     * largest key smaller than k; it returns null if the given key has no
     * predecessor. The given key DOES NOT need to be in the dictionary.
     *
     * @param k
     * @return
     * @throws birds.DictionaryException
     */
    @Override
    public CountryRecord predecessor(DataKey k) throws DictionaryException {
        Node current = root;
        Node predecessor = null;

        while (current != null) {
            if (current.getData().getDataKey().compareTo(k) < 0) {
                predecessor = current;
                current = current.getRightChild();
            } else {
                current = current.getLeftChild();
            }
        }

        if (predecessor == null) {
            // No predecessor found, return null
            return null;
        } else {
            // Predecessor found, return its data
            return predecessor.getData();
        }
    }

    /**
     * Returns the record with smallest key in the ordered dictionary. Returns
     * null if the dictionary is empty.
     *
     * @return
     */
    @Override
    public CountryRecord smallest() throws DictionaryException {
        if (root == null) {
            throw new DictionaryException("The dictionary is empty.");
        }
        Node current = root;
        while (current.getLeftChild() != null) {
            current = current.getLeftChild();
        }
        if (current.getData() == null || current.getData().getDataKey() == null) {
            System.out.println("[smallest] Error: Smallest record or its key is null.");
            throw new DictionaryException("Error: Smallest record or its key is null.");
        }
        return current.getData();
    }


    /*
	 * Returns the record with largest key in the ordered dictionary. Returns
	 * null if the dictionary is empty.
     */
    @Override
    public CountryRecord largest() throws DictionaryException {
        if (root == null) {
            return null; // Tree is empty
        }

        Node current = root;
        while (current.getRightChild() != null) {
            current = current.getRightChild(); // Keep going right to find the largest key
        }

        return current.getData(); // The rightmost node
    }


    /* Returns true if the dictionary is empty, and true otherwise. */
    @Override
    public boolean isEmpty (){
        return root.isEmpty();
    }
}
