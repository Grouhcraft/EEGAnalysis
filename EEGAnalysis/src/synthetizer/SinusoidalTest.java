package synthetizer;

import static org.junit.Assert.*;

import main.Logger;

import org.junit.Test;

public class SinusoidalTest {

	@Test
	public void testGenerate() {
		double[][] result = Sinusoidal.generate(1, 1, 1, 0.5, 1);
		Logger.log(result);
		fail("not yet implemented");
	}

	@Test
	public void testMerge() {
		double[][] a = {
			new double[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 },
			new double[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 }
		};
		double[][] b = {
			new double[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 },
			new double[] { 2, 2, 3, 4, 5, 6, 7, 8, 9 }
		};
		double[][] normal_r = {
			new double[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 },
			new double[] { 1, 1.5, 2.5, 3.5, 4.5, 5.5, 6.5, 7.5, 8.5 }
		};
		assertArrayEquals(normal_r, Sinusoidal.merge(a, b));
	}

	@Test
	public void testInterpolateX2DoubleArrayArray() {
		double[][] src = new double[][] {
				new double[] { 0,1,2,3 },
				new double[] { 3,4,5,6 }
				};
		double[][] expected = new double[][] {
				new double[] { 0,0.5,1,1.5,2,2.5,3,3 },
				new double[] { 3,3.5,4,4.5,5,5.5,6,6 }
		};
		double[][] r = Sinusoidal.interpolateX2(src);
		assertArrayEquals(expected, r);
	}

	@Test
	public void testInterpolateX2DoubleArray() {
		double[] src = new double[] {3,4,5,6};
		double[][] expected = new double[][] {
				new double[] { 3,3.5,4,4.5,5,5.5,6,6 }
		};
		double[][] r = new double[][] { Sinusoidal.interpolateX2(src) };
		assertArrayEquals(expected, r);
	}

}
