package hunternif.atlas.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ListMapValueIterator<E> implements Iterator<E> {
    private final Iterator<List<E>> valuesIter;
    private Iterator<E> nextListIter;
    private E next;
    private boolean immutable = false;

    public ListMapValueIterator(Map<?, List<E>> map) {
        this.valuesIter = map.values().iterator();
    }

    public ListMapValueIterator<E> setImmutable(boolean value) {
        this.immutable = value;
        return this;
    }

    public boolean hasNext() {
        if (this.next == null) {
            this.next = (E)this.findNext();
        }

        return this.next != null;
    }

    public E next() {
        if (this.next == null) {
            this.next = (E)this.findNext();
        }

        E next = this.next;
        this.next = null;
        return next;
    }

    public void remove() {
        if (!this.immutable) {
            this.nextListIter.remove();
        }

    }

    private E findNext() {
        while(this.nextListIter == null || !this.nextListIter.hasNext()) {
            if (!this.valuesIter.hasNext()) {
                return null;
            }

            this.nextListIter = ((List)this.valuesIter.next()).iterator();
        }

        return (E)this.nextListIter.next();
    }
}
