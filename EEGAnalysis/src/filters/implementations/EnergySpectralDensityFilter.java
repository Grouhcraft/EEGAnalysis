package filters.implementations;

import utils.Logger;
import utils.types.Complex;
import filters.Filter;

/**
 * Energy Spectral Density computation methods
 * @author knoodrake
 */
public class EnergySpectralDensityFilter extends Filter {

	/**
	 * Computes a simple Energy SD periodogram for the given 2D signal.
	 * Please don't forget that the length of the signal (in s) have to be
	 * at least 2 times the periods to catch ; and there are obviously the
	 * same kind of considerations regarding to the sampling rate !
	 * @param signal			the 2D signal, where index 0 is X, 1 is Y
	 * @param fs				the sampling rate
	 * @param freqUpperLimit	the frequency upper limit shown in the periodogram
	 * @return
	 * 		The periodogram plot data
	 */
	public static double[][] compute(
			double[][] signal,
			double fs,
			double freqLowerLimit,
			double freqUpperLimit,
			boolean logarithmicY
	){
		if(freqUpperLimit > fs/2) {
			throw new IllegalArgumentException("data sampling must be at least 2 times the upper frequency limit");
		}
		FFTFilter fft = new FFTFilter(signal);
		fft.forward();
		//fft.forwardFull();
		Logger.log("quick half-size real fft used");

		double originalDataLenght = signal[Y].length;

		int len = (int) ((originalDataLenght / fs) * freqUpperLimit);
		int from = (int) ((originalDataLenght / fs) * freqLowerLimit);
		double[] sumy = new double[len - from];
		double[] sumx = new double[len - from];
		for(int i=from; i<len; i++) {
			sumy[i-from] = logarithmicY
					? Math.log10(sq(module(fft.getData().y[i])))
					: sq(module(fft.getData().y[i]))
					;
		}

		for(int i=from; i<len; i++) {
			sumx[i-from] = i * fs / originalDataLenght;
		}
		return new double[][] { sumx, sumy };
	}

	/**
	 * Same as {@link #compute(double[][], double, double)} but in a raw fashion,
	 * meaning without logarithmic Y axis and with implicit X axis.
	 * Also, the complete frequency spectrum is returned.
	 * @param signal			the 2D signal, where just Y axis is passed,
	 * 							and X value is considered to be equal to Y array index.
	 * @param fs				the sampling rate
	 * @return
	 * 		The raw periodogram plot data
	 */
	public static double[] compute(double[] signal, double fs) {
		FFTFilter fft = new FFTFilter(signal);
		fft.forward();
		int fftLen = fft.getData().y.length;
		double[] sum = new double[fftLen];
		int i=0;
		for(Complex c : fft.getData().y) {
			sum[i] = sq(module(c));
			i++;
		}
		return sum;
	}
}
