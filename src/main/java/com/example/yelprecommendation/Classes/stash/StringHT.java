package com.example.yelprecommendation.Classes.stash;

import java.io.*;
import java.util.Iterator;

// Adapted through Doug Lea's Code (https://gee.cs.oswego.edu/dl/csc365/HT.java)
class StringHT implements Serializable, Iterable<Object>{
    static final class Node {
        Object key;
        Node next;
        String id;
        //         Object value;
        Node(Object k, String id, Node n) { key = k; this.id = id; next = n; }
    }
    Node[] table = new Node[8]; // always a power of 2
    int size = 0;
    boolean contains(Object key) {
        int h = key.hashCode();
        int i = h & (table.length - 1);
        for (Node e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key))
                return true;
        }
        return false;
    }
    String getID(Object key) {
        int h = key.hashCode();
        int i = h & (table.length - 1);
        for (Node e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key))
                return e.id;
        }
        return "";
    }
    void setID(Object key, String id) {
        int h = key.hashCode();
        int i = h & (table.length - 1);
        for (Node e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key))
                e.id = id;
        }
    }
    void copyHT(StringHT hashtable) {
        for (Node node : hashtable.table) {
            while (node != null) {
                this.add(node.key, node.id);
                node = node.next;
            }
        }
    }
    //    Object getKey(Object key) {
//        int h = key.hashCode();
//        int i = h & (table.length - 1);
//        for (Node e = table[i]; e != null; e = e.next) {
//            if (key.equals(e.key))
//                return e.value;
//        }
//        return false;
//    }
    void add(Object key, String id) {
        int h = key.hashCode();
        int i = h & (table.length - 1);
        for (Node e = table[i]; e != null; e = e.next) {
            if (key.equals(e.key))
                return;
        }
        table[i] = new Node(key, id, table[i]);
        ++size;
        if ((float)size/table.length >= 0.75f)
            resize();
    }
    void resize() {
        Node[] oldTable = table;
        int oldCapacity = oldTable.length;
        int newCapacity = oldCapacity << 1;
        Node[] newTable = new Node[newCapacity];
        for (int i = 0; i < oldCapacity; ++i) {
            for (Node e = oldTable[i]; e != null; e = e.next) {
                int h = e.key.hashCode();
                int j = h & (newTable.length - 1);
                newTable[j] = new Node(e.key, e.id, newTable[j]);
            }
        }
        table = newTable;
    }
    void resizeV2() { // avoids unnecessary creation
        Node[] oldTable = table;
        int oldCapacity = oldTable.length;
        int newCapacity = oldCapacity << 1;
        Node[] newTable = new Node[newCapacity];
        for (int i = 0; i < oldCapacity; ++i) {
            Node e = oldTable[i];
            while (e != null) {
                Node next = e.next;
                int h = e.key.hashCode();
                int j = h & (newTable.length - 1);
                e.next = newTable[j];
                newTable[j] = e;
                e = next;
            }
        }
        table = newTable;
    }
    void remove(Object key) {
        int h = key.hashCode();
        int i = h & (table.length - 1);
        Node e = table[i], p = null;
        while (e != null) {
            if (key.equals(e.key)) {
                if (p == null)
                    table[i] = e.next;
                else
                    p.next = e.next;
                break;
            }
            p = e;
            e = e.next;
        }
    }
    void printAll() {
        for (int i = 0; i < table.length; ++i)
            for (Node e = table[i]; e != null; e = e.next)
                System.out.printf("%-20s %s%n", "Business ID: " + e.key, "Business Name: " + e.id);
    }
    private void writeObject(ObjectOutputStream s) throws Exception {
        s.defaultWriteObject();
        s.writeInt(size);
        for (int i = 0; i < table.length; ++i) {
            for (Node e = table[i]; e != null; e = e.next) {
                s.writeObject(e.key);
            }
        }
    }
    private void readObject(ObjectInputStream s) throws Exception {
        s.defaultReadObject();
        int n = s.readInt();
        for (int i = 0; i < n; ++i)
            add(s.readObject(), String.valueOf(s.readObject()));
    }

    @Override
    public Iterator<Object> iterator() {
        return new KeyIterator();
    }

    public Iterator<String> countIterator() {
        return new CountIterator();
    }


    private class KeyIterator implements Iterator<Object> {
        int currentIndex = 0;
        Node currentNode = table[0];

        @Override
        public boolean hasNext() {
            while (currentIndex < table.length && currentNode == null) {
                currentNode = table[currentIndex++];
            }
            return currentNode != null;
        }

        @Override
        public Object next() {
            Object key = currentNode.key;
            currentNode = currentNode.next;
            if (currentNode == null && currentIndex < table.length) {
                currentNode = table[currentIndex++];
            }
            return key;
        }
    }

    private class CountIterator implements Iterator<String> {
        int currentIndex = 0;
        Node currentNode = table[0];

        @Override
        public boolean hasNext() {
            while (currentIndex < table.length && currentNode == null) {
                currentNode = table[currentIndex++];
            }
            return currentNode != null;
        }

        @Override
        public String next() {
            String id = currentNode.id;
            currentNode = currentNode.next;
            if (currentNode == null && currentIndex < table.length) {
                currentNode = table[currentIndex++];
            }
            return id;
        }
    }

}

