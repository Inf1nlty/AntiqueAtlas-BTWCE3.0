package hunternif.atlas.util;

import java.util.Iterator;

public class ArrayIterator<T> implements Iterator<T> {
    private final T[] array;
    private int currentIndex = 0;

    public ArrayIterator(T[] array) {
        this.array = array;
    }

    public boolean hasNext() {
        return this.currentIndex < this.array.length;
    }

    public T next() {
        int i = this.currentIndex++;
        return (T)this.array[i];
    }

    public void remove() {
        throw new UnsupportedOperationException("cannot remove items from an array");
    }
}
