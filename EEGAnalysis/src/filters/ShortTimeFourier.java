package filters;

import main.utils.Logger;
import filters.utils.ChunkedData;
import filters.utils.Filter;

public class ShortTimeFourier extends Filter {

	// @TODO
	public static double[][] compute(double[][] data) {
		int fs = 100;
		double resolutionFactor = 1;
		int timeResolution = (int) (fs / resolutionFactor);
		int freqLowerLimit = (int) (2 * resolutionFactor);
		int freqUpperLimit = (timeResolution <= fs) ? timeResolution/2 : fs/2;
		ChunkedData chunked = new ChunkedData(data[Y], fs, timeResolution, 0);
		int yFqTo = (int) ((timeResolution / (double)fs) * freqUpperLimit);
		int yFqFrom = (int) ((timeResolution / (double)fs) * freqLowerLimit);
		int yLen = yFqTo - yFqFrom;
		Logger.log("request array X len:" + chunked.getNumberOfChunk());
		Logger.log("request array Y len:" + yLen);
		Logger.log("chunkLen:" + timeResolution);
		Logger.log("dataYLen:" + data[Y].length);
		double[][] stf = new double[][] {
				new double[chunked.getNumberOfChunk()],
				new double[yLen],
				new double[chunked.getNumberOfChunk() * yLen]
		};
		for(int i=0; i < chunked.getNumberOfChunk(); i++) {
			stf[X][i] = i;
		}

		double[] psd = null;
		while(chunked.hasNextChunk()) {
			psd = EnergySpectralDensity.compute(chunked.getChunk(), fs);
			try {
				for(int i=yFqFrom; i<yFqTo; i++) {
					stf[Z][(chunked.getChunkPosition()*yLen) + i - yFqFrom] = Math.log(psd[i]);
				}
			} catch (Exception e) {
				Logger.log("	yFqFrom==" + yFqFrom);
				Logger.log(" 	yFqTo==" + yFqTo);
				Logger.log(" 	psdLen==" + psd.length);
				Logger.log(" 	chunkedPos==" + chunked.getChunkPosition());
				e.printStackTrace();
				return null;
			}
			chunked.nextChunk();
		}
		for(int i=yFqFrom; i<yFqTo; i++) {
			stf[Y][i-yFqFrom] = i;
		}

		return stf;
	}
}
