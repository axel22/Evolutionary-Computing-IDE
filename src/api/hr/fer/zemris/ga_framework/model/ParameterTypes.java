package hr.fer.zemris.ga_framework.model;

import hr.fer.zemris.ga_framework.model.misc.Pair;
import hr.fer.zemris.ga_framework.model.misc.Time;
import hr.fer.zemris.ga_framework.model.misc.Time.Metric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;





/**
 * This enumeration lists the common
 * parameter types used for the algorithms.
 * 
 * @author Axel
 *
 */
public enum ParameterTypes {
	/**
	 * Represented by the Time class.
	 */
	TIME() {
		@Override
		public Class<?> getValueClass() {
			return Time.class;
		}
		@Override
		public Object deserialize(String s) {
			return Time.parseTime(s);
		}
		@Override
		public String serialize(Object obj) {
			return ((Time)obj).toString();
		}
		@Override
		public String niceName() {
			return "Time";
		}
		@Override
		public boolean isSimpleArithmeticType() {
			return true;
		}
		@Override
		@SuppressWarnings("unchecked")
		public Pair<Object, Integer> average(List<? extends Object> values) {
			List<Time> lst = (List<Time>) values;
			Metric firstmet = lst.get(0).getMetric();
			
			// first find average
			double intervalsum = 0.0;
			int num = values.size();
			for (int i = 0; i < num; i++) {
				intervalsum += lst.get(i).convertTo(Metric.us).getInterval();
			}
			double avg = intervalsum / num;
			Time avgtime = new Time(avg, Metric.us).convertTo(firstmet);
			
			// now find the value closest to the average
			Time diff = new Time(Double.MAX_VALUE, Metric.h);
			int pos = -1;
			for (int i = 0; i < num; i++) {
				Time currdiff = Time.absolute(Time.subtract(avgtime, lst.get(i)));
				if (currdiff.compareTo(diff) < 0) {
					diff = currdiff;
					pos = i;
				}
			}
			
			return new Pair<Object, Integer>(avgtime, pos);
		}
		@Override
		public Integer maximum(List<? extends Object> values) {
			Time maxobj = (Time)values.get(0);
			Integer maxpos = 0;
			for (int i = 1, sz = values.size(); i < sz; i++) {
				if (maxobj.compareTo((Time)values.get(i)) < 0) {
					maxobj = (Time)values.get(i);
					maxpos = i;
				}
			}
			return maxpos;
		}
		@Override
		@SuppressWarnings("unchecked")
		public Integer median(List<? extends Object> values) {
			List<Time> lst = new ArrayList<Time>((List<Time>)values);
			
			Collections.sort(lst);
			
			return values.indexOf(lst.get(lst.size() / 2));
		}
		@Override
		public Integer minimum(List<? extends Object> values) {
			Time minobj = (Time)values.get(0);
			Integer minpos = 0;
			for (int i = 1, sz = values.size(); i < sz; i++) {
				if (minobj.compareTo((Time)values.get(i)) > 0) {
					minobj = (Time)values.get(i);
					minpos = i;
				}
			}
			return minpos;
		}
		@Override
		public Pair<Object, Integer> stddev(List<? extends Object> values) {
			Double avg = 0.0;
			
			for (Object o : values) {
				Time t = (Time) o;
				avg += t.convertTo(Metric.us).getInterval();
			}
			avg /= values.size();
			
			Double stdev = 0.0;
			for (Object o : values) {
				Time t = (Time) o;
				Double d = t.convertTo(Metric.us).getInterval();
				Double delta = (d - avg);
				stdev += delta * delta;
			}
			stdev /= values.size();
			stdev = Math.sqrt(stdev);
			
			Time t = new Time(stdev, Metric.us).convertTo(((Time) values.get(0)).getMetric());
			return new Pair<Object, Integer>(t, values.size() - 1);
		}
		@Override
		public Pair<Object, Integer> sum(List<? extends Object> values) {
			Double sum = 0.0;
			
			for (Object o : values) {
				Time t = (Time) o;
				sum += t.convertTo(Metric.us).getInterval();
			}
			
			Time t = new Time(sum, Metric.us).convertTo(((Time) values.get(0)).getMetric());
			return new Pair<Object, Integer>(t, values.size() - 1);
		}
		@Override
		public int compare(Object o1, Object o2) {
			Time t1 = (Time) o1, t2 = (Time) o2;
			
			double d1 = t1.convertTo(Metric.us).getInterval();
			double d2 = t2.convertTo(Metric.us).getInterval();
			
			if (d1 < d2) return -1;
			if (d1 > d2) return 1;
			return 0;
		}
		@Override
		public boolean isComparable(Class<?> o) {
			return true;
		}
		@Override
		public boolean doesAllowHandler(Class<?> cls, HandlerTypes ht) {
			return true;
		}
	},
	/**
	 * Integer class.
	 */
	INTEGER() {
		@Override
		public Class<?> getValueClass() {
			return Integer.class;
		}
		@Override
		public Object deserialize(String s) {
			return Integer.parseInt(s);
		}
		@Override
		public String serialize(Object obj) {
			return ((Integer)obj).toString();
		}
		@Override
		public String niceName() {
			return "Integer";
		}
		@Override
		public boolean isSimpleArithmeticType() {
			return true;
		}
		@Override
		@SuppressWarnings("unchecked")
		public Pair<Object, Integer> average(List<? extends Object> values) {
			List<Integer> lst = (List<Integer>) values;
			
			// first find average
			int intsum = 0;
			int num = values.size();
			for (int i = 0; i < num; i++) {
				intsum += lst.get(i);
			}
			Integer avgint = (int)((double)intsum / num);
			
			// now find the value closest to the average
			Integer diff = Integer.MAX_VALUE;
			int pos = -1;
			for (int i = 0; i < num; i++) {
				int currdiff = avgint - lst.get(i);
				if (currdiff < diff) {
					diff = currdiff;
					pos = i;
				}
			}
			
			return new Pair<Object, Integer>(avgint, pos);
		}
		@Override
		public Integer maximum(List<? extends Object> values) {
			Integer maxobj = (Integer)values.get(0);
			Integer maxpos = 0;
			for (int i = 1, sz = values.size(); i < sz; i++) {
				if (maxobj < (Integer)values.get(i)) {
					maxobj = (Integer)values.get(i);
					maxpos = i;
				}
			}
			return maxpos;
		}
		@Override
		@SuppressWarnings("unchecked")
		public Integer median(List<? extends Object> values) {
			List<Integer> lst = new ArrayList<Integer>((List<Integer>)values);
			
			Collections.sort(lst);
			
			return values.indexOf(lst.get(lst.size() / 2));
		}
		@Override
		public Integer minimum(List<? extends Object> values) {
			Integer minobj = (Integer)values.get(0);
			Integer minpos = 0;
			for (int i = 1, sz = values.size(); i < sz; i++) {
				if (minobj > (Integer)values.get(i)) {
					minobj = (Integer)values.get(i);
					minpos = i;
				}
			}
			return minpos;
		}
		@Override
		public Pair<Object, Integer> stddev(List<? extends Object> values) {
			Double avg = 0.0;
			
			for (Object o : values) {
				Integer t = (Integer) o;
				avg += t;
			}
			avg /= values.size();
			
			Double stdev = 0.0;
			for (Object o : values) {
				Integer t = (Integer) o;
				Double delta = (t - avg);
				stdev += delta * delta;
			}
			stdev /= values.size();
			stdev = Math.sqrt(stdev);
			
			return new Pair<Object, Integer>((int)(double)stdev, values.size() - 1);
		}
		@Override
		public Pair<Object, Integer> sum(List<? extends Object> values) {
			Double sum = 0.0;
			
			for (Object o : values) {
				Double t = (Double) o;
				sum += t;
			}
			
			return new Pair<Object, Integer>(sum, values.size() - 1);
		}
		@Override
		public int compare(Object o1, Object o2) {
			Integer i1 = (Integer) o1, i2 = (Integer) o2;
			
			if (i1 < i2) return -1;
			if (i1 > i2) return 1;
			return 0;
		}
		@Override
		public boolean isComparable(Class<?> o) {
			return true;
		}
		@Override
		public boolean doesAllowHandler(Class<?> cls, HandlerTypes ht) {
			return true;
		}
	},
	/**
	 * Double class.
	 */
	REAL() {
		@Override
		public Class<?> getValueClass() {
			return Double.class;
		}
		@Override
		public Object deserialize(String s) {
			return Double.parseDouble(s);
		}
		@Override
		public String serialize(Object obj) {
			return ((Double)obj).toString();
		}
		@Override
		public String niceName() {
			return "Real";
		}
		@Override
		public boolean isSimpleArithmeticType() {
			return true;
		}
		@Override
		@SuppressWarnings("unchecked")
		public Pair<Object, Integer> average(List<? extends Object> values) {
			List<Double> lst = (List<Double>) values;
			
			// first find average
			double doubsum = 0;
			int num = values.size();
			for (int i = 0; i < num; i++) {
				doubsum += lst.get(i);
			}
			Double avgdoub = (doubsum / num);
			
			// now find the value closest to the average
			Double diff = Double.MAX_VALUE;
			int pos = -1;
			for (int i = 0; i < num; i++) {
				double currdiff = avgdoub - lst.get(i);
				if (currdiff < diff) {
					diff = currdiff;
					pos = i;
				}
			}
			
			return new Pair<Object, Integer>(avgdoub, pos);
		}
		@Override
		public Integer maximum(List<? extends Object> values) {
			Double maxobj = (Double)values.get(0);
			Integer maxpos = 0;
			for (int i = 1, sz = values.size(); i < sz; i++) {
				if (maxobj < (Double)values.get(i)) {
					maxobj = (Double)values.get(i);
					maxpos = i;
				}
			}
			return maxpos;
		}
		@Override
		@SuppressWarnings("unchecked")
		public Integer median(List<? extends Object> values) {
			List<Double> lst = new ArrayList<Double>((List<Double>)values);
			
			Collections.sort(lst);
			
			return values.indexOf(lst.get(lst.size() / 2));
		}
		@Override
		public Integer minimum(List<? extends Object> values) {
			Double minobj = (Double)values.get(0);
			Integer minpos = 0;
			for (int i = 1, sz = values.size(); i < sz; i++) {
				if (minobj > (Double)values.get(i)) {
					minobj = (Double)values.get(i);
					minpos = i;
				}
			}
			return minpos;
		}
		@Override
		public Pair<Object, Integer> stddev(List<? extends Object> values) {
			Double avg = 0.0;
			
			for (Object o : values) {
				Double t = (Double) o;
				avg += t;
			}
			avg /= values.size();
			
			double stdev = 0.0;
			for (Object o : values) {
				Double t = (Double) o;
				Double delta = (t - avg);
				stdev += delta * delta;
			}
			stdev /= values.size();
			stdev = Math.sqrt(stdev);
			
			return new Pair<Object, Integer>(stdev, values.size() - 1);
		}
		@Override
		public Pair<Object, Integer> sum(List<? extends Object> values) {
			double sum = 0.0;
			
			for (Object o : values) {
				Integer t = (Integer) o;
				sum += t;
			}
			
			return new Pair<Object, Integer>((int)sum, values.size() - 1);
		}
		@Override
		public int compare(Object o1, Object o2) {
			Double d1 = (Double) o1, d2 = (Double) o2;
			
			if (d1 < d2) return -1;
			if (d1 > d2) return 1;
			return 0;
		}
		@Override
		public boolean isComparable(Class<?> o) {
			return true;
		}
		@Override
		public boolean doesAllowHandler(Class<?> cls, HandlerTypes ht) {
			return true;
		}
	},
	/**
	 * String class.
	 */
	STRING() {
		@Override
		public Class<?> getValueClass() {
			return String.class;
		}
		@Override
		public Object deserialize(String s) {
			return s;
		}

		@Override
		public String serialize(Object obj) {
			return (String)obj;
		}
		@Override
		public String niceName() {
			return "String";
		}
		@Override
		public boolean isSimpleArithmeticType() {
			return false;
		}
		@Override
		public Pair<Object, Integer> average(List<? extends Object> values) {
			throw new UnsupportedOperationException("Does not allow arithmetic.");
		}
		@Override
		public Integer maximum(List<? extends Object> values) {
			throw new UnsupportedOperationException("Does not allow arithmetic.");
		}
		@Override
		public Integer median(List<? extends Object> values) {
			throw new UnsupportedOperationException("Does not allow arithmetic.");
		}
		@Override
		public Integer minimum(List<? extends Object> values) {
			throw new UnsupportedOperationException("Does not allow arithmetic.");
		}
		@Override
		public Pair<Object, Integer> stddev(List<? extends Object> values) {
			throw new UnsupportedOperationException("Does not allow arithmetic.");
		}
		@Override
		public Pair<Object, Integer> sum(List<? extends Object> values) {
			throw new UnsupportedOperationException("Does not allow arithmetic.");
		}
		@Override
		public int compare(Object o1, Object o2) {
			String s1 = (String) o1, s2 = (String) o2;
			
			return s1.compareTo(s2);
		}
		@Override
		public boolean isComparable(Class<?> o) {
			return true;
		}
		@Override
		public boolean doesAllowHandler(Class<?> cls, HandlerTypes ht) {
			if (ht == HandlerTypes.Last) return true;
			return false;
		}
	},
	/**
	 * Boolean class.
	 */
	BOOLEAN() {
		@Override
		public Class<?> getValueClass() {
			return Boolean.class;
		}
		@Override
		public Object deserialize(String s) {
			return Boolean.parseBoolean(s);
		}
		@Override
		public String serialize(Object obj) {
			return ((Boolean)obj).toString();
		}
		@Override
		public String niceName() {
			return "Boolean";
		}
		@Override
		public boolean isSimpleArithmeticType() {
			return false;
		}
		@Override
		public Pair<Object, Integer> average(List<? extends Object> values) {
			throw new UnsupportedOperationException("Does not allow arithmetic.");
		}
		@Override
		public Integer maximum(List<? extends Object> values) {
			throw new UnsupportedOperationException("Does not allow arithmetic.");
		}
		@Override
		public Integer median(List<? extends Object> values) {
			throw new UnsupportedOperationException("Does not allow arithmetic.");
		}
		@Override
		public Integer minimum(List<? extends Object> values) {
			throw new UnsupportedOperationException("Does not allow arithmetic.");
		}
		@Override
		public Pair<Object, Integer> stddev(List<? extends Object> values) {
			throw new UnsupportedOperationException("Does not allow arithmetic.");
		}
		@Override
		public Pair<Object, Integer> sum(List<? extends Object> values) {
			throw new UnsupportedOperationException("Does not allow arithmetic.");
		}
		@Override
		public int compare(Object o1, Object o2) {
			Boolean b1 = (Boolean) o1, b2 = (Boolean) o2;
			
			int i1 = b1 ? 1 : 0;
			int i2 = b2 ? 1 : 0;
			
			if (i1 < i2) return -1;
			if (i1 > i2) return 1;
			return 0;
		}
		@Override
		public boolean isComparable(Class<?> o) {
			return true;
		}
		@Override
		public boolean doesAllowHandler(Class<?> cls, HandlerTypes ht) {
			if (ht == HandlerTypes.Last) return true;
			return false;
		}
	},
	/**
	 * Data that is serializable, i.e. it
	 * abides the ISerializable interface.
	 */
	ISERIALIZABLE() {
		@Override
		public Class<?> getValueClass() {
			return ISerializable.class;
		}
		@Override
		public Object deserialize(String s) {
			throw new IllegalStateException("ISERIALIZABLE cannot do this.");
		}
		@Override
		public String serialize(Object obj) {
			throw new IllegalStateException("ISERIALIZABLE cannot do this.");
		}
		@Override
		public String niceName() {
			return "Special";
		}
		@Override
		public boolean isSimpleArithmeticType() {
			return false;
		}
		@Override
		public Pair<Object, Integer> average(List<? extends Object> values) {
			throw new UnsupportedOperationException("Does not allow arithmetic.");
		}
		@Override
		public Integer maximum(List<? extends Object> values) {
			throw new UnsupportedOperationException("Does not allow arithmetic.");
		}
		@Override
		public Integer median(List<? extends Object> values) {
			throw new UnsupportedOperationException("Does not allow arithmetic.");
		}
		@Override
		public Integer minimum(List<? extends Object> values) {
			throw new UnsupportedOperationException("Does not allow arithmetic.");
		}
		@Override
		public boolean isISerializable() {
			return true;
		}
		@Override
		public Pair<Object, Integer> stddev(List<? extends Object> values) {
			throw new UnsupportedOperationException("Does not allow arithmetic.");
		}
		@Override
		public Pair<Object, Integer> sum(List<? extends Object> values) {
			throw new UnsupportedOperationException("Does not allow arithmetic.");
		}
		@Override
		public int compare(Object o1, Object o2) {
			ISerializable ser1 = (ISerializable) o1, ser2 = (ISerializable) o2;
			
			return ser1.compareTo(ser2);
		}
		@Override
		public boolean isComparable(Class<?> cls) {
			ISerializable iser = null;
			try {
				iser = (ISerializable) cls.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			
			return iser.isComparable();
		}
		@Override
		public boolean doesAllowHandler(Class<?> cls, HandlerTypes ht) {
			ISerializable iser = null;
			try {
				iser = (ISerializable) cls.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			
			switch (ht) {
			case Average:
				return iser.hasAverage();
			case Minimal:
				return iser.hasMin();
			case Maximal:
				return iser.hasMax();
			case Median:
				return iser.hasMedian();
			case StandardDeviation:
				return iser.hasStandardDeviation();
			case Sum:
				return iser.hasSum();
			case Last:
				return true;
			default:
				throw new IllegalStateException("This should not occur.");
			}
		}
	};
	
	/**
	 * Returns the class of the value associated
	 * with a parameter of the given type.
	 * In case of the <code>ISERIALIZABLE</code>,
	 * only the class of the base interface is
	 * returned.
	 * 
	 * @return
	 * Class of the value.
	 */
	public abstract Class<?> getValueClass();
	
	/**
	 * Serializes an object to string, assuming,
	 * of course, it is of correct type.
	 * Not applicable to <code>ISERIALIZABLE</code>
	 * element.
	 * 
	 * @param obj
	 * @return
	 * @throws IllegalStateException
	 * If element is <code>ISERIALIZABLE</code>.
	 * @throws IllegalArgumentException
	 * If the object is not of the correct type.
	 */
	public abstract String serialize(Object obj);
	
	/**
	 * Deserializes an object from string, assuming,
	 * of course, it is of correct type.
	 * Not applicable to <code>ISERIALIZABLE</code>
	 * element.
	 * 
	 * @param s
	 * @return
	 * @throws IllegalStateException
	 * If element is <code>ISERIALIZABLE</code>.
	 */
	public abstract Object deserialize(String s);

	public abstract String niceName();
	
	public abstract boolean isSimpleArithmeticType();
	
	public abstract int compare(Object o1, Object o2);
	
	/**
	 * Returns the average object of the list of objects.
	 * Only applicable if arithmetic is allowed.
	 * 
	 * @param values
	 * @return
	 * An average object, an it's approximate position in the
	 * list (the position of the object closest by value).
	 * @throws UnsupportedOperationException
	 * If the arithmetic is not allowed for the parameter type.
	 * @throws ArrayIndexOutOfBoundsException
	 * If the given list is empty.
	 */
	public abstract Pair<Object, Integer> average(List<? extends Object> values);
	
	/**
	 * Returns the median object of the list of objects.
	 * Only applicable if arithmetic is allowed.
	 * 
	 * @param values
	 * @return
	 * Median object's position.
	 * @throws UnsupportedOperationException
	 * If the arithmetic is not allowed for the parameter type.
	 * @throws ArrayIndexOutOfBoundsException
	 * If the given list is empty.
	 */
	public abstract Integer median(List<? extends Object> values);
	
	/**
	 * Returns the minimum object of the list of objects.
	 * Only applicable if arithmetic is allowed.
	 * 
	 * @param values
	 * @return
	 * Position of the min. object.
	 * @throws UnsupportedOperationException
	 * If the arithmetic is not allowed for the parameter type.
	 * @throws ArrayIndexOutOfBoundsException
	 * If the given list is empty.
	 */
	public abstract Integer minimum(List<? extends Object> values);
	
	/**
	 * Returns the maximum object of the list of objects.
	 * Only applicable if arithmetic is allowed.
	 * 
	 * @param values
	 * @return
	 * Position of the max. object.
	 * @throws UnsupportedOperationException
	 * If the arithmetic is not allowed for the parameter type.
	 * @throws ArrayIndexOutOfBoundsException
	 * If the given list is empty.
	 */
	public abstract Integer maximum(List<? extends Object> values);
	
	/**
	 * Returns the standard deviation of the list of objects.
	 * Only applicable if arithmetic is allowed.
	 * 
	 * @param values
	 * @return
	 * Pair (stddev, position in list).
	 * @throws UnsupportedOperationException
	 * If the arithmetic is not allowed for the parameter type.
	 * @throws ArrayIndexOutOfBoundsException
	 * If the given list is empty.
	 */
	public abstract Pair<Object, Integer> stddev(List<? extends Object> values);
	
	/**
	 * Returns the sum of the objects in the list.
	 * Only applicable if arithmetic is allowed.
	 * 
	 * @param values
	 * @return
	 * Pair (sum, position in list).
	 * @throws UnsupportedOperationException
	 * If the arithmetic is not allowed for the parameter type.
	 * @throws ArrayIndexOutOfBoundsException
	 * If the given list is empty.
	 */
	public abstract Pair<Object, Integer> sum(List<? extends Object> values);
	
	public abstract boolean doesAllowHandler(Class<?> cls, HandlerTypes ht);
	
	public abstract boolean isComparable(Class<?> o);
	
	public boolean isISerializable() {
		return false;
	}
	
}














