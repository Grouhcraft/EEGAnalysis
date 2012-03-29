package graphwindow.plot;

import filters.WechWechMethod;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import graphwindow.WaveClass;

import java.io.File;

public class WelchPlot extends Plot{

	public WelchPlot(int channel, File file) {
		super(channel, file);
	}
	
	public WelchPlot(IPlot plot) {
		super(plot);
	}

	@Override
	protected SGTData setMetaData(SGTData data) {
		((SimpleLine)data).setXMetaData(new SGTMetaData("Frequency", "Hz", false, false));
	    ((SimpleLine)data).setYMetaData(new SGTMetaData("Magnitude", "dBµV²", false, false));
	    return data;
	}
	
	@Override
	protected SGTData processSignal(double[][] data) {				
		int lfq = (waveClass == WaveClass.NONE) ? 0 : (int)waveClass.getFrequencyRange().lower;
		int hfq = (waveClass == WaveClass.NONE) ? dataInfo.fs / 2 : (int)waveClass.getFrequencyRange().higher;
			
		data = WechWechMethod.compute( data, dataInfo.fs, lfq, hfq);
	    return new SimpleLine(data[X], data[Y], null);
	}
}
