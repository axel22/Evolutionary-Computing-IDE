package hr.fer.zemris.ga_framework.model.misc;




/**
 * A pair class the standard Java framework
 * so sadly lacks.
 * 
 * @author Axel
 *
 * @param <X>
 * @param <Y>
 */
public class Pair<X, Y> {

	public X first;
	public Y second;
	
	

	/* ctors */
	
	public Pair() {
		first = null;
		second = null;
	}
	
	public Pair(X firstElement, Y secondElement) {
		first = firstElement;
		second = secondElement;
	}
	

	/* methods */
	
	public void setFirst(X first) {
		this.first = first;
	}
	public X getFirst() {
		return first;
	}
	public void setSecond(Y second) {
		this.second = second;
	}
	public Y getSecond() {
		return second;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pair)) return false;
		Pair<?, ?> that = (Pair<?, ?>) o;
		
		return ((this.first == null && that.first == null) || (this.first != null && this.first.equals(that.first))) 
			&& ((this.second == null && that.second == null) || (this.second != null && this.second.equals(that.second)));
	}

	@Override
	public int hashCode() {
		int hash = 0;
		
		if (first != null) hash += first.hashCode();
		if (second != null) hash += second.hashCode();
		
		return hash;
	}

	@Override
	public String toString() {
		return "[" + ((first != null) ? first : "null") + ", " + ((second != null) ? second : "null") + "]";
	}
	
}














