package filters.utils;

public class Complex {
	public double im;
	public double real;

	public Complex(double im, double real) {
		this.im =  im;
		this.real = real;
	}

	public static Complex[] fromFFTArray(double[] in) {
		Complex[] c = new Complex[in.length/2];
		for(int i=0; i<in.length/2; i++) {
			c[i] = new Complex(in[i*2], in[i*2+1]);
		}
		return c;
	}

	public static double[] toFFTArray(Complex[] c) {
		double[] d = new double[c.length * 2];
		for(int i=0; i<c.length; i++) {
			d[i*2] = c[i].im;
			d[i*2+1] = c[i].real;
		}
		return d;
	}

	public void zero() {
		im = 0;
		real = 0;
	}
}
