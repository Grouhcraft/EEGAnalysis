package plotframes.plots.implementations;

import filters.implementations.FFTFilter;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import plotframes.data.EEGSource;
import plotframes.graphlayouts.LinePlotLayout;
import plotframes.plots.IPlot;
import plotframes.plots.annotations.GraphSetting;
import plotframes.plots.annotations.UserPlot;

@UserPlot(	name = "FFT",
			layout = LinePlotLayout.class)

public class FFTPlot extends WaveformPlot {

	public FFTPlot(int channel, EEGSource dataSrc) {
		super(channel, dataSrc);
	}

	public FFTPlot(IPlot plot) {
		super(plot);
	}

	@GraphSetting("Imaginaries instead of real ?")
	public boolean viewImaginaries = false;

	@Override
	protected SGTData processSignal() {
		FFTFilter fft = new FFTFilter(getRawData()).forward();
		double newArr[][] = {
				fft.getData().x,
				new double[fft.getData().x.length]
		};
		newArr[Y][0] = 0;
		newArr[Y][1] = 0;
		if (viewImaginaries) {
			for(int i=2; i<fft.getData().y.length; i++)
				newArr[Y][i] = fft.getData().y[i].im;
		} else {
			for(int i=2; i<fft.getData().y.length; i++)
				newArr[Y][i] = fft.getData().y[i].real;
		}
		return new SimpleLine(newArr[X], newArr[Y], null);
	}

}
