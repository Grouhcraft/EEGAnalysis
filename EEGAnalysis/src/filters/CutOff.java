package filters;

import java.util.ArrayList;
import java.util.List;

import filters.utils.FFT;
import filters.utils.Filter;
import filters.utils.FrequencyRange;

import main.utils.Logger;

/**
 * Filters the signal by Amplitude or Frequency
 * @author knoodrake
 *
 */
public class CutOff extends Filter {
	protected static enum AMPLITUDE { High, Low }
	
	/**
	 * Filters the signal to only keep the given frequency range
	 * @param data data to filters, modified by copy (original won't be altered)
	 * @param fr frequency range (in Hz) 
	 * @param fs sampling rate
	 * @return a filtered data
	 */
	public static double[][] frequencyRange(double[][] data, FrequencyRange fr, double fs) {		
		FFT fft = new FFT(data);
		fft.forward();
		
		double signalLen = fft.getData().y.length;
		int hzFrom = (int) ((signalLen / fs) * fr.getLower());
		int hzTo = (int) ((signalLen / fs) * fr.getHigher());
		
		for(int i=0; i<hzFrom; i++) {
			fft.getData().y[i].zero();
		}
		for(int i=hzTo; i<fft.getData().y.length; i++) {
			fft.getData().y[i].zero();
		}
		fft.inverse();
		return fft.getInitialData();
	}
	
	/**
	 * Apply a Low cut-off filter to the data
	 * @param data 
	 * @param threshold above which data will be erased
	 * @return the filtered data, which usually contains less samples, since some have been erased
	 * @see {@link #highAmplitude(double[][], double)}
	 */
	public static double[][] lowAmplitude(double[][] data, double threshold) {
		return amplitude(data, threshold, AMPLITUDE.Low); 
	}
	
	/**
	 * Apply a High cut-off filter to the data
	 * @param data 
	 * @param threshold bellow which data will be erased
	 * @return the filtered data, which usually contains less samples, since some have been erased
	 * @see {@link #lowAmplitude(double[][], double)}
	 */
	public static double[][] highAmplitude(double[][] data, double threshold) {
		return amplitude(data, threshold, AMPLITUDE.High);
	}
	
	/**
	 * @see {@link #lowAmplitude(double[][], double)}
	 * @see {@link #highAmplitude(double[][], double)}
	 * @param data
	 * @param threshold
	 * @param ampl
	 * @return filtered signal data
	 */
	/**
	 * @see {@link #lowAmplitude(double[][], double)}
	 * @see {@link #highAmplitude(double[][], double)}
	 * @param data
	 * @param threshold
	 * @param ampl
	 * @return filtered signal data
	 */
	public static double[][] amplitude(double[][] data, double threshold,
			AMPLITUDE ampl) {
		Double ratio = ratioMaxMin(data);
		if (ratio == null)
			return data;
		double min = -threshold / (ratio + 1);
		double max = threshold * ratio / (ratio + 1);
		double f[] = data[Y];
		List<Integer> toErase = new ArrayList<Integer>();

		if (ampl == AMPLITUDE.Low) {
			System.out.println("Low : min=" + min + "; max=" + max);
			for (int i = 0; i < f.length; i++) {
				if (f[i] < min || f[i] > max) {
					toErase.add(i);
				}
			}
		} else if (ampl == AMPLITUDE.High) {
			System.out.println("High : min=" + min + "; max=" + max);
			for (int i = 0; i < f.length; i++) {
				if (f[i] > min && f[i] < max) {
					toErase.add(i);
				}
			}
		}

		Logger.log("cutoff: " + toErase.size() + " samples droped");
		return removePoints(toErase, data);
	}
}
