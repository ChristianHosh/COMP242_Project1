package com.example.project1ds;

public abstract class Node {
    private Node next;

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public abstract void setKey(int key);
    public abstract String toString();
    public abstract String toData();
    public abstract int getKey();
}
