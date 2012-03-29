package filters;

import main.utils.Logger;
import filters.utils.ChunkedData;
import filters.utils.Filter;

public class ShortTimeFourier extends Filter {
	
	// @TODO
	public static double[][] compute(double[][] data) {
		int fs = 100;
		int timeResolution = 300;
		ChunkedData chunked = new ChunkedData(data[Y], fs, timeResolution, 1);
		Logger.log("request array len:" + chunked.getNumberOfChunk());
		Logger.log("request array len:" + sq(chunked.getNumberOfChunk()));
		double[][] stf = new double[][] {
				new double[chunked.getNumberOfChunk()],
				null,
				new double[(int) chunked.getNumberOfChunk() * (data[Y].length/2)]
		};
		for(int i=0; i < chunked.getNumberOfChunk(); i++) {
			stf[X][i] = i * timeResolution;
		}
		
		double[] psd = null;
		while(chunked.hasNextChunk()) {
			psd = EnergySpectralDensity.compute(chunked.getChunk(), fs);
			for(int i=0; i<psd.length; i++) {
				stf[Z][(chunked.getChunkPosition() * timeResolution)+ i] = psd[i]; 
			}
			chunked.nextChunk();
		}
		stf[Y] = new double[psd.length];
		for(int i=0; i<psd.length; i++) {
			stf[Y][i] = i;
		}
		
		return stf;
	}
}
