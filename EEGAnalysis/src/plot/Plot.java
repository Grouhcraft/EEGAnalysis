package plot;

import filters.CutOff;
import filters.EnergySpectralDensity;
import filters.WelchMethod;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import gov.noaa.pmel.sgt.swing.JPlotLayout;
import gov.noaa.pmel.util.Point2D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import main.Logger;
import main.R;

public class Plot {
	static class DataSettings  {
		int channelsCount = 59;
		int samplingRate = 100;
		File file = null; 
		File markerFile = new File(System.getenv("EEGDATA") + "\\" + R.get("markerfile"));
		int subSampling = 1;
		int channel = 1;
		String[] channelCode = new String[] {
				"AF3", "AF4", "F5", "F3", 
				"F1", "Fz", "F2", "F4", "F6", 
				"FC5", "FC3", "FC1", "FCz", 
				"FC2", "FC4", "FC6", "CFC7", "CFC5", 
				"CFC3", "CFC1", "CFC2", "CFC4",
				"CFC6", "CFC8", "T7", "C5", 
				"C3", "C1", "Cz", "C2", "C4", 
				"C6", "T8", "CCP7", "CCP5", "CCP3",
				"CCP1", "CCP2", "CCP4", "CCP6", "CCP8", 
				"CP5", "CP3", "CP1", "CPz", "CP2", 
				"CP4", "CP6", "P5", "P3", "P1", "Pz", 
				"P2", "P4", "P6", "PO1", "PO2", "O1"
			};
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
		int from = 10;
		int to = 30;
	} 
	
	public TimeFrame timeFrame = new TimeFrame();
	public FrequencyRange frequencyRange = new FrequencyRange();
	public AmplitudeCutoff amplitudeCutoff = new AmplitudeCutoff();
	public DataSettings dataSettings = new DataSettings();	
	
	private final int X = 0;
	private final int Y = 1;
	public WaveClass waveClass;
	
	private SGTData data = null;
	private GraphType graphType = GraphType.WaveForm;
	
	public Plot(int channel, File file) {
		dataSettings.file = file;
		dataSettings.channel = channel;
	}

	public int getChanel() {
		return dataSettings.channel;
	}

	/**
	 * Reads the EEG data from {@link #dataFile}   
	 * @param channel EEG Channel to read 
	 * @return the SGTData data used by the graph layouts
	 */
	private SGTData readTheData() {
		return readTheData(dataSettings.file, dataSettings.channel);
	}
	
	
	@SuppressWarnings("unused")
	private SGTData readTheMarkerData(double startLimit, double endLimit, double minYValue, double maxYValue) {
		BufferedReader in = null;
		String line = null;
	    try {
			in = new BufferedReader(new FileReader(dataSettings.markerFile));
			line = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    ArrayList<Double> times = new ArrayList<Double>();
	    ArrayList<Integer> directions = new ArrayList<Integer>();
	    while(line != null) {
	    	double d = Double.parseDouble(line.split("\t")[0]);
	    	double plopStartLimit = timeFrame.from * dataSettings.samplingRate;
	    	double plopEndLimit = timeFrame.to * dataSettings.samplingRate;
	    	if(d > plopStartLimit && d < plopEndLimit) {
	    		int dir = (int)Double.parseDouble(line.split("\t")[1]);
	    		directions.add(dir);
	    		times.add(d);
	    	}
	    	try {
	    		line = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    double[] xArr = new double[(int) (endLimit - startLimit)];
	    double[] yArr = new double[(int) (endLimit - startLimit)];
	    for(int i=(int) startLimit; i<endLimit; i++) {
	    	xArr[i] = i;
	    	yArr[i] = 0;
	    }
	    int cueDuration = 4;
	    for(int i=0; i<times.size(); i++) {
	    	for(int y=0; y<cueDuration * dataSettings.samplingRate &&
	    			(int)(times.get(i) - timeFrame.from * dataSettings.samplingRate) + y < yArr.length 
	    			; y++) {
		    	yArr[(int)(times.get(i) - timeFrame.from * dataSettings.samplingRate) + y] 
		    			= directions.get(i) == 1 ? maxYValue : minYValue;
	    	}
	    }
	    SimpleLine markers = new SimpleLine(xArr, yArr, null);
	    markers.setId("markers");	
	    markers.setXMetaData(new SGTMetaData("Cues", "", false, false));
	    markers.setYMetaData(new SGTMetaData("", "", false, false));
	    return markers;
	}
	
	/**
	 * Reads the EEG data from {@link #dataFile}   
	 * @param file absolute path of the data file
	 * @param subsamplingFactor 1 = no subSampling, 10 = take 1/10th of the samples, 100 = ... 
	 * @param channel EEG channel to read
	 * @param LowCutOff @todo remove that !
	 * @param HighCutOff @todo remove that !
	 * @return the SGTData data used by the graph layouts
	 */
	private SGTData readTheData(File file, int channel) {
		BufferedReader in = null;
		String line = null;
		int x,y;
		Point2D.Double p = null;
		ArrayList<Point2D> list = new ArrayList<Point2D>();
		
	    try {
			in = new BufferedReader(new FileReader(file));
			line = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    int i=0;
	    int toSkip = (timeFrame.from * dataSettings.samplingRate) / dataSettings.subSampling;
		while(line != null && i<toSkip) {
			try {
				line = in.readLine();
				i++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
		int toRead = ((timeFrame.to - timeFrame.from) * dataSettings.samplingRate) / dataSettings.subSampling; 
	    i = 0;
	    while(line != null && i < toRead) {
	    	x = i; 
	    	y = Integer.parseInt(line.split("\t")[channel]);
	    	p = new Point2D.Double(x, y);
	    	list.add(p);
	    	
	    	i++;
	    	try {
	    		for(int ii=0; ii < (dataSettings.subSampling > 1 ? dataSettings.subSampling-1 : 1); ii++ ) {
	    			line = in.readLine();
	    		}
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    Logger.log("parsing " + i/1000 + "K samples (over " + (i*dataSettings.subSampling)/1000 + "K ones) from " 
	    		+ dataSettings.samplingRate + "Hz channel " + channel + "'s data => " 
	    		+ (i*dataSettings.subSampling)/dataSettings.samplingRate + "s record"
	    		);
	    
	    
	    double xArr[] = new double[list.size()];
	    double yArr[] = new double[list.size()];
	    Iterator<Point2D> it = list.iterator();
	    i = 0;
	    while(it.hasNext()) {
	    	p = (Point2D.Double) it.next();
	    	xArr[i] = p.x / dataSettings.samplingRate;
	    	yArr[i] = p.y;
	    	i++;
	    }
	    SimpleLine data = processSignal(new double[][] {xArr, yArr});
	    data.setId(getDataId(file,dataSettings.subSampling, channel, amplitudeCutoff, timeFrame, frequencyRange));
	    if(graphType == GraphType.EnergySpectralDensity) {
		    data.setXMetaData(new SGTMetaData("Frequency", "Hz", false, false));
		    data.setYMetaData(new SGTMetaData("Magnitude", "dBµV²", false, false));
	    } else {
		    data.setXMetaData(new SGTMetaData("Time", "secondes", false, false));
		    data.setYMetaData(new SGTMetaData("Potential", "µV", false, false));	    	
	    }
	    
		return data;
	}
	
	/**
	 * Register the various settings related to the selected {@link WavesClasses wave class}
	 * @param wc the wave class
	 * @see {@link WavesClasses}
	 * @see {@link #frequencyRange}
	 * @see {@link #HighCutOff}
	 * @see {@link #LowCutOff}
	 */
	public void setWaveClass(WaveClass wc) {
		waveClass = wc;
		amplitudeCutoff.low = wc.getUpperAmpl();
		amplitudeCutoff.high = wc.getLowerAmpl();
		frequencyRange.lower = wc.getLowerFreq();
		frequencyRange.higher = wc.getUpperFreq();
	}
	
	/**
	 * Apply treatments to the signal data then transforms it to a {@link SimpleLine}, 
	 * used by the {@link JPlotLayout graph}
	 * @param data index 0 = X, index 1 = Y
	 * @return the drawable SimpleLine curve
	 */
	private SimpleLine processSignal(double[][] data) {				
		if(waveClass != WaveClass.NONE) {
			Logger.log("showing frequency range [" + frequencyRange.lower + " ; " + frequencyRange.higher + "]");
			data = CutOff.frequencyRange(data, frequencyRange.lower, frequencyRange.higher, dataSettings.samplingRate);
		}
		if(getGraphType() == GraphType.EnergySpectralDensity) {
			int lfq;
			int hfq;
			if(waveClass == WaveClass.NONE) {
				lfq = 0;
				hfq = dataSettings.samplingRate / 2;
			} else {
				lfq = waveClass.getLowerFreq();
				hfq = waveClass.getUpperFreq();
			}
			data = EnergySpectralDensity.compute(data, dataSettings.samplingRate, lfq, hfq);
		}
	    return new SimpleLine(data[X], data[Y], null);
	}

	/**
	 * Constructs the ID string for a data, which allows to track any changes in data settings
	 * @param file the data file used
	 * @param sampling the subsampling ratio used
	 * @param channel the EEG channel  
	 * @param LowCutOff the Low-Amplitude cutOff set
	 * @param HighCutOff the High-Amplitude cutOff set
	 * @return a string representing the data and its settings
	 */
	private String getDataId(File file, int sampling, int channel, AmplitudeCutoff ampCutOff, TimeFrame timeRange, FrequencyRange fr) {
		return file.getPath() + sampling 
				+ "_" + channel + "_" + ampCutOff.low + "_" + ampCutOff.high
				+ "_" + timeRange.from + "_" + timeRange.to
				+ "_" + fr.higher + "_" + fr.lower;
	}

	public void update() {
		data = readTheData();
	}

	public SGTData getData() {
		return data;
	}
	
	public File getDataFile() {
		return dataSettings.file;
	}

	public void setDataFile(File dataFile) {
		dataSettings.file = dataFile;
	}

	public void setGraphType(GraphType graphType) {
		this.graphType = graphType;
	}

	public GraphType getGraphType() {
		return graphType;
	}
}
