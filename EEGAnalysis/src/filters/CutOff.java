package filters;

import java.util.ArrayList;
import java.util.List;

import main.Logger;

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
	 * @param from frequency range start (in Hz) 
	 * @param to frequency range end (in Hz)
	 * @param fs sampling rate
	 * @return a filtered data
	 */
	public static double[][] frequencyRange(double[][] data, int from, int to, double fs) {		
		FFT fft = new FFT(data);
		fft.forward();
		
		double signalLen = fft.getData().y.length;
		int hzFrom = (int) ((signalLen / fs) * from);
		int hzTo = (int) ((signalLen / fs) * to);
		
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
	private static double[][] amplitude(double[][] data, double threshold, AMPLITUDE ampl ) {
		double f[] = data[Y];
		List<Integer> toErease = new ArrayList<Integer>();
		
		int xx = 1;
		while(xx < f.length-2) {
			int x = xx;
			
			if(isAscending(x, f)) {			
				x = nextDrop(x, f);
				if((ampl == AMPLITUDE.High && Math.abs(f[xx] - f[x]) < threshold)
						|| (ampl == AMPLITUDE.Low && Math.abs(f[xx] - f[x]) > threshold)) {
					toErease = addRangeTo(toErease, xx, x);
					//for(int i=xx; i<x; i++) { data[Y][i] = 0; }
				}
				xx = x+1;
				continue;
			} 
			
			else if (isDropping(x, f)) {	
				x = nextAscent(x, f);
				if((ampl == AMPLITUDE.High && Math.abs(f[xx] - f[x]) < threshold) ||
						(ampl == AMPLITUDE.Low && Math.abs(f[xx] - f[x]) > threshold)) {
					toErease = addRangeTo(toErease, xx, x);
					//for(int i=xx; i<x; i++) { data[Y][i] = 0; }
				}
				xx = x+1;
				continue;
			} 
			
			else {
				x = nextNonFlat(x, f);
				xx = x+1;
				continue;
			}
		}
		Logger.log("cutoff: " + toErease.size() + " samples droped");
		return removePoints(toErease, data);
	}
}
