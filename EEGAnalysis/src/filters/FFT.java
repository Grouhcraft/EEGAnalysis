package filters;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

/**
 * Computes <a href="https://en.wikipedia.org/wiki/Discrete_Fourier_transform">DFT</a> 
 * through <a href="https://en.wikipedia.org/wiki/Fast_Fourier_transform">FFT</a> 
 * using the JTransforms library
 * @author knoodrake
 *
 */
public class FFT extends Filter {
	
	/**
	 * Computes the DFT of the Time-based signal data 
	 * @param data 2 dimensions signal data
	 * @return the DFT data
	 */
	public static double[][] forward(double[][] data) {
		DoubleFFT_1D fft = new DoubleFFT_1D(data[Y].length);
		double[] y = data[Y].clone();
		fft.realForward(y);
		return new double[][] { data[X].clone(), y };
	}
	
	/**
	 * Computes the signal data from the it's DFT 
	 * @param data 2 dimensions signal data, within which Y-axis (index 1) is a DFT
	 * @return the original signal data
	 */
	public static double[][] inverse(double[][] data) {
		DoubleFFT_1D fft = new DoubleFFT_1D(data[Y].length);
		double[] y = data[Y];
		fft.realInverse(y, false);
		return new double[][] { data[X].clone(), y };
	}

	public static double[] forward(double[] data) {
		DoubleFFT_1D fft = new DoubleFFT_1D(data.length);
		double[] y = data.clone();
		fft.realForward(y);
		return y;
	}
}
