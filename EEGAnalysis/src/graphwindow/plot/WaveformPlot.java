package graphwindow.plot;

import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import graphwindow.graphlayouts.IGraphLayout;
import graphwindow.graphlayouts.LinePlotLayout;

import java.io.File;
import java.text.DecimalFormat;

public class WaveformPlot extends Plot {

	public WaveformPlot(int channel, File file) {
		super(channel, file);
	}

	public WaveformPlot(IPlot plot) {
		super(plot);
	}

	@Override
	protected SGTData setMetaData(SGTData data) {
	    ((SimpleLine)data).setXMetaData(new SGTMetaData("Time", "secondes", false, false));
	    ((SimpleLine)data).setYMetaData(new SGTMetaData("Potential", "�V", false, false));
	    return data;
	}
	
	@Override	
	protected SGTData processSignal(double[][] data) {		
		data = cleanXAxis(data);
	    return new SimpleLine(((double[][])data)[X], ((double[][])data)[Y], null);
	}
	
	protected double[][] cleanXAxis(double[][] data) {
		double newData[][] = data.clone();
		DecimalFormat f = new DecimalFormat("#.##");
		for(int i=0; i<data[X].length; i++) {
			newData[X][i] = Double.valueOf(f.format(newData[X][i]));    
		}
		return newData;
	}

	@Override
	public Class<? extends IGraphLayout> getGraphLayoutType() {
		return LinePlotLayout.class;
	}


	@Override
	public void setDataId(SGTData data, String id) {
		((SimpleLine)data).setId(id);
	}
}
