package plotframes.plots.implementations;

import filters.implementations.EnergySpectralDensityFilter;
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
		name = "Energy spectral density",
		layout = LinePlotLayout.class
		)

public class SpectralDensityPlot extends Plot{

	@GraphSetting("Use logarithmic Y scale")
	public boolean useLogScale = true;
	
	public SpectralDensityPlot(int channel, File file) {
		super(channel, file);
	}

	public SpectralDensityPlot(IPlot plot) {
		super(plot);
	}

	@Override
	protected Object setMetaData(Object data) {
		((SimpleLine)data).setXMetaData(new SGTMetaData("Frequency", "Hz", false, false));
	    ((SimpleLine)data).setYMetaData(new SGTMetaData("Magnitude", "dBµV²", false, false));
	    return data;
	}

	@Override
	protected SGTData processSignal() {
		double[][] data = getRawData();
		int lfq = (waveClass == WaveClass.NONE) ? 0 : (int)(double) waveClass.getFrequencyRange().getLower();
		int hfq = (waveClass == WaveClass.NONE) ? dataInfo.fs / 2 : (int)(double) waveClass.getFrequencyRange().getHigher();

		data = EnergySpectralDensityFilter.compute( data, dataInfo.fs, lfq, hfq, useLogScale );

	    return new SimpleLine(data[X], data[Y], null);
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
