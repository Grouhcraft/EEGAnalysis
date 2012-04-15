package filters.implementations;

import utils.ChunkedData;
import filters.Filter;

public class ShortTimeFourierFilter extends Filter {

	public static double[][] compute(double[][] data, double resolutionFactor, int fs) {
		int timeResolution = (int) (fs / resolutionFactor);
		int freqLowerLimit = (int) (2 * resolutionFactor);
		int freqUpperLimit = (timeResolution <= fs) ? timeResolution/2 : fs/2;
		ChunkedData chunked = new ChunkedData(data[Y], fs, timeResolution, 0);
		int yFqTo = (int) ((timeResolution / (double)fs) * freqUpperLimit);
		int yFqFrom = (int) ((timeResolution / (double)fs) * freqLowerLimit);
		int yLen = yFqTo - yFqFrom;

		double[][] stf = new double[][] {
				new double[chunked.getNumberOfChunk()],
				new double[yLen],
				new double[chunked.getNumberOfChunk() * yLen]
		};
		for(int i=0; i < chunked.getNumberOfChunk(); i++) {
			stf[X][i] = i;
		}

		double[] psd = null;
		try {
			while(chunked.hasNextChunk()) {
				psd = EnergySpectralDensityFilter.compute(chunked.getChunk(), fs);
				for(int i=yFqFrom; i<yFqTo; i++)
					stf[Z][(chunked.getChunkPosition()*yLen) + i - yFqFrom] = Math.log(psd[i]);
				chunked.nextChunk();
			}
			psd = EnergySpectralDensityFilter.compute(chunked.getChunk(), fs);
			for(int i=yFqFrom; i<yFqTo; i++)
				stf[Z][(chunked.getChunkPosition()*yLen) + i - yFqFrom] = Math.log(psd[i]);
		} catch (Exception e) {	
			e.printStackTrace();
		}
		
		for(int i=yFqFrom; i<yFqTo; i++) {
			stf[Y][i-yFqFrom] = i;
		}

		return stf;
	}
}
