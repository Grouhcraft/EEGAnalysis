package filters.utils;

import java.util.Arrays;

import main.utils.Logger;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

/**
 * Computes <a href="https://en.wikipedia.org/wiki/Discrete_Fourier_transform">DFT</a>
 * through <a href="https://en.wikipedia.org/wiki/Fast_Fourier_transform">FFT</a>
 * using the JTransforms library
 * @author knoodrake
 *
 */
public class FFT extends Filter {

	public class Data {
		public double[] x;
		public Complex[] y;

		public double[] initialX;
		public double[] initialY;

		public Data(double[] x, Complex[] y) {
			this.x = x;
			this.y = y;
		}

		public Data(double[] initialData) {
			initialY = initialData;
		}

		public Data(double[][] initialData) {
			initialX = initialData[X];
			initialY = initialData[Y];
		}

		private Data() {}
	}
	private Data data = new Data();

	public FFT() {}

	public FFT(double[] data) {
		this.data.initialY = data.clone();
	}

	public FFT(double[][] data) {
		this.data.initialX = data[X].clone();
		this.data.initialY = data[Y].clone();
	}

	public FFT forwardFull() {
		if(data.initialY.length % 2 > 0)
			Logger.log("Warning, data length is odd");
		DoubleFFT_1D fft = new DoubleFFT_1D(data.initialY.length);
		double[] yData = Arrays.copyOf(data.initialY, data.initialY.length*2);
		fft.realForwardFull(yData);
		if(data.initialX != null) {
			//data.x = oneOfTwo(data.initialX);
			data.x = data.initialX.clone();
		}
		data.y = Complex.fromFFTArray(yData);
		return this;
	}

	/**
	 * Compute the DFT of the signal
	 * @return itself
	 */
	public FFT forward() {
		DoubleFFT_1D fft = new DoubleFFT_1D(data.initialY.length);
		double[] yData = data.initialY;
		fft.realForward(yData);
		if(data.initialX != null) {
			data.x = oneOfTwo(data.initialX);
		}
		data.y = Complex.fromFFTArray(yData);
		return this;
	}

	public FFT inverse() {
		data.initialY = Complex.toFFTArray(data.y);
		new DoubleFFT_1D(data.initialY.length).realInverse(data.initialY, false);
		return this;
	}

	private double[] oneOfTwo(double[] from) {
		double[] half = new double[from.length/2];
		for(int i=0; i<from.length/2; i++) {
			half[i] = from[i*2];
		}
		return half;
	}

	public Data getData() {
		return data;
	}


	public void setData(Data data) {
		this.data = data;
	}

	public double[][] getInitialData() {
		return new double[][] {
			data.initialX,
			data.initialY
		};
	}
}
