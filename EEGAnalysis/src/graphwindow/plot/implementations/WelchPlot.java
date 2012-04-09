package graphwindow.plot.implementations;

import filters.WechWechMethod;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import graphwindow.data.WaveClass;
import graphwindow.graphlayouts.LinePlotLayout;
import graphwindow.plot.GraphSetting;
import graphwindow.plot.IPlot;
import graphwindow.plot.Plot;
import graphwindow.plot.graphtype;

import java.io.File;

@graphtype(
		name = "Welch Periodogram",
		layout = LinePlotLayout.class
		)

public class WelchPlot extends Plot{
	
	@GraphSetting("Limit the Fq band to current waveclass one")
	public boolean limitFrequencyToWaveClass = true;
	
	@GraphSetting("Segment length")
	public int segmentLen = 800;
	
	@GraphSetting("Use logarithmic Y scale")
	public boolean useLogScale = true;
	
	@GraphSetting("Use Square window instead of Hann")
	public boolean useSquareWindow = false;
	
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
	protected SGTData processSignal() {
		double[][] data = getRawData();
		int lfq,hfq;
		if(limitFrequencyToWaveClass) {
			lfq = (waveClass == WaveClass.NONE) ? 0 : (int)(double)waveClass.getFrequencyRange().getLower();
			hfq = (waveClass == WaveClass.NONE) ? dataInfo.fs / 2 : (int)(double)waveClass.getFrequencyRange().getHigher();
		} else {
			lfq = 0;
			hfq = dataInfo.fs / 2;
		}

		data = WechWechMethod.compute( data, dataInfo.fs, lfq, hfq, segmentLen, useSquareWindow, useLogScale);
	    return new SimpleLine(data[X], data[Y], null);
	}

	@Override
	public void setDataId(Object data, String id) {
		((SimpleLine)data).setId(id);
	}
}
