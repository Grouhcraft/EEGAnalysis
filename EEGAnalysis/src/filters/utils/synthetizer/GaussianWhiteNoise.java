package filters.utils.synthetizer;

import java.util.Random;

import filters.utils.Filter;

public class GaussianWhiteNoise {
	private static final int X = 0;
	private static final int Y = 1;
	
	public static double[][] addNoise(double[][] data, int amount) {
		return new double[][] { data[X].clone(), addNoise(data[Y], amount) };	
	}
	
	public static double[] addNoise(double[] data, int amount) {
		double x;
		double mean = Filter.average(data);
		double stdDev = Filter.stdDeviation(data);
		double[] noised = new double[data.length];
		Random rand = new Random();
		for(int i=0; i<data.length; i++) {
			x = rand.nextGaussian();
			x = mean + (stdDev * amount) * x;
			noised[i] = (data[i] + x)/2;
		}
		return noised;
	}
	
}
