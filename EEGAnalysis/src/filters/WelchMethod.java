package filters;

import java.util.Arrays;

import javax.swing.text.Segment;

import main.Logger;
import main.MainWindow;

/**
 * Welch method is a method to computes a special kind of periodogram
 * averaged by time to reduce variance and therfore noise.
 * Multiple window functions are provided to compute the periodogram 
 * @author knoodrake
 *
 */
public class WelchMethod extends Filter {
	/**
	 * Represents the window function to use on 
	 * a data chunk. HANN is recommanded over SQUARE 
	 * function, which can bring aliasing and/or leakage
	 * @author knoodrake
	 *
	 */
	private enum WindowType {
		HANN, 
		SQUARE
	};
	
	/**
	 * Wrapper for {@link #compute(double[][], int, int, double, double, double, WindowType, Boolean)}
	 * Calling it with automatic parameters guessed or found in settings  
	 * @param data	The original data, index 0 is X, time, and index 1 is amplitude
	 * @param fs	Sampling rate
	 * @param lfq	Lower frequency (in Hz) to show up
	 * @param hfq	Higher frequency (in Hz) to show up
	 * @return		the periodogram plot data
	 */
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
	 * Computes the periodogram of given signal with welch method
	 * @param data				The original data, index 0 is X, time, and index 1 is amplitude
	 * @param segmentLength		Length of the data chuncks in samples
	 * @param overlapSize		Length of the data chuncks overlaping in samples
	 * @param fs				Sample rate
	 * @param freqLowerLimit	Lower frequency (in Hz) to show up
	 * @param freqUpperLimit	Higher frequency (in Hz) to show up
	 * @param windowType		{@link WindowType window function} to be applied to chunks
	 * @param logYScale			If true, magnitude is expressed in a dB scale
	 * @return					the periodogram plot data
	 * @see #compute(double[][], double, int, int)
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
		int from = (int) ((windowSize/ fs) * freqLowerLimit); 
		int to = (int) ((windowSize / fs) * freqUpperLimit); 
		double[][] powerFrequency = new double[][] { 
				new double[to - from], 
				new double[to - from] 
		};
		for(int i=from; i<to; i++) {
			powerFrequency[X][i-from] = ((double)i) * fs / windowSize;
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
			return getHannWindow(dataSegment, windowSize);
		}
	}
	
	private static double[] getSquareWindow(double[] dataSegment, double windowSize) {
		int windowStart = (int) ((dataSegment.length - windowSize) / 2);
		double[] window = Arrays.copyOfRange(dataSegment, windowStart, (int) (windowStart + windowSize));
		return window;
	}
	
	private static double[] getHannWindow(double[] dataSegment, double windowSize) {
		// https://en.wikipedia.org/wiki/Hanning_window
		double[] window = new double[(int) windowSize];
		int start = (int) (((double)dataSegment.length - windowSize)/2d);
		for(int i=start; i<windowSize + start; i++) {  
			double multiplier = 0.5d * ( 1d - Math.cos((2.0d * Math.PI * (double)(i-start)) / (windowSize-1d)) );
			window[i-start] = dataSegment[i] * multiplier;
		}
		window[0] = dataSegment[start] * 0.5d * ( 1d + Math.cos(0 / (windowSize-1d)) );
		return window;
	}	
}
