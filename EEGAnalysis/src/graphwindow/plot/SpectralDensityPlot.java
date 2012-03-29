package graphwindow.plot;

import filters.EnergySpectralDensity;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import graphwindow.WaveClass;

import java.io.File;

public class SpectralDensityPlot extends Plot{

	public SpectralDensityPlot(int channel, File file) {
		super(channel, file);
	}
	
	public SpectralDensityPlot(IPlot plot) {
		super(plot);
	}

	@Override
	protected SGTData setMetaData(SGTData data) {
		((SimpleLine)data).setXMetaData(new SGTMetaData("Frequency", "Hz", false, false));
	    ((SimpleLine)data).setYMetaData(new SGTMetaData("Magnitude", "dB�V�", false, false));
	    return data;
	}
	
	@Override
	protected SGTData processSignal(double[][] data) {				
		int lfq = (waveClass == WaveClass.NONE) ? 0 : (int)waveClass.getFrequencyRange().lower;
		int hfq = (waveClass == WaveClass.NONE) ? dataInfo.fs / 2 : (int)waveClass.getFrequencyRange().higher;
		
		data = EnergySpectralDensity.compute( data, dataInfo.fs, lfq, hfq );
			
	    return new SimpleLine(data[X], data[Y], null);
	}
}
