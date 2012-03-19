package filters;

import java.util.List;

/**
 * Abstract layer for the filters
 * @author knoodrake
 *
 */
public abstract class Filter {
	protected final static int X = 0;
	protected final static int Y = 1;
	
	protected static double sq(double x) {
		return x * x;
	}
	
	protected static double abs(double x) {
		return Math.abs(x);
	}
	
	protected static double sqrt(double x) {
		return Math.sqrt(x);
	}
	
	protected static double module(Complex c) {
		return sqrt(sq(c.im) + sq(c.real));
	}
	
	/**
	 * Erases values from a signal data array
	 * @param remove list of values to removes
	 * @param from signal data to be cleaned
	 * @return cleaned data array
	 */
	protected static double[][] removePoints(List<?> remove, double[][] from) {
		double[] xArr = new double[from[Y].length - remove.size()];
		double[] yArr = new double[from[Y].length - remove.size()];
		
		for(int i=0, ii=0; i<from[Y].length; i++) {
			if(!remove.contains(i)) {
				xArr[ii] = (double) i;
				yArr[ii] = from[Y][i];
				ii++;
			}
		}
		
		return new double[][] { xArr, yArr };
	}
	
	/**
	 * Advance the position to the next point where the curve start to decline
	 * @param pos current position in the data array
	 * @param data
	 * @return the new position
	 */
	protected static int nextDrop(int pos, double[] data) {
		while((pos+1 < data.length-1) && (data[pos] < data[pos+1] || data[pos] == data[pos+1])) {pos++;}
		return pos;
	}
	
	/**
	 * Advance the position to the next point where the curve start to go up
	 * @param pos current position in the data array
	 * @param data
	 * @return the new position
	 */
	protected static int nextAscent(int pos, double[] data) {
		while((pos+1 < data.length-1) && (data[pos] > data[pos+1] || data[pos] == data[pos+1])) {pos++;}
		return pos;
	}
	
	/**
	 * Advance the position to the next point where the curve isn't flat (growing or declining)
	 * @param pos
	 * @param data
	 * @return
	 */
	protected static int nextNonFlat(int pos, double[] data) {
		while((pos+1 < data.length-1) && (data[pos] == data[pos+1])) {pos++;}
		return pos;
	}
	
	/**
	 * Adds a range of integers to an integer List
	 * @param list
	 * @param from range start
	 * @param to range end
	 * @return the filled list
	 */
	protected static List<Integer> addRangeTo(List<Integer> list, int from, int to) {
		for(int i=from; i<=to; i++) {
			list.add(i);
		}
		return list;
	}
	
	/**
	 * Returns true if the curve is ascending at the given position
	 * @param pos
	 * @param data
	 * @return
	 */
	protected static boolean isAscending(int pos, double[] data) {
		return data[pos] < data[pos+1];
	}
	
	/**
	 * Returns true if the curve is dropping at the given position
	 * @param pos
	 * @param data
	 * @return
	 */
	protected static boolean isDropping(int pos, double[] data) {
		return data[pos] > data[pos+1];
	}
}
