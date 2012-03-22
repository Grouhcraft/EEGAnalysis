package filters;

import java.util.Arrays;

import main.Logger;

public class WelchMethod extends Filter {
	private enum WindowType {
		HANN, 
		SQUARE
	};
	
	private static int numberOfSegments = 6;
	private static int lengthOfSegments = 1000;
	private static WindowType windowType = WindowType.HANN;
	
	static public double[][] compute(double[][] data, double fs, int lfq, int hfq) {
		int segLen;
		if(lengthOfSegments == 0) {
			segLen = (data[Y].length/2) / numberOfSegments;
			Logger.log("using " + numberOfSegments + " segments of size: " + segLen);
		} else {
			segLen = lengthOfSegments;
			Logger.log("using a segment length of " + lengthOfSegments);
		}
		return compute(data, 
				segLen, 
				segLen/2, 
				segLen, 
				fs, lfq, hfq);
		
	}
	
	/**
	 * @param data
	 * @param windowSize in samples
	 * @param overlapSize in samples
	 * @return
	 */
	static public double[][] compute(
			double[][] data, 
			int segmentLength, 
			int overlapSize, 
			int windowSize,
			double fs,
			double freqLowerLimit,
			double freqUpperLimit
			) {
		int signalLength = data[Y].length;
		int nSegments = (int) (signalLength / (segmentLength - overlapSize));
		int fftLen = windowSize/2;
		int from = (int) ((fftLen / fs) * freqLowerLimit); 
		int to = (int) ((fftLen / fs) * freqUpperLimit); 
		double[][] powerFrequency = new double[][] { 
				new double[to - from], 
				new double[to - from] 
		};
		for(int i=from; i<to; i++) {
			powerFrequency[X][i-from] = ((double)i) * fs / fftLen;
		}
		
		for(int i=0; i<nSegments; i++) {
			int segmentStart = i * (segmentLength - overlapSize);
			double[] dataSegment = Arrays.copyOfRange(data[Y], segmentStart, segmentStart + segmentLength);
			double[] dataWindow = getWindow(dataSegment, windowSize, windowType);

			double[] psd = EnergySpectralDensity.compute(dataWindow, fs);
			for(int ii=from; ii<to; ii++) {
				powerFrequency[Y][ii-from] += psd[ii];
			}
		}
		for(int i=0; i<powerFrequency[Y].length; i++) {
			powerFrequency[Y][i] = Math.log10(powerFrequency[Y][i] / ((double)nSegments));
		}
		
		return powerFrequency;
	}
	
	/**
	 * Performs a window function over given data and returns the result in a new array
	 * @param dataSegment
	 * @param windowSize
	 */
	private static double[] getWindow(double[] dataSegment, int windowSize, WindowType type) {
		if(type == WindowType.SQUARE) {
			return getSquareWindow(dataSegment, windowSize);
		} else {
			return getHannWindow(dataSegment, windowSize);
		}
	}
	
	private static double[] getSquareWindow(double[] dataSegment, int windowSize) {
		int windowStart = (dataSegment.length - windowSize) / 2;
		double[] window = Arrays.copyOfRange(dataSegment, windowStart, windowStart + windowSize);
		return window;
	}

	private static double[] getHannWindow(double[] dataSegment, int windowSize) {
		// https://en.wikipedia.org/wiki/Hanning_window
		double[] window = new double[windowSize];
		int start = (dataSegment.length - windowSize)/2;
		for(int i=start; i<windowSize + start; i++) { 
			window[i-start] = 0.5 * ( 1 - Math.cos((2 * Math.PI * dataSegment[i])/windowSize) ); 
		}
		return window;
	}	
}
