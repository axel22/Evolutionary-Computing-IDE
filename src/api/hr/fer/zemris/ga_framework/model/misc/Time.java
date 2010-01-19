package hr.fer.zemris.ga_framework.model.misc;





/**
 * Represents a time value.
 * It is immutable.
 * 
 * @author Axel
 *
 */
public final class Time implements Comparable<Time> {
	
	public static enum Metric {
		us() {
			@Override
			public double inUs() {
				return 1;
			}
		},
		ms() {
			@Override
			public double inUs() {
				return 1000;
			}
		},
		s() {
			@Override
			public double inUs() {
				return 1000000;
			}
		},
		min() {
			@Override
			public double inUs() {
				return 60000000;
			}
		},
		h() {
			@Override
			public double inUs() {
				return 3600000000.0;
			}
		};
		
		public abstract double inUs();
		public static Metric parseMetric(String s) {
			s = s.trim();
			if (s.equals("us")) return us;
			else if (s.equals("ms")) return ms;
			else if (s.equals("s")) return ms;
			else if (s.equals("min")) return ms;
			else if (s.equals("h")) return ms;
			else throw new IllegalArgumentException("Metric not recognized.");
		}
		public static int ordinalPosition(Metric m) {
			switch (m) {
			case us:
				return 0;
			case ms:
				return 1;
			case s:
				return 2;
			case min:
				return 3;
			case h:
				return 4;
			}
			if (m == null) return -1;
			throw new IllegalStateException("New metrics may have been added.");
		}
	}
	
	/* static fields */
	
	

	/* private fields */
	private double t;
	private Metric met;
	

	/* ctors */
	
	public Time() {
		t = 0;
		met = Metric.s;
	}
	
	public Time(double interval, Metric metric) {
		t = interval;
		met = metric;
		if (met == null) throw new IllegalArgumentException("Metric cannot be null.");
	}
	

	/* methods */
	
	/**
	 * Returns metric associated with this
	 * time interval.
	 */
	public final Metric getMetric() {
		return met;
	}
	
	/**
	 * @return
	 * Returns the time interval.
	 */
	public final double getInterval() {
		return t;
	}
	
	/**
	 * Converts time to another format.
	 * Time is still the same underneath.
	 * 
	 * @param metric
	 * @return
	 */
	public final Time convertTo(Metric metric) {
		return new Time(t * getRatio(met, metric), metric);
	}
	
	/**
	 * Parses time from string.
	 * 
	 * Time can be of format returned by the <code>toString</code>
	 * method, or metric can be separated from the interval by any
	 * number of spaces or htabs. Metric cannot immediately follow
	 * the interval!
	 * <br/>
	 * Examples:<br/>
	 * 
	 * 5 us <br/>
	 * 4.0e2  s <br/>
	 * 3.13		  h <br/>
	 * 
	 * @param tstr
	 * @return
	 * A Time object.
	 * @throws IllegalArgumentException
	 * If the string does not represent time.
	 */
	public static Time parseTime(String tstr) {
		int pos = tstr.indexOf(' '), b = tstr.indexOf('\t');
		if (pos == -1 || ((b < pos && b != -1))) pos = b;
		if (pos == -1) throw new IllegalArgumentException("No tabs or spaces in time string.");
		
		Double d;
		Metric m;
		try {
			d = Double.parseDouble(tstr.substring(0, pos));
			m = Metric.parseMetric(tstr.substring(pos));
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid time format.", e);
		}
		
		Time t = new Time(d, m);
		
		return t;
	}
	
	/**
	 * @return
	 * Returns how many times metric m2 fits
	 * into metric m1.
	 */
	private static final double getRatio(Metric m1, Metric m2) {
		return m1.inUs() / m2.inUs();
	}
	
	/**
	 * Returns the delta = p - q.
	 * 
	 * @param p
	 * @param q
	 * @return
	 */
	public static final Time subtract(Time p, Time q) {
		Time res = new Time(p.t, p.met);
		
		double d = q.t * getRatio(q.met, p.met);
		res.t -= d;
		
		return res;
	}
	
	public static final Time absolute(Time t) {
		return new Time(Math.abs(t.t), t.met);
	}
	
	/**
	 * Returns the delta = p - q.
	 * 
	 * @param p
	 * @param q
	 * @return
	 */
	public static final Time add(Time p, Time q) {
		Time res = new Time(p.t, p.met);
		
		double d = q.t * getRatio(q.met, p.met);
		res.t += d;
		
		return res;
	}
	
	/**
	 * @return
	 * Returns whether this time equals other time
	 * with some tolerance eta.
	 * Will return false if either parameter is null.
	 */
	public final boolean equalsEta(Time other, Time eta) {
		if (other == null || eta == null) return false;
		double d = (this.t * this.met.inUs()) - (other.t * other.met.inUs());
		if (Math.abs(d) <= (eta.t * eta.met.inUs())) return true;
		return false;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Time)) return false;
		Time other = (Time)obj;
		if ((this.t * this.met.inUs()) == (other.t * other.met.inUs())) return true;
		return false;
	}

	@Override
	public final int hashCode() {
		return (int)(this.t * this.met.inUs());
	}

	@Override
	public final String toString() {
		return t + " " + met.toString();
	}

	public int compareTo(Time o) {
		double ratio = getRatio(met, Metric.us);
		double thisint = t * ratio;
		double othint = o.t * ratio;
		if (thisint < othint) return -1;
		if (thisint == othint) return 0;
		return 1;
	}

	/**
	 * Returns a nicely formatted string, containing
	 * time intervals only within the selected metrics.
	 * For instance 5403 seconds will return the following
	 * string:<br/>
	 * 1h 30min 3s
	 * <br/>
	 * 
	 * @param from
	 * Highest allowed metric. Must be higher than <code>to</code>.
	 * @param to
	 * Lowest allowed metric.
	 * @return
	 */
	public String toNiceString(Metric from, Metric to) {
		StringBuilder sb = new StringBuilder();
		
		Metric[] mvalues = Metric.values();
		double total = t;
		for (int i = Metric.ordinalPosition(from), endpos = Metric.ordinalPosition(to); i >= endpos; i--) {
			Metric m = mvalues[i];
			double val = total * getRatio(met, m);
			if (val < 1.0 && i != endpos) continue;
			
			sb.append((int)val).append(m).append(' ');
			total -= ((int)val) * getRatio(m, met);
		}
		
		return sb.toString();
	}

}














