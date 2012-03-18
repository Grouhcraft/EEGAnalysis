package filters;

import java.util.Arrays;

public class WelchMethod extends Filter {

	private static int defaultSegmentLength = 16;
	private static int defaultOverlappSize = 8;
	private static int defaultWindowSize = 8;
	
	static public double[][] compute(double[][] data) {
		return compute(data, defaultSegmentLength, defaultOverlappSize, defaultWindowSize);
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
			double[] fftOfDataWindow = FFT.forward(dataWindow);
			double squaredMag = computeSquaredMagnitude(fftOfDataWindow);
			
			powerFrequency[X][i] = i;
			powerFrequency[Y][i] = squaredMag/(nSegments * sumOfTheFFT(fftOfDataWindow));
		}
		return powerFrequency;
	}

	private static double sumOfTheFFT(double[] dft) {
		double sum = 0;
		for(int i=0; i<dft.length; i++) {
			sum += Math.abs(dft[i]); 
		}
		return sum;
	}

	private static double computeSquaredMagnitude(double[] dft) {
		double squaredMag = 0;
		for(int i=0; i<dft.length; i++) {
			squaredMag += dft[i]*dft[i] + i*i;
		}
		return squaredMag;
	}
	
}
