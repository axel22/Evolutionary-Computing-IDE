package hr.fer.zemris.ga_framework.algorithms.tsp;

import hr.fer.zemris.ga_framework.model.ISerializable;
import hr.fer.zemris.ga_framework.model.impl.SimpleSerializable;

import java.util.Arrays;


/**
 * Object describing the number of cities and their
 * mutual distances.
 * 
 * @author Axel
 *
 */
public class CityTable extends SimpleSerializable {

	/* static fields */

	/* private fields */
	private int numcities;
	private int wdt, hgt;
	private double[] distances;
	private double[] positions;

	/* ctors */
	
	public CityTable() {
		this(1, 620, 330);
	}
	
	public CityTable(int cityNumber, int width, int height) {
		numcities = cityNumber;
		wdt = width;
		hgt = height;
		if (numcities < 500) distances = new double[numcities * numcities];
		else distances = null;
		positions = new double[numcities * 2];
	}
	
	/**
	 * Copy ctor.
	 * @param citytab
	 */
	public CityTable(CityTable citytab) {
		numcities = citytab.numcities;
		wdt = citytab.wdt;
		hgt = citytab.hgt;
		if (citytab.distances != null) {
			distances = Arrays.copyOf(citytab.distances, citytab.distances.length);
		} else distances = null;
		positions = Arrays.copyOf(citytab.positions, citytab.positions.length);
	}

	/* methods */
	
	public ISerializable deserialize(String s) {
		CityTable ntable = new CityTable();
		
		ntable.deserializeInternal(s);
		
		return ntable;
	}
	
	private void deserializeInternal(String s) {
		String[] sf = s.split(":");
		if (sf.length != 5) throw new IllegalArgumentException("String " + s + " not a valid serialization string.");
		try {
			numcities = Integer.parseInt(sf[0]);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("String must begin with city number - wrong string: " + s, e);
		}
		
		// distances
		if (numcities < 500) {
			distances = new double[numcities * numcities];
			sf[1] = sf[1].substring(1, sf[1].length() - 1);
			String[] dsf = sf[1].split(",");
			try {
				for (int i = 0; i < distances.length; i++) {
					distances[i] = Double.parseDouble(dsf[i]);
				}
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Distance invalid in string: " + s);
			}
		} else distances = null;
		
		// positions
		positions = new double[numcities * 2];
		sf[2] = sf[2].substring(1, sf[2].length() - 1);
		String[] dsf = sf[2].split(",");
		try {
			for (int i = 0; i < positions.length; i++) {
				positions[i] = Double.parseDouble(dsf[i]);
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("City position invalid in string: " + s);
		}
		try {
			wdt = Integer.parseInt(sf[3]);
			hgt = Integer.parseInt(sf[4]);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid width or height - wrong string: " + s, e);
		}
	}
	
	public double get(int f, int s) {
		if (distances != null) return distances[f * numcities + s];
		else {
			double dist = Math.sqrt((positions[f * 2] - positions[s * 2]) * (positions[f * 2] - positions[s * 2]) +
					(positions[f * 2 + 1] - positions[s * 2 + 1]) * (positions[f * 2 + 1] - positions[s * 2 + 1]));
			dist = ((int)(dist * 100)) / 100;
			return dist;
		}
	}
	
	public void set(int city, double xpos, double ypos) {
		positions[city * 2] = xpos;
		positions[city * 2 + 1] = ypos;
		
		// calculate distances
		if (distances != null) for (int i = 0; i < numcities; i++) {
			double dist = Math.sqrt((positions[i * 2] - xpos) * (positions[i * 2] - xpos) +
					(positions[i * 2 + 1] - ypos) * (positions[i * 2 + 1] - ypos));
			dist = ((int)(dist * 100)) / 100;
			distances[city * numcities + i] = dist;
			distances[i * numcities + city] = dist;
		}
	}
	
	public int getWidth() {
		return wdt;
	}
	
	public int getHeight() {
		return hgt;
	}
	
	public int getCityNum() {
		return numcities;
	}
	
	public double getCityX(int num) {
		return positions[num * 2];
	}
	
	public double getCityY(int num) {
		return positions[num * 2 + 1];
	}
	
	public String serialize() {
		return numcities + ":" + ((distances != null) ? Arrays.toString(distances) : "") + 
		":" + Arrays.toString(positions) +
		":" + wdt + ":" + hgt;
	}

	@Override
	public String toString() {
		if (numcities == 1) return "[1 city, " + wdt + "x" + hgt + "]";
		return "[" + numcities + " cities, " + wdt + "x" + hgt + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CityTable)) return false;
		CityTable that = (CityTable)obj;
		return this.hgt == that.hgt && this.wdt == that.wdt && this.numcities == that.numcities
		&& Arrays.equals(this.positions, that.positions);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(positions) << 16 + hgt << 10 + wdt << 4 + numcities;
	}

	public ISerializable deepCopy() {
		return new CityTable(this);
	}
	
}














