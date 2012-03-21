package filters;

import java.util.Arrays;

public class WelchMethod extends Filter {

	private static int defaultSegmentLength = 10000;
	/*
	static public double[][] compute(double[][] data) {
		//return compute(data, defaultSegmentLength, defaultSegmentLength/2, defaultSegmentLength/2);
		return computeTest(data, defaultSegmentLength, defaultSegmentLength/2, defaultSegmentLength/2);
		
	}
	*/
	
	/**
	 * @param data
	 * @param windowSize in samples
	 * @param overlapSize in samples
	 * @return
	 */
	/*
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
	
	static public double[][] computeTest(double[][] data, int segmentLength, int overlapSize, int windowSize) {
		int signalLength = data[Y].length;
		int nSegments = (int) (signalLength / (segmentLength - overlapSize));
		double[][] powerFrequency = new double[][] { 
				new double[windowSize/2 -1], 
				new double[windowSize/2 -1] 
		};
		for(int i=0; i<powerFrequency[Y].length; i++) {
			powerFrequency[X][i] = i*2;
		}
		
		for(int i=0; i<nSegments; i++) {
			int segmentStart = i * (segmentLength - overlapSize);
			double[] dataSegment = Arrays.copyOfRange(data[Y], segmentStart, segmentStart + segmentLength);
			int windowStart = (segmentLength - windowSize) / 2;
			double[] dataWindow = Arrays.copyOfRange(dataSegment, windowStart, windowStart + windowSize);

			double[] psd = EnergySpectralDensity.compute(dataWindow);
			for(int ii=0; ii<powerFrequency[Y].length; ii++) {
				powerFrequency[Y][ii] += psd[ii] / windowSize;
			}
		}
		for(int i=0; i<powerFrequency[Y].length; i++) {
			powerFrequency[Y][i] /= nSegments;
		}
		
		return powerFrequency;
	}	
	*/
}
