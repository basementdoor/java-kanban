package util;

import model.Task;

public class Node {
    private Task value;
    private Node prev;
    private Node next;

    public Node(Task value, Node prev, Node next) {
        this.value = value;
        this.prev = prev;
        this.next = next;
    }

    public Task getValue() {
        return value;
    }

    public void setValue(Task value) {
        this.value = value;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}
