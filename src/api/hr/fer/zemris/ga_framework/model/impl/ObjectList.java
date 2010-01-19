package hr.fer.zemris.ga_framework.model.impl;

import hr.fer.zemris.ga_framework.model.ISerializable;
import hr.fer.zemris.ga_framework.model.ParameterTypes;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.StringEscapeUtils;

public class ObjectList extends ArrayList<Object> implements ISerializable {

	/* static fields */
	private static final long serialVersionUID = 1L;
	
	/* private fields */
	private ParameterTypes ptype;
	private List<Object> allowed;
	
	/* ctors */
	
	/**
	 * Algorithm implementers should NEVER use this ctor!!
	 * Once again, NEVER USE THIS CTOR.
	 * It's for serialization purposes only.
	 */
	public ObjectList() {
		ptype = null;
		allowed = null;
	}
	
	/**
	 */
	public ObjectList(ArrayList<Object> lst, ParameterTypes pt, List<Object> allowedVals) {
		super(lst);
		ptype = pt;
		allowed = allowedVals;
	}
	
	public ObjectList(ObjectList other) {
		ptype = other.ptype;
		if (other.allowed != null) allowed = new ArrayList<Object>(other.allowed);
		for (Object o : other) {
			super.add(o);
		}
		// note - the objects in the list are immutable, thus,
		// deep copy is not required
	}
	
	/**
	 * Ctor that should be used by algorithm implementers.
	 * 
	 * @param pt
	 * The type of the parameter - never null and never ISERIALIZABLE.
	 * @param allowedObjectsInThisList
	 * Objects of corresponding type that are allowed in this object list.
	 * If any object is allowed, simply pass null here.
	 */
	public ObjectList(ParameterTypes pt, List<Object> allowedObjectsInThisList) {
		if (pt == null) throw new IllegalArgumentException("Parameter type cannot be null.");
		if (pt.equals(ParameterTypes.ISERIALIZABLE)) throw new IllegalArgumentException("Only works for primitive types.");
		
		ptype = pt;
		allowed = allowedObjectsInThisList;
	}
	
	/* methods */
	
	public void addObjectString(String serializedstr) {
		// deserialize
		serializedstr = serializedstr.substring(1, serializedstr.length() - 1);
		Object o = ptype.deserialize(serializedstr);
		
		// add to list
		super.add(o);
	}
	
	public void addAllowedObjectString(String serstr) {
		serstr = serstr.substring(1, serstr.length() - 1);
		Object o = ptype.deserialize(serstr);
		if (allowed == null) allowed = new ArrayList<Object>();
		allowed.add(o);
	}
	
	public void setParameterType(String paramTypeName) {
		ptype = ParameterTypes.valueOf(paramTypeName);
	}
	
	public ParameterTypes getParameterType() {
		return ptype;
	}
	
	public List<Object> getAllowedObjects() {
		return allowed;
	}
	
	public ISerializable deserialize(String s) {
		super.clear();
		allowed = null;
		
		Digester d = new Digester();
		ObjectList ol = new ObjectList();
		d.push(ol);
		d.addCallMethod("r/pt", "setParameterType", 1);
		d.addCallParam("r/pt", 0);
		d.addCallMethod("r/allowed/o", "addAllowedObjectString", 1);
		d.addCallParam("r/allowed/o", 0);
		d.addCallMethod("r/d", "addObjectString", 1);
		d.addCallParam("r/d", 0);
		
		try {
			d.parse(new StringReader(s));
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not deserialize.", e);
		}
		
		return ol;
	}

	public String serialize() {
		StringBuilder sb = new StringBuilder("<r>");
		
		sb.append("<pt>");
		sb.append(ptype.name());
		sb.append("</pt>");
		
		if (allowed != null) {
			sb.append("<allowed>");
			for (Object o : allowed) {
				sb.append("<o>!");
				sb.append(StringEscapeUtils.escapeXml(o.toString()));
				sb.append("!</o>");
			}
			sb.append("</allowed>");
		}
		
		for (Object o : this) {
			sb.append("<d>!");
			sb.append(StringEscapeUtils.escapeXml(o.toString()));
			sb.append("!</d>");
		}
		sb.append("</r>");
		
		return sb.toString();
	}

	public Object average(List<? extends Object> objs) {
		throw new UnsupportedOperationException();
	}

	public boolean hasAverage() {
		return false;
	}

	public boolean hasMax() {
		return false;
	}

	public boolean hasMedian() {
		return false;
	}

	public boolean hasMin() {
		return false;
	}

	public boolean hasStandardDeviation() {
		return false;
	}

	public boolean isComparable() {
		return false;
	}

	public Object max(List<? extends Object> objs) {
		throw new UnsupportedOperationException();
	}

	public Object median(List<? extends Object> objs) {
		throw new UnsupportedOperationException();
	}

	public Object min(List<? extends Object> objs) {
		throw new UnsupportedOperationException();
	}

	public Object stddev(List<? extends Object> objs) {
		throw new UnsupportedOperationException();
	}
	
	public Object sum(List<? extends Object> objs) {
		throw new UnsupportedOperationException();
	}

	public int compareTo(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean hasSum() {
		return false;
	}

	public ISerializable deepCopy() {
		return new ObjectList(this);
	}
	
	public ArrayList<Object> getOrdinaryListCopy() {
		return new ArrayList<Object>(this);
	}
	
	@Override
	public void add(int index, Object element) {
		throw new UnsupportedOperationException("This class is immutable.");
	}

	@Override
	public boolean add(Object e) {
		throw new UnsupportedOperationException("This class is immutable.");
	}

	@Override
	public boolean addAll(Collection<? extends Object> c) {
		throw new UnsupportedOperationException("This class is immutable.");
	}

	@Override
	public boolean addAll(int index, Collection<? extends Object> c) {
		throw new UnsupportedOperationException("This class is immutable.");
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("This class is immutable.");
	}

	@Override
	public Object remove(int index) {
		throw new UnsupportedOperationException("This class is immutable.");
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("This class is immutable.");
	}

	@Override
	public Object set(int index, Object element) {
		throw new UnsupportedOperationException("This class is immutable.");
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ObjectList)) return false;
		ObjectList other = (ObjectList) o;
		if (this.ptype != other.ptype) return false;
		if (this.allowed == null && other.allowed != null) return false;
		if (!this.allowed.equals(other.allowed)) return false;
		boolean supereq = super.equals(o);
		return supereq;
	}

	@Override
	public int hashCode() {
		int hash = ptype.hashCode();
		if (allowed != null) hash += allowed.hashCode();
		hash += super.hashCode();
		return hash;
	}
	
}














