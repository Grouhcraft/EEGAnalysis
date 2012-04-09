package graphwindow.plot.implementations;

import filters.EnergySpectralDensity;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import graphwindow.data.WaveClass;
import graphwindow.graphlayouts.LinePlotLayout;
import graphwindow.plot.IPlot;
import graphwindow.plot.Plot;
import graphwindow.plot.graphtype;

import java.io.File;

@graphtype(
		name = "Energy spectral density",
		layout = LinePlotLayout.class
		)

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
	    ((SimpleLine)data).setYMetaData(new SGTMetaData("Magnitude", "dBµV²", false, false));
	    return data;
	}

	@Override
	protected SGTData processSignal() {
		double[][] data = getRawData();
		int lfq = (waveClass == WaveClass.NONE) ? 0 : (int)(double) waveClass.getFrequencyRange().getLower();
		int hfq = (waveClass == WaveClass.NONE) ? dataInfo.fs / 2 : (int)(double) waveClass.getFrequencyRange().getHigher();

		data = EnergySpectralDensity.compute( data, dataInfo.fs, lfq, hfq );

	    return new SimpleLine(data[X], data[Y], null);
	}

	@Override
	public void setDataId(Object data, String id) {
		((SimpleLine)data).setId(id);
	}

}
