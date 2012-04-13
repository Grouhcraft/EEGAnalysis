package filters.implementations;

import utils.ChunkedData;
import utils.Logger;
import filters.Filter;
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
public class WelchMethodFilter extends Filter {
	/**
	 * Computes the periodogram of given signal with welch method
	 * @param data	The original data, index 0 is X, time, and index 1 is amplitude
	 * @param fs	Sampling rate
	 * @param lfq	Lower frequency (in Hz) to show up
	 * @param hfq	Higher frequency (in Hz) to show up
	 * @param segLen desired window length
	 * @param useSquareWindow if true, uses square window instead of Hann
	 * @param logYScale if true, amplitude is expressed in dB
	 * @return		the periodogram plot data
	 */
	static public double[][] compute(
			double[][] data, 
			double fs, 
			int lfq, 
			int hfq,
			int segLen,
			boolean useSquareWindow,
			boolean logYScale
			) {
		Window w = useSquareWindow ? new SquareWindow() : new HannWindow(); 
		return compute(data, segLen, fs, lfq, hfq, w, logYScale);
	}

	/**
	 * Computes the periodogram of given signal with welch method
	 * @param data				The original data, index 0 is X, time, and index 1 is amplitude
	 * @param segmentLength		Length of the data chuncks in samples
	 * @param fs				Sample rate
	 * @param freqLowerLimit	Lower frequency (in Hz) to show up
	 * @param freqUpperLimit	Higher frequency (in Hz) to show up
	 * @param win				the {@link Window} function used
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

		// avoid aliasing
		while(data[Y].length % segmentLength != 0) segmentLength++;

		ChunkedData chunked = new ChunkedData(data[Y], (int) fs, segmentLength, win.getRecommandedOverlappingSize(segmentLength), true);
		Logger.log("Computed R size=" + chunked.getOverlapSize());
		Logger.log("Computed K segments=" + chunked.getNumberOfChunk());
		
		// Limit the displayed spectrum
		int from = (int) ((chunked.getOverlapSize()/ fs) * freqLowerLimit);
		int to = (int) ((chunked.getOverlapSize() / fs) * freqUpperLimit);
		double[][] powerFrequency = {
				new double[to - from],
				new double[to - from]
		};
		
		// inserts frequencies (x-axis)
		for(int i=from; i<to; i++) powerFrequency[X][i-from] = (i * fs / chunked.getOverlapSize())/2.;

		// computes the spectral density of each chunk
		while(chunked.hasNextChunk()) {
			double[] psd = EnergySpectralDensityFilter.compute(chunked.getChunk(), fs);
			for(int ii=from; ii<to; ii++) powerFrequency[Y][ii-from] += psd[ii];
			chunked.nextChunk();
		}

		// averages all the density spectrums into one
		if(logYScale) for(int i=0; i<powerFrequency[Y].length; i++)
				powerFrequency[Y][i] = Math.log10(powerFrequency[Y][i] / chunked.getNumberOfChunk());

		else for(int i=0; i<powerFrequency[Y].length; i++)
				powerFrequency[Y][i] = powerFrequency[Y][i] / chunked.getNumberOfChunk();

		return powerFrequency;
	}
}
