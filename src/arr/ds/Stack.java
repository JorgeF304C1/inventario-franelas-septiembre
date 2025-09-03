package arr.ds;

public class Stack<T> {
    private final SinglyLinkedList<T> list = new SinglyLinkedList<>();
    public void push(T v) { list.addFirst(v); }
    public T pop() { return list.removeFirst(); }
    public boolean isEmpty() { return list.isEmpty(); }
    public int size() { return list.size(); }
}
