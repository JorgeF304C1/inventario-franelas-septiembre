package arr.ds;

public class SinglyLinkedList<T> {
    private Node<T> head, tail;
    private int size;

    void addLast(T v) {
        if (v == null) throw new IllegalArgumentException("Valor nulo no permitido");
        Node<T> n = new Node<>(v);
        if (tail == null) { head = tail = n; }
        else { tail.next = n; tail = n; }
        size++;
    }

    T removeFirst() {
        if (head == null) throw new IllegalStateException("Estructura vacía");
        T v = head.data;
        head = head.next;
        if (head == null) tail = null;
        size--;
        return v;
    }

    void addFirst(T v) {
        if (v == null) throw new IllegalArgumentException("Valor nulo no permitido");
        Node<T> n = new Node<>(v);
        n.next = head;
        head = n;
        if (tail == null) tail = n;
        size++;
    }

    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }
}
