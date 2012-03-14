package filters;

import java.util.List;

public abstract class Filter {
	protected final static int X = 0;
	protected final static int Y = 1;
	
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
	
	protected static int nextDrop(int pos, double[] data) {
		while((pos+1 < data.length-1) && (data[pos] < data[pos+1] || data[pos] == data[pos+1])) {pos++;}
		return pos;
	}
	
	protected static int nextAscent(int pos, double[] data) {
		while((pos+1 < data.length-1) && (data[pos] > data[pos+1] || data[pos] == data[pos+1])) {pos++;}
		return pos;
	}
	
	protected static int nextNonFlat(int pos, double[] data) {
		while((pos+1 < data.length-1) && (data[pos] == data[pos+1])) {pos++;}
		return pos;
	}
	
	protected static List<Integer> addRangeTo(List<Integer> list, int from, int to) {
		for(int i=from; i<=to; i++) {
			list.add(i);
		}
		return list;
	}
	
	protected static boolean isAscending(int pos, double[] data) {
		return data[pos] < data[pos+1];
	}
	
	protected static boolean isDropping(int pos, double[] data) {
		return data[pos] > data[pos+1];
	}
}
