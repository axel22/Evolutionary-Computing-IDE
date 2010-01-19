package hr.fer.zemris.ga_framework.algorithms.tsp;

import java.util.Iterator;

public class CircularQueue<T> implements Iterable<T> {
	
	/* static fields */

	/* private fields */
	private Object[] array;
	private int sz;
	private int first, freepos;

	/* ctors */
	
	public CircularQueue(int size) {
		array = new Object[size];
		sz = 0;
		first = freepos = 0;
	}

	/* methods */
	
	public int capacity() {
		return array.length;
	}
	
	public int size() {
		return sz;
	}
	
	public void add(T elem) {
		if (sz != array.length) {
			sz++;
			array[freepos] = elem;
			freepos = (freepos + 1) % array.length;
		} else {
			array[freepos] = elem;
			freepos = (freepos + 1) % array.length;
		}
	}
	
	@SuppressWarnings("unchecked")
	public T first() {
		if (sz == 0) return null;
		return (T) array[first];
	}
	
	@SuppressWarnings("unchecked")
	public T last() {
		if (sz == 0) return null;
		int last = freepos - 1;
		if (last < 0) last += array.length;
		return (T) array[last];
	}

	public Iterator<T> iterator() {
		return new Iterator<T>() {
			int i = first, count = sz;
			public boolean hasNext() {
				return count > 0;
			}
			@SuppressWarnings("unchecked")
			public T next() {
				T elem = (T) array[i];
				i = (i + 1) % array.length;
				count--;
				return elem;
			}
			public void remove() {
				throw new UnsupportedOperationException("Cannot remove.");
			}
		};
	}

}














