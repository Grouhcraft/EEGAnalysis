package plotframes.data;

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

import plotframes.plots.IPlot;
import utils.Logger;
import utils.types.TimeFrame;

public class EEGSourceFile implements EEGSource {

	private File file = null;

	public EEGSourceFile(File f) {
		file = f;
	}

	@Override
	public String getName() {
		return file.getName();
	}

	public File getFile() {
		return file;
	}

	@Override
	public double[][] read(DataInfos infos, TimeFrame time) {
		BufferedReader in = null;
		String line = null;
		double x,y = 0;
		Point2D.Double p = null;
		ArrayList<Point2D> list = new ArrayList<Point2D>();

	    try {
			in = new BufferedReader(new FileReader(file));
			line = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

	    int i=0;
	    int toSkip = (time.getFrom() * infos.fs);
		while(line != null && i<toSkip) {
			try {
				line = in.readLine();
				i++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		int toRead = ((time.getTo() - time.getFrom()) * infos.fs);
	    i = 0;
	    while(line != null && i < toRead) {
	    	x = i;
	    	if(infos.areChannelsAveraged) {
	    		String[] splitted = line.split("\t");
	    		for(int chan : infos.channelsToAverage) {
	    			y += Double.parseDouble(splitted[chan]);
	    		}
	    		y /= infos.channelsToAverage.length;
	    	} else {
	    		y = Integer.parseInt(line.split("\t")[infos.channel]);
	    	}
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
	    		+ infos.fs + "Hz channel " + infos.channel + "'s data => "
	    		+ i/infos.fs + "s record");
	    Logger.log("Channels averaging: " + infos.areChannelsAveraged);


	    double xArr[] = new double[list.size()];
	    double yArr[] = new double[list.size()];
	    Iterator<Point2D> it = list.iterator();
	    i = 0;
	    while(it.hasNext()) {
	    	p = (Point2D.Double) it.next();
	    	xArr[i] = p.x / infos.fs;
	    	yArr[i] = p.y / 1000;
	    	i++;
	    }

	    return new double[][] {xArr, yArr};
	}

	@Override
	public boolean isContinuous() {
		return false;
	}

	@Override
	public boolean isFile() {
		return true;
	}

	public SGTData readMarkersFromPlot(IPlot plot)
			throws IOException, IllegalArgumentException {

		if(!plot.getDataSource().isFile())
			throw new IllegalArgumentException("Unable to read a marker file if source is not a file..");

		File plotFile = ((EEGSourceFile)plot.getDataSource()).getFile();
		String plotFileName = plotFile.getName();
		File markerFile = null;
		String markerFileName = "";
		if(plotFileName.contains("_cnt")) {
			markerFileName = plotFile.getParentFile() + File.separator + plotFileName.replace("_cnt", "_mrk");
			markerFile = new File(markerFileName);
		}
		if(markerFile == null || !markerFile.exists()) {
			throw new IOException("Unable to find or open marker file:" + markerFileName);
		}

		return readMarkers(
				plot.getXRange().getLower(),
				plot.getXRange().getHigher(),
				plot.getYRange().getLower(),
				plot.getYRange().getHigher(),
				markerFile,
				plot.getInfos().fs,
				plot.getTime().getFrom(),
				plot.getTime().getTo()
				);
	}

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
	public SGTData readMarkers(
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
	    double fromSample = from * samplingRate;
	    double toSample = to * samplingRate;
	    minYValue += Math.abs(minYValue/10);
	    maxYValue -= maxYValue/10;
	    ArrayList<Double> times = new ArrayList<>();
	    ArrayList<Integer> directions = new ArrayList<>();
	    while(line != null) {
	    	double d = Double.parseDouble(line.split("\t")[0]);
	    	if(d > fromSample && d < toSample) {
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
	    //endLimit = Math.ceil(endLimit);
	    double[] xArr = new double[(int) (toSample - fromSample)];
	    double[] yArr = new double[xArr.length];
	    for(int i=0; i<xArr.length; i++) {
	    	xArr[i] = (double)i / (double)samplingRate;
	    	yArr[i] = 0;
	    }

	    int cueDuration = 4 * samplingRate;
	    for(int i=0; i<times.size(); i++) {
	    	for(int y=0; y<cueDuration && (int)(times.get(i) - fromSample) + y < yArr.length; y++) {
		    	yArr[(int)(times.get(i) - fromSample) + y]
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
