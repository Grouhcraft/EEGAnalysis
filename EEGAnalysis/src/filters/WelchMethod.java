package filters;

import java.util.Arrays;

import windowsFunctions.HannWindow;
import windowsFunctions.SquareWindow;
import windowsFunctions.Window;

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
		
		Window w = (MainWindow.getPrefs().getBoolean(MainWindow.PREF_WELCH_USE_SQ_WIN, false))
				? new SquareWindow() : new HannWindow() ;
		
		boolean logYScale = (MainWindow.getPrefs().getBoolean(MainWindow.PREF_PERIO_USE_DBSCALE, false))
				? true : false;
		
		return compute(data, segLen, fs, lfq, hfq, w, logYScale);
		
	}
	
	private static int getNumberOfSegments(double dataLen, double segmentLen, double overlapSize) {
		return (int) Math.floor(((dataLen - segmentLen) / overlapSize) + 1d);
	}
	
	/**
	 * Computes the periodogram of given signal with welch method
	 * @param data				The original data, index 0 is X, time, and index 1 is amplitude
	 * @param segmentLength		Length of the data chuncks in samples
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
			double fs,
			double freqLowerLimit,
			double freqUpperLimit,
			Window window,
			Boolean logYScale
			) {
		
		window.setData(data[Y]);
		int signalLength = data[Y].length;
		int overlapSize = window.getRecommandedOverlappingSize(); 
		int nSegments = getNumberOfSegments(signalLength, segmentLength, overlapSize);
		Logger.log("Computed R size=" + overlapSize);
		Logger.log("Computed K segments=" + nSegments);
		int from = (int) ((overlapSize/ fs) * freqLowerLimit); 
		int to = (int) ((overlapSize / fs) * freqUpperLimit);
		
		int fromToLen = to - from;
		Logger.log("fromToLen=" + fromToLen);
		double[][] powerFrequency = new double[][] { 
				new double[to - from], 
				new double[to - from] 
		};
		for(int i=from; i<to; i++) powerFrequency[X][i-from] = ((double)i) * fs / overlapSize;
		
		for(int i=0; i<nSegments; i++) {
			int segmentStart = i * (segmentLength - overlapSize);
			double[] dataSegment = Arrays.copyOfRange(data[Y], segmentStart, segmentStart + segmentLength);
			double[] dataWindow = window.setData(dataSegment).get(overlapSize);

			double[] psd = EnergySpectralDensity.compute(dataWindow, fs);
			for(int ii=from; ii<to; ii++) powerFrequency[Y][ii-from] += psd[ii];
		}

		if(logYScale) for(int i=0; i<powerFrequency[Y].length; i++)
				powerFrequency[Y][i] = Math.log10(powerFrequency[Y][i] / ((double)nSegments));
		
		else for(int i=0; i<powerFrequency[Y].length; i++)
				powerFrequency[Y][i] = powerFrequency[Y][i] / ((double)nSegments); 
	
		return powerFrequency;
	}
}
