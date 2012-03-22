package plot;

import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import gov.noaa.pmel.util.Point2D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import main.Logger;

public class DataFileReader {
	
	public class DataReader {
		/**
		 * Reads the EEG data from {@link #dataFile}   
		 * @param file absolute path of the data file
		 * @param subsamplingFactor 1 = no subSampling, 10 = take 1/10th of the samples, 100 = ... 
		 * @param channel EEG channel to read
		 * @param LowCutOff @todo remove that !
		 * @param HighCutOff @todo remove that !
		 * @return the SGTData data used by the graph layouts
		 */
		public double[][] read(File file, int channel, int samplingRate, int from, int to) {
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
		    int toSkip = (from * samplingRate);
			while(line != null && i<toSkip) {
				try {
					line = in.readLine();
					i++;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	    	
			int toRead = ((to - from) * samplingRate); 
		    i = 0;
		    while(line != null && i < toRead) {
		    	x = i; 
		    	y = Integer.parseInt(line.split("\t")[channel]);
		    	p = new Point2D.Double(x, y);
		    	list.add(p);
		    	
		    	i++;
		    	try {
	    			line = in.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		    Logger.log("parsing " + i/1000 + "K samples (over " + i/1000 + "K ones) from " 
		    		+ samplingRate + "Hz channel " + channel + "'s data => " 
		    		+ i/samplingRate + "s record"
		    		);
		    
		    
		    double xArr[] = new double[list.size()];
		    double yArr[] = new double[list.size()];
		    Iterator<Point2D> it = list.iterator();
		    i = 0;
		    while(it.hasNext()) {
		    	p = (Point2D.Double) it.next();
		    	xArr[i] = p.x / samplingRate;
		    	yArr[i] = p.y;
		    	i++;
		    }
		    
		    return new double[][] {xArr, yArr};
		}
	}
	DataReader dataReader = new DataReader();
	
	
	public class MetaDataReader {
		
		/**
		 * @TODO !
		 * @param startLimit
		 * @param endLimit
		 * @param minYValue
		 * @param maxYValue
		 * @param markerFile
		 * @param samplingRate
		 * @param from
		 * @param to
		 * @return
		 */
		@SuppressWarnings("unused")
		private SGTData read(
				double startLimit, 
				double endLimit, 
				double minYValue, 
				double maxYValue,
				File markerFile,
				int samplingRate,
				int from,
				int to
				){
			BufferedReader in = null;
			String line = null;
		    try {
				in = new BufferedReader(new FileReader(markerFile));
				line = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    ArrayList<Double> times = new ArrayList<Double>();
		    ArrayList<Integer> directions = new ArrayList<Integer>();
		    while(line != null) {
		    	double d = Double.parseDouble(line.split("\t")[0]);
		    	double plopStartLimit = from * samplingRate;
		    	double plopEndLimit = to * samplingRate;
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
		    	for(int y=0; y<cueDuration * samplingRate &&
		    			(int)(times.get(i) - from * samplingRate) + y < yArr.length 
		    			; y++) {
			    	yArr[(int)(times.get(i) - from * samplingRate) + y] 
			    			= directions.get(i) == 1 ? maxYValue : minYValue;
		    	}
		    }
		    SimpleLine markers = new SimpleLine(xArr, yArr, null);
		    markers.setId("markers");	
		    markers.setXMetaData(new SGTMetaData("Cues", "", false, false));
		    markers.setYMetaData(new SGTMetaData("", "", false, false));
		    return markers;
		}
	}
	MetaDataReader metaDataReader = new MetaDataReader();
}
