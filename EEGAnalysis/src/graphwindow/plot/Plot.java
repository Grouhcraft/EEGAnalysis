package graphwindow.plot;

import filters.CutOff;
import filters.utils.AmplitudeRange;
import filters.utils.FrequencyRange;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import gov.noaa.pmel.sgt.swing.JPlotLayout;
import graphwindow.DataFileReader;
import graphwindow.DataInfos;
import graphwindow.WaveClass;

import java.io.File;
import main.MainWindow;
import main.utils.Logger;

public abstract class Plot implements IPlot {	
	public static class TimeFrame {
		public int getFrom() { 
			return MainWindow.getPrefs().getInt(MainWindow.PREF_TIME_FROM, 30);
		}
		public void setFrom(int from) {
			MainWindow.getPrefs().putInt(MainWindow.PREF_TIME_FROM, from);
		}
		public int getTo() {
			return getFrom() + MainWindow.getPrefs().getInt(MainWindow.PREF_TIME_DURATION, 60);
		}
		public void setTo(int to) {
			MainWindow.getPrefs().putInt(MainWindow.PREF_TIME_DURATION, to - getFrom());
		}
	} 
	
	public TimeFrame time = new TimeFrame();
	public FrequencyRange freqRange = new FrequencyRange(1,15);
	public AmplitudeRange amplitudeCutoff = new AmplitudeRange();
	public DataInfos dataInfo = new DataInfos();	
	
	protected final int X = 0;
	protected final int Y = 1;
	protected final int Z = 2;
	public WaveClass waveClass;
	
	private SGTData data = null;
	
	public Plot(int channel, File file) {
		dataInfo.file = file;
		dataInfo.channel = channel;		
	}
	
	public Plot(IPlot src) {
		dataInfo.file = src.getInfos().file;
		dataInfo.channel = src.getInfos().channel;
		setWaveClass(src.getWaveClass());
	}

	/**
	 * Reads the EEG data from {@link #dataFile}   
	 * @return the SGTData data used by the graph layouts
	 */
	private SGTData readTheData() {
		boolean test = false;
		SGTData data;
		Logger.log("Test mode: " + (test ? true : false));
		double[][] rawData = new DataFileReader().dataReader.read(dataInfo, time);
		
		if(waveClass != WaveClass.NONE) {
			Logger.log("showing frequency range [" + freqRange.lower + " ; " + freqRange.higher + "]");
			rawData = CutOff.frequencyRange(rawData, freqRange, dataInfo.fs);
		}
		
		// Real data
		if(!test) data = processSignal(rawData);
			
		// Test sinusoidal
		else data = processSignal(synthetizer.Sinusoidal.merge(
					synthetizer.Sinusoidal.generate(5, dataInfo.fs, time.getTo() - time.getFrom(), 0.10, 1),
					synthetizer.Sinusoidal.generate(15, dataInfo.fs, time.getTo() - time.getFrom(), 0.05, 1)
				));
		
	 
		setDataId(data, getDataId());
	    data = setMetaData(data);
		return data;
	}
	
	protected abstract SGTData setMetaData(SGTData data);
	
	protected abstract SGTData processSignal(double[][] data);

	/**
	 * Constructs the ID string for a data
	 * @return a string representing the data and its settings
	 */
	private String getDataId() {
		return dataInfo.file.getPath() 
				+ "_" + dataInfo.channel + "_" + amplitudeCutoff.lower + "_" + amplitudeCutoff.higher
				+ "_" + time.getFrom() + "_" + time.getTo()
				+ "_" + freqRange.higher + "_" + freqRange.lower;
	}

	/**
	 * Reloads and parse data from file
	 */
	@Override
	public void update() {
		data = readTheData();
	}

	@Override
	public SGTData getData() {
		return data;
	}
	
	@Override
	public DataInfos getInfos() {
		return dataInfo;
	}
	
	@Override
	public TimeFrame getTime() {
		return time;
	}
	
	@Override
	public WaveClass getWaveClass() {
		return waveClass;
	}
	
	/**
	 * Register the various settings related to the selected {@link WavesClasses wave class}
	 * @param wc the wave class
	 * @see {@link WavesClasses}
	 * @see {@link #freqRange}
	 * @see {@link #HighCutOff}
	 * @see {@link #LowCutOff}
	 */
	@Override
	public void setWaveClass(WaveClass wc) {
		waveClass = wc;
		//TODO: verify that wc' range object is not overwritten
		amplitudeCutoff = wc.getAmplitudeRange();
		freqRange = wc.getFrequencyRange();
	}
}
