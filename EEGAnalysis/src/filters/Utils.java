package filters;

import java.util.ArrayList;
import java.util.List;

import main.Logger;

import filters.CutOff.AMPLITUDE;

public class Utils extends Filter {

	public static Coord minCoords(double[][] data) {
		double[] x = data[X];
		double[] y = data[Y];
		double xmin = x[0];
		double ymin = y[0];
		for (int i = 1; i < y.length; i++) {
			if (y[i] < ymin) {
				xmin = x[i];
				ymin = y[i];
			}
		}
		return new Coord(xmin, ymin);
	}

	public static Coord maxCoords(double[][] data) {
		double[] x = data[X];
		double[] y = data[Y];
		double xmax = x[0];
		double ymax = y[0];
		for (int i = 1; i < y.length; i++) {
			if (y[i] > ymax) {
				xmax = x[i];
				ymax = y[i];
			}
		}
		return new Coord(xmax, ymax);
	}

	public static double min(double[][] data) {
		double[] y = data[Y];
		double ymin = y[0];
		for (int i = 1; i < y.length; i++) {
			if (y[i] < ymin) {
				ymin = y[i];
			}
		}
		return ymin;
	}

	public static double max(double[][] data) {
		double[] y = data[Y];
		double ymax = y[0];
		for (int i = 1; i < y.length; i++) {
			if (y[i] > ymax) {
				ymax = y[i];
			}
		}
		return ymax;
	}

	public static Double ratioMaxMin(double[][] data) {
		double min = min(data);
		double max = max(data);
		if (min >= 0 || max <= 0) {
			System.out.println("The sign of the graph never changes");
			return null;
		}
		return Math.abs(max) / Math.abs(min);
	}

	/**
	 * @see {@link #lowAmplitude(double[][], double)}
	 * @see {@link #highAmplitude(double[][], double)}
	 * @param data
	 * @param threshold
	 * @param ampl
	 * @return filtered signal data
	 */
	public static double[][] amplitude(double[][] data, double threshold,
			AMPLITUDE ampl) {
		Double ratio = ratioMaxMin(data);
		if (ratio == null)
			return data;
		double min = -threshold / (ratio + 1);
		double max = threshold * ratio / (ratio + 1);
		double f[] = data[Y];
		List<Integer> toErase = new ArrayList<Integer>();

		if (ampl == AMPLITUDE.Low) {
			System.out.println("Low : min=" + min + "; max=" + max);
			for (int i = 0; i < f.length; i++) {
				if (f[i] < min || f[i] > max) {
					toErase.add(i);
				}
			}
		} else if (ampl == AMPLITUDE.High) {
			System.out.println("High : min=" + min + "; max=" + max);
			for (int i = 0; i < f.length; i++) {
				if (f[i] > min && f[i] < max) {
					toErase.add(i);
				}
			}
		}

		Logger.log("cutoff: " + toErase.size() + " samples droped");
		return removePoints(toErase, data);
	}

	/**
	 * Erases values from a signal data array
	 * 
	 * @param remove
	 *            list of values to removes
	 * @param from
	 *            signal data to be cleaned
	 * @return cleaned data array
	 */
	protected static double[][] removePoints(List<?> remove, double[][] from) {
		double[] xArr = new double[from[Y].length - remove.size()];
		double[] yArr = new double[from[Y].length - remove.size()];

		for (int i = 0, ii = 0; i < from[Y].length; i++) {
			if (!remove.contains(i)) {
				xArr[ii] = (double) i;
				yArr[ii] = from[Y][i];
				ii++;
			}
		}

		return new double[][] { xArr, yArr };
	}
}
