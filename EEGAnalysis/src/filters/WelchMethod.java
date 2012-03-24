package filters;

import java.util.Arrays;

import javax.swing.text.Segment;

import main.Logger;
import main.MainWindow;

public class WelchMethod extends Filter {
	private enum WindowType {
		HANN, 
		SQUARE
	};
	
	static public double[][] compute(double[][] data, double fs, int lfq, int hfq) {
		int segLen = MainWindow.getPrefs().getInt(MainWindow.PREF_WELCH_SEG_LENGTH, 1000);
		
		WindowType w = (MainWindow.getPrefs().getBoolean(MainWindow.PREF_WELCH_USE_SQ_WIN, false))
				? WindowType.SQUARE : WindowType.HANN ;
		
		boolean logYScale = (MainWindow.getPrefs().getBoolean(MainWindow.PREF_PERIO_USE_DBSCALE, false))
				? true : false;
		
		Logger.log("segLen=" + segLen);
		
		return compute(data, 
				segLen, segLen/2, 
				fs, lfq, hfq, w, logYScale
				);
		
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
			double fs,
			double freqLowerLimit,
			double freqUpperLimit,
			WindowType windowType,
			Boolean logYScale
			) {
		int signalLength = data[Y].length;
		int nSegments = (int) (signalLength / (segmentLength - overlapSize));		
		double windowSize;
		if(windowType == WindowType.HANN) {
			if(segmentLength % 2 == 0)
				windowSize = ((double)segmentLength) / 2d;
			else
				windowSize = (((double)segmentLength) - 1d) / 2d;
		} else { // SQUARE
			windowSize = segmentLength;
		}
		int fftLen = (int) (windowSize/2);
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

		if(logYScale) {
			for(int i=0; i<powerFrequency[Y].length; i++) {
				powerFrequency[Y][i] = Math.log10(powerFrequency[Y][i] / ((double)nSegments));
			}
		} else {
			for(int i=0; i<powerFrequency[Y].length; i++) {
				powerFrequency[Y][i] = powerFrequency[Y][i] / ((double)nSegments); 
			}
		}
		
		return powerFrequency;
	}
	
	/**
	 * Performs a window function over given data and returns the result in a new array
	 * @param dataSegment
	 * @param windowSize
	 */
	private static double[] getWindow(double[] dataSegment, double windowSize, WindowType type) {
		if(type == WindowType.SQUARE) {
			return getSquareWindow(dataSegment, windowSize);
		} else {
			return getHannWindowTEST(dataSegment, windowSize);
		}
	}
	
	private static double[] getSquareWindow(double[] dataSegment, double windowSize) {
		int windowStart = (int) ((dataSegment.length - windowSize) / 2);
		double[] window = Arrays.copyOfRange(dataSegment, windowStart, (int) (windowStart + windowSize));
		return window;
	}
	
	private static double[] getHannWindowTEST(double[] dataSegment, double windowSize) {
		// https://en.wikipedia.org/wiki/Hanning_window
		double[] window = new double[dataSegment.length];
		
		int start = (int) (((double)dataSegment.length - windowSize)/2d);
		for(int i=0; i<start; i++) 	dataSegment[i] = 0;
		for(int i= start + (int)windowSize; i< dataSegment.length; i++) dataSegment[i] = 0;
		
		for(int i=0; i<dataSegment.length; i++) {  
			window[i] = 0.5d * ( 1d - Math.cos((2.0d * Math.PI * dataSegment[i]) / (windowSize-1d)) );
		}
		return window;
	}	
	
	private static double[] getHannWindowORIGINAL(double[] dataSegment, double windowSize) {
		// https://en.wikipedia.org/wiki/Hanning_window
		double[] window = new double[(int) windowSize];
		int start = (int) (((double)dataSegment.length - windowSize)/2d);
		for(int i=start; i<windowSize + start; i++) {  
			window[i-start] = 0.5d * ( 1d - Math.cos((2.0d * Math.PI * dataSegment[i]) / (windowSize-1d)) );
		}
		Logger.log("	ws= " + window.length + ", st= " + start);
		return window;
	}	
}
