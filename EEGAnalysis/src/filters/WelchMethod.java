package filters;

import java.util.Arrays;

public class WelchMethod extends Filter {

	private static int defaultSegmentLength = 100;
	
	static public double[][] compute(double[][] data) {
		return compute(data, defaultSegmentLength, defaultSegmentLength/2, defaultSegmentLength/2);
	}
	
	/**
	 * @param data
	 * @param windowSize in samples
	 * @param overlapSize in samples
	 * @return
	 */
	static public double[][] compute(double[][] data, int segmentLength, int overlapSize, int windowSize) {
		int signalLength = data[Y].length;
		int nSegments = (int) (signalLength / (segmentLength - overlapSize));
		double[][] powerFrequency = new double[][] { 
				new double[nSegments], 
				new double[nSegments] 
		};
		
		for(int i=0; i<nSegments; i++) {
			int segmentStart = i * (segmentLength - overlapSize);
			double[] dataSegment = Arrays.copyOfRange(data[Y], segmentStart, segmentStart + segmentLength);
			int windowStart = (segmentLength - windowSize) / 2;
			double[] dataWindow = Arrays.copyOfRange(dataSegment, windowStart, windowStart + windowSize);

			double[] psd = EnergySpectralDensity.compute(dataWindow);
			double squaredMag = 0;
			for(double d : psd) {
				squaredMag += d;
			}
			squaredMag /= psd.length;
			
			powerFrequency[X][i] = i;
			powerFrequency[Y][i] = squaredMag;
		}
		return powerFrequency;
	}	
}
