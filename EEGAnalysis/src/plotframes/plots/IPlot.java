package plotframes.plots;


import java.util.ArrayList;

import plotframes.data.DataInfos;
import plotframes.data.WaveClass;
import plotframes.plots.Plot.TimeFrame;

import utils.Range;


public interface IPlot {

	static ArrayList<Class<? extends IPlot>> graphTypes = new ArrayList<>();

	WaveClass getWaveClass();

	/**
	 * Retuns the {@link DataInfos} object containing all
	 * the informations about the readen data and the manner it is 
	 * read. For instance, the sampling rate, the File handler, ...
	 */
	DataInfos getInfos();

	/**
	 * Reloads and parse data from input source
	 */
	void update();

	Object getData();

	/**
	 * If needed by the plot layout component or the plot, 
	 * sets an ID string to the data Object.
	 * @param data
	 * @param id
	 */
	void setDataId(Object data, String id);

	/**
	 * Asks the plot to set a particular waveclass.<br/>
	 * Doing so usualy also filters the corresponding 
	 * amplitudes and frequencies bands through FFT
	 * @param waveClass
	 */
	void setWaveClass(WaveClass waveClass);

	/**
	 * Returns the currently readen time frame within the data
	 */
	TimeFrame getTime();

	Range<Double> getXRange();
	
	Range<Double> getYRange();

	/**
	 * Indicates if the EEG channels are currently averaged.<br/>
	 * If they are, the channels averaged are usualy taken from {@link DataInfos#channelsToAverage} 
	 */
	boolean areChannelsAveraged();

	/**
	 * Asks the plot to average channels
	 * @see {@link #areChannelsAveraged()}
	 * @param areChannelsAveraged
	 */
	void setChannelsAveraged(boolean areChannelsAveraged);
}
