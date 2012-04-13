package plotframes.plots.implementations;

import filters.implementations.WelchMethodFilter;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;

import java.io.File;

import plotframes.data.WaveClass;
import plotframes.graphlayouts.LinePlotLayout;
import plotframes.plots.IPlot;
import plotframes.plots.Plot;
import plotframes.plots.annotations.GraphSetting;
import plotframes.plots.annotations.graphtype;

import utils.Range;

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
	protected Object setMetaData(Object data) {
		((SimpleLine)data).setXMetaData(new SGTMetaData("Frequency", "Hz", false, false));
	    ((SimpleLine)data).setYMetaData(new SGTMetaData("Magnitude", "dBµV²", false, false));
	    return data;
	}

	@Override
	protected Object processSignal() {
		double[][] rawdata = getRawData();
		int lfq,hfq;
		if(limitFrequencyToWaveClass) {
			lfq = (waveClass == WaveClass.NONE) ? 0 : (int)(double)waveClass.getFrequencyRange().getLower();
			hfq = (waveClass == WaveClass.NONE) ? dataInfo.fs / 2 : (int)(double)waveClass.getFrequencyRange().getHigher();
		} else {
			lfq = 0;
			hfq = dataInfo.fs / 2;
		}

		rawdata = WelchMethodFilter.compute( rawdata, dataInfo.fs, lfq, hfq, segmentLen, useSquareWindow, useLogScale);
	    return new SimpleLine(rawdata[X], rawdata[Y], null);
	}

	@Override
	public void setDataId(Object data, String id) {
		((SimpleLine)data).setId(id);
	}
	
	
	@Override
	public Range<Double> getXRange() {
		return new Range<Double>(
				(Double)((SGTData)getData()).getXRange().getStart().getObjectValue(),
				(Double)((SGTData)getData()).getXRange().getEnd().getObjectValue()
				);
	}

	@Override
	public Range<Double> getYRange() {
		return new Range<Double>(
				(Double)((SGTData)getData()).getYRange().getStart().getObjectValue(),
				(Double)((SGTData)getData()).getYRange().getEnd().getObjectValue()
				);
	}
}
