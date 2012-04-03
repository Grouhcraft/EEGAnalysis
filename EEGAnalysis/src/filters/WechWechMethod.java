package filters;

import main.MainWindow;
import main.utils.Logger;
import filters.utils.ChunkedData;
import filters.utils.Filter;
import filters.windowsFunctions.HannWindow;
import filters.windowsFunctions.SquareWindow;
import filters.windowsFunctions.Window;

/**
 * Welch method is a method to computes a special kind of periodogram
 * averaged by time to reduce variance and therfore noise.
 * Multiple window functions are provided to compute the periodogram
 * @author knoodrake
 *
 */
public class WechWechMethod extends Filter {
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
			Window win,
			Boolean logYScale
			) {

		ChunkedData chunked = new ChunkedData(data[Y], (int) fs, segmentLength, win.getRecommandedOverlappingSize(segmentLength), true);
		Logger.log("Computed R size=" + chunked.getOverlapSize());
		Logger.log("Computed K segments=" + chunked.getNumberOfChunk());
		int from = (int) ((chunked.getOverlapSize()/ fs) * freqLowerLimit);
		int to = (int) ((chunked.getOverlapSize() / fs) * freqUpperLimit);
		double[][] powerFrequency = new double[][] {
				new double[to - from],
				new double[to - from]
		};
		for(int i=from; i<to; i++) powerFrequency[X][i-from] = i * fs / chunked.getOverlapSize();

		while(chunked.hasNextChunk()) {
			double[] psd = EnergySpectralDensity.compute(chunked.getChunk(), fs);
			for(int ii=from; ii<to; ii++) powerFrequency[Y][ii-from] += psd[ii];
			chunked.nextChunk();
		}

		if(logYScale) for(int i=0; i<powerFrequency[Y].length; i++)
				powerFrequency[Y][i] = Math.log10(powerFrequency[Y][i] / chunked.getNumberOfChunk());

		else for(int i=0; i<powerFrequency[Y].length; i++)
				powerFrequency[Y][i] = powerFrequency[Y][i] / chunked.getNumberOfChunk();

		return powerFrequency;
	}
}
