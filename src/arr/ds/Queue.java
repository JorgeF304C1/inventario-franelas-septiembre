package arr.ds;

public class Queue<T> {
    private final SinglyLinkedList<T> list = new SinglyLinkedList<>();
    public void enqueue(T v) { list.addLast(v); }
    public T dequeue() { return list.removeFirst(); }
    public boolean isEmpty() { return list.isEmpty(); }
    public int size() { return list.size(); }
}
