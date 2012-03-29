package graphwindow.plot;

import gov.noaa.pmel.sgt.dm.SGTData;
import graphwindow.DataInfos;
import graphwindow.WaveClass;
import graphwindow.graphlayouts.IGraphLayout;
import graphwindow.plot.Plot.TimeFrame;

public interface IPlot {
	
	WaveClass getWaveClass();
	
	DataInfos getInfos();
	
	/**
	 * Reloads and parse data from file
	 */
	void update();

	SGTData getData();
	
	void setDataId(SGTData data, String id);

	void setWaveClass(WaveClass waveClass);

	TimeFrame getTime();
	
	Class<? extends IGraphLayout> getGraphLayoutType();
}
