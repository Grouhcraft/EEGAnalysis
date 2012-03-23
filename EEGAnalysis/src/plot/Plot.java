package plot;

import filters.CutOff;
import filters.EnergySpectralDensity;
import filters.WelchMethod;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import gov.noaa.pmel.sgt.swing.JPlotLayout;
import java.io.File;
import main.Logger;
import main.MainWindow;
import main.R;

public class Plot {
	static class DataSettings  {
		int 	channelsCount 	= 59;
		int 	fs 				= 100;
		File 	file 			= null; 
		File 	markerFile 		= new File(System.getenv("EEGDATA") + "\\" + R.get("markerfile"));
		int		channel 		= 1;
		String[] channelsCodes = new String[] { "AF3", "AF4", "F5", "F3", "F1",
				"Fz", "F2", "F4", "F6", "FC5", "FC3", "FC1", "FCz", "FC2",
				"FC4", "FC6", "CFC7", "CFC5", "CFC3", "CFC1", "CFC2", "CFC4",
				"CFC6", "CFC8", "T7", "C5", "C3", "C1", "Cz", "C2", "C4", "C6",
				"T8", "CCP7", "CCP5", "CCP3", "CCP1", "CCP2", "CCP4", "CCP6",
				"CCP8", "CP5", "CP3", "CP1", "CPz", "CP2", "CP4", "CP6", "P5",
				"P3", "P1", "Pz", "P2", "P4", "P6", "PO1", "PO2", "O1" };
	}
	
	static class AmplitudeCutoff {
		int high = 30;
		int low = 5;
		int passes = 3;
	}
	
	static class FrequencyRange {
		int lower = 15;
		int higher = 5;
	}
	
	static class TimeFrame {
		int getFrom() { 
			return MainWindow.getPrefs().getInt(MainWindow.PREF_TIME_FROM, 30);
		}
		void setFrom(int from) {
			MainWindow.getPrefs().putInt(MainWindow.PREF_TIME_FROM, from);
		}
		int getTo() {
			return getFrom() + MainWindow.getPrefs().getInt(MainWindow.PREF_TIME_DURATION, 60);
		}
		void setTo(int to) {
			MainWindow.getPrefs().putInt(MainWindow.PREF_TIME_DURATION, to - getFrom());
		}
	} 
	
	public TimeFrame time = new TimeFrame();
	public FrequencyRange freqRange = new FrequencyRange();
	public AmplitudeCutoff amplitudeCutoff = new AmplitudeCutoff();
	public DataSettings dataInfo = new DataSettings();	
	
	private final int X = 0;
	private final int Y = 1;
	public WaveClass waveClass;
	
	private SGTData data = null;
	private GraphType graphType = GraphType.WaveForm;
	
	public Plot(int channel, File file) {
		dataInfo.file = file;
		dataInfo.channel = channel;
	}

	/**
	 * Reads the EEG data from {@link #dataFile}   
	 * @return the SGTData data used by the graph layouts
	 */
	private SGTData readTheData() {
		 SimpleLine data = processSignal(new DataFileReader().dataReader.read(
				 dataInfo.file, 
				 dataInfo.channel,
				 dataInfo.fs,
				 time.getFrom(),
				 time.getTo()
				 ));
		    data.setId(getDataId());
		    if(graphType == GraphType.EnergySpectralDensity
		    		|| graphType == GraphType.WelchPeriodogram) {
			    data.setXMetaData(new SGTMetaData("Frequency", "Hz", false, false));
			    data.setYMetaData(new SGTMetaData("Magnitude", "dBµV²", false, false));
		    } else if (graphType == GraphType.WaveForm) {
			    data.setXMetaData(new SGTMetaData("Time", "secondes", false, false));
			    data.setYMetaData(new SGTMetaData("Potential", "µV", false, false));	    	
		    }
			return data;
	}
	
	/**
	 * Register the various settings related to the selected {@link WavesClasses wave class}
	 * @param wc the wave class
	 * @see {@link WavesClasses}
	 * @see {@link #freqRange}
	 * @see {@link #HighCutOff}
	 * @see {@link #LowCutOff}
	 */
	public void setWaveClass(WaveClass wc) {
		waveClass = wc;
		amplitudeCutoff.low = wc.getUpperAmpl();
		amplitudeCutoff.high = wc.getLowerAmpl();
		freqRange.lower = wc.getLowerFreq();
		freqRange.higher = wc.getUpperFreq();
	}
	
	/**
	 * Apply treatments to the signal data then transforms it to a {@link SimpleLine}, 
	 * used by the {@link JPlotLayout graph}
	 * @param data index 0 = X, index 1 = Y
	 * @return the drawable SimpleLine curve
	 */
	private SimpleLine processSignal(double[][] data) {				
		if(waveClass != WaveClass.NONE) {
			Logger.log("showing frequency range [" + freqRange.lower + " ; " + freqRange.higher + "]");
			data = CutOff.frequencyRange(data, freqRange.lower, freqRange.higher, dataInfo.fs);
		}
		if(getGraphType() == GraphType.EnergySpectralDensity
				|| getGraphType() == GraphType.WelchPeriodogram) 
		{
			int lfq = (waveClass == WaveClass.NONE) 
					? 0 : waveClass.getLowerFreq();
			int hfq = (waveClass == WaveClass.NONE) 
					? dataInfo.fs / 2 : waveClass.getUpperFreq();
	
			if(getGraphType() == GraphType.EnergySpectralDensity)
				data = EnergySpectralDensity.compute( data, dataInfo.fs, lfq, hfq );
			
			else if (getGraphType() == GraphType.WelchPeriodogram) {
				data = WelchMethod.compute( data, dataInfo.fs, lfq, hfq);
			}
		}
	    return new SimpleLine(data[X], data[Y], null);
	}

	/**
	 * Constructs the ID string for a data, which allows to track any changes in data settings
	 * @param file the data file used
	 * @param channel the EEG channel  
	 * @param LowCutOff the Low-Amplitude cutOff set
	 * @param HighCutOff the High-Amplitude cutOff set
	 * @return a string representing the data and its settings
	 */
	private String getDataId() {
		return dataInfo.file.getPath() 
				+ "_" + dataInfo.channel + "_" + amplitudeCutoff.low + "_" + amplitudeCutoff.high
				+ "_" + time.getFrom() + "_" + time.getTo()
				+ "_" + freqRange.higher + "_" + freqRange.lower;
	}

	/**
	 * Reloads and parse data from file
	 */
	public void update() {
		data = readTheData();
	}

	public SGTData getData() {
		return data;
	}
	
	public File getDataFile() {
		return dataInfo.file;
	}

	public void setDataFile(File dataFile) {
		dataInfo.file = dataFile;
	}

	public void setGraphType(GraphType graphType) {
		this.graphType = graphType;
	}

	public GraphType getGraphType() {
		return graphType;
	}
}
