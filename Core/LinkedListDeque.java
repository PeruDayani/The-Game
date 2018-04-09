package byog.Core;

public class LinkedListDeque<T> {
    private IntNode sentinel;
    private int size;
    private IntNode last;

    public LinkedListDeque() {
        sentinel = new IntNode(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    public int size() {
        return this.size;
    }

    public void addFirst(T item) {
        IntNode temp = new IntNode(item, sentinel.next, sentinel);
        sentinel.next = temp;
        temp.next.prev = temp;
        size += 1;
    }

    public void addLast(T item) {
        size += 1;
        sentinel.prev.next = new IntNode(item, sentinel, sentinel.prev);
        sentinel.prev = sentinel.prev.next;
    }

    public boolean isEmpty() {
        if (size == 0) {
            return true;
        }
        return false;
    }

    public void printDeque() {
        IntNode temp = new IntNode(sentinel.next.item, sentinel.next.next, sentinel.next.prev);
        while (temp != sentinel) {
            System.out.print(temp.item + " ");
            temp = temp.next;
        }
    }

    public T removeFirst() {
        if (this.isEmpty()) {
            return null;
        }
        T temp = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size -= 1;
        return temp;
    }

    public T removeLast() {
        if (this.isEmpty()) {
            return null;
        }
        T temp = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size -= 1;
        return temp;
    }

    public T get(int index) {
        T returnValue = null;
        if (size == 0 || index > size - 1) {
            return returnValue;
        } else {
            int count = 0;
            IntNode p = sentinel.next;
            while (p != sentinel) {
                if (count == index) {
                    returnValue = p.item;
                }
                p = p.next;
                count += 1;
            }
        }
        return returnValue;
    }

    private class IntNode {
        private T item;
        private IntNode next;
        private IntNode prev;

        private IntNode(T i, IntNode n, IntNode p) {
            item = i;
            next = n;
            prev = p;
        }
    }
}
