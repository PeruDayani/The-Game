package byog.Core;

public class ArrayDeque<T> {
    private T[] items;
    private int size;
    private int last;
    private int first;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        first = 4;
        last = 5;
    }

    public static boolean contains(int[] arr, int item) {
        for (int n : arr) {
            if (item == n) {
                return true;
            }
        }
        return false;
    }

    /* To test whether my removeIndex function was working or not */
    public static void main(String[] args) {
        ArrayDeque<Integer> l = new ArrayDeque<>();
        l.addLast(5);
        l.addLast(6);
        l.addLast(7);
        l.addLast(8);
        l.addLast(4);
        int[] remove = new int[]{1, 3};
        l.removeIndex(remove);
        l.printDeque();
    }

    public void addFirst(T item) {
        if (size < items.length) {
            items[first] = item;
            first -= 1;
            if (first < 0) {
                first = items.length - 1;
            }
            size += 1;
        } else {
            resize(size * 2);
            addFirst(item);
        }
    }

    public void addLast(T item) {
        if (size < items.length) {
            items[last] = item;
            last = (last + 1) % items.length;
            size += 1;
        } else {
            resize(size * 2);
            addLast(item);
        }
    }

    public boolean isEmpty() {
        if (size == 0) {
            return true;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int temp = (first + 1) % items.length;
        while (temp != last) {
            System.out.print(items[temp] + " ");
            temp = (temp + 1) % items.length;
        }
    }

    public T removeFirst() {
        if (this.isEmpty()) {
            return null;
        }
        first = (first + 1) % items.length;
        T temp = items[first];
        items[first] = null;
        size -= 1;
        if (items.length >= 16 && (((double) size / (double) items.length) < 0.25)) {
            resize(items.length / 2);
        }
        return temp;
    }

    public T removeLast() {
        if (this.isEmpty()) {
            return null;
        }
        last = last - 1;
        if (last < 0) {
            last = items.length - 1;
        }
        T temp = items[last];
        items[last] = null;
        size -= 1;
        if (items.length >= 16 && (((double) size / (double) items.length) < 0.25)) {
            resize(items.length / 2);
        }
        return temp;
    }

    public T get(int index) {
        int pos = (first + 1) % items.length;
        int count = 0;
        while (count < index) {
            pos = (pos + 1) % items.length;
            count += 1;
        }
        return items[pos];
    }

    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        int counter = 0;
        if (size == items.length) {
            int temp = (last + 1) % items.length;
            a[0] = items[last];
            counter += 1;
            while (temp != last) {
                a[counter] = items[temp];
                temp = (temp + 1) % items.length;
                counter += 1;
            }
        } else {
            int temp = (first + 1) % items.length;
            while (temp != last) {
                a[counter] = items[temp];
                temp = (temp + 1) % items.length;
                counter += 1;
            }
        }
        items = a;
        last = counter;
        first = capacity - 1;
    }

    public void removeIndex(int[] i) {
        T[] a = (T[]) new Object[items.length - i.length];
        for (int j = 0; j < i.length; j++) {
            i[j] = (first + 1 + i[j]) % items.length;
        }
        int counter = 0;
        if (size == items.length) {
            int temp = (last + 1) % items.length;
            while (temp != last) {
                if (!contains(i, temp)) {
                    a[counter] = items[temp];
                    counter += 1;
                }
                temp = (temp + 1) & items.length;
            }
        } else {
            int temp = (first + 1) % items.length;
            while (temp != last) {
                if (!contains(i, temp)) {
                    a[counter] = items[temp];
                    counter += 1;
                }
                temp = (temp + 1) % items.length;
            }
        }
        size -= i.length;
        items = a;
        last = counter;
        first = items.length - 1;
    }
}
