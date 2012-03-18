package main;

import filters.CutOff;
import filters.FFT;
import filters.WelchMethod;
import gov.noaa.pmel.sgt.Attribute;
import gov.noaa.pmel.sgt.LineAttribute;
import gov.noaa.pmel.sgt.dm.PointCollection;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import gov.noaa.pmel.sgt.swing.JPlotLayout;
import gov.noaa.pmel.util.Point2D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;

public class GraphWindow extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2649145625138349841L;
	private JPanel contentPane;
	private JPlotLayout graphALayout;
	private JPlotLayout graphBLayout;

	static class DataSettings  {
		int channelsCount = 59;
		int frequency = 100;
		File file = new File(System.getenv("EEGDATA") + "\\" + R.get("datafile"));
		File markerFile = new File(System.getenv("EEGDATA") + "\\" + R.get("markerfile"));
		int subSampling = 1;
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
		int from = 43;
		int to = 49;
	} 
	
	static class Channels {
		int a = 1;
		int b = 57;
		String[] code = new String[] {
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
	
	private Channels channels = new Channels();
	private TimeFrame timeFrame = new TimeFrame();
	private FrequencyRange frequencyRange = new FrequencyRange();
	private AmplitudeCutoff amplitudeCutoff = new AmplitudeCutoff();
	private DataSettings dataSettings = new DataSettings();
	
	private final int X = 0;
	private final int Y = 1;
	private WaveClass waveClass;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GraphWindow frame = new GraphWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Update the two graph layouts if the underlying data has changed
	 */
	public void updateGraphs() { 
		updateGraphs(false);
	}
	
	/**
	 * Indicate if the data used by the given graph layout has changed since his last update 
	 * @param graphID the string identifying the graph layout
	 * @return true if the data has changed
	 * @see {@link #updateGraphs()}
	 */
	private boolean settingsChanged(String graphID) {
		if(graphID.equals("A")) {
			return !((SGTData)graphALayout.getData().firstElement()).getId().equals(
					getDataId(dataSettings.file, dataSettings.subSampling, channels.a, amplitudeCutoff, timeFrame, frequencyRange));
		} else {
			return !((SGTData)graphBLayout.getData().firstElement()).getId().equals(
					getDataId(dataSettings.file, dataSettings.subSampling, channels.b, amplitudeCutoff, timeFrame, frequencyRange));
		}
	}
	/**
	 * Update the two graph layouts if the underlying data has changed
	 * @param force Update the graph without checking if data, used for the first update.
	 * @see {@link #updateGraphs()} 
	 */
	private void updateGraphs(Boolean force) {
		if(force || settingsChanged("A")) {
			graphALayout.setBatch(true);
			
			graphALayout.clear();
			SGTData dataA = readTheData(channels.a);
			graphALayout.addData(dataA, new LineAttribute(LineAttribute.SOLID, Color.MAGENTA));
	/*		
			// Graph markers
			double startLimit = (Double) dataA.getXRange().getStart().getObjectValue();
			double endLimit = (Double) dataA.getXRange().getEnd().getObjectValue();
			double maxYValue = (Double) dataA.getYRange().getEnd().getObjectValue();
			double minYValue = (Double) dataA.getYRange().getStart().getObjectValue();
			SGTData makerData = readTheMarkerData(startLimit, endLimit, minYValue, maxYValue);
			graphALayout.addData(makerData, new LineAttribute(LineAttribute.HEAVY, Color.LIGHT_GRAY));

			// --------------
		*/	
			graphALayout.setTitles("Channel #" + channels.a + " (" + channels.code[channels.a]+ ")", waveClass.getName(), dataSettings.file.getName());
			
			graphALayout.setBatch(false);

		}
		
		if(force || settingsChanged("B")) {
			graphBLayout.setBatch(true);
			
			graphBLayout.clear();
			SGTData dataB = readTheData(channels.b);
			graphBLayout.addData(dataB);
			graphBLayout.setTitles("Channel #" + channels.b + " (" + channels.code[channels.b]+ ")", waveClass.getName(), dataSettings.file.getName());

			graphBLayout.setBatch(false);

		}
 	}

	/**
	 * Create the frame
	 */
	public GraphWindow() {
		JMenuBar menuBar = new GraphMenu(this);
		setJMenuBar(menuBar);
		setTitle("EEG Viewer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 882, 410);
		setLayout(new BorderLayout());
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(contentPane, BorderLayout.CENTER); 
		
		graphALayout = new JPlotLayout(false, false, false, false, "A", null, false);
		graphBLayout = new JPlotLayout(false, false, false, false, "B", null, false);
		
		setWaveClass(WaveClass.GAMMA);
		updateGraphs(true);

		JButton btnPrevA = new JButton("<< Prev Ch.");
		JButton btnNextA = new JButton("Next Ch. >>");		
		JButton btnPrevB = new JButton("<< Prev. Ch.");
		JButton btnNextB = new JButton("Next Ch. >>");
		btnPrevA.addActionListener(this);
		btnNextA.addActionListener(this);
		btnPrevB.addActionListener(this);
		btnNextB.addActionListener(this);
		btnPrevA.setActionCommand("preva");
		btnNextA.setActionCommand("nexta");
		btnPrevB.setActionCommand("prevb");
		btnNextB.setActionCommand("nextb");
		
		JButton btnPlus = new JButton("+");
		JButton btnLess = new JButton("-");
		JButton btnMoreCutoff = new JButton(">");
		JButton btnLessCutoff = new JButton("<");
		btnPlus.setFont(new Font("Tahoma", Font.BOLD, 8));
		btnLess.setFont(new Font("Tahoma", Font.BOLD, 8));
		btnMoreCutoff.setFont(new Font("Tahoma", Font.BOLD, 8));
		btnLessCutoff.setFont(new Font("Tahoma", Font.BOLD, 8));
		btnMoreCutoff.addActionListener(this);
		btnLessCutoff.addActionListener(this);
		btnPlus.addActionListener(this);
		btnLess.addActionListener(this);
		btnPlus.setActionCommand("increase_sampling");
		btnLess.setActionCommand("decrease_sampling");
		btnMoreCutoff.setActionCommand("increase_cutoff");
		btnLessCutoff.setActionCommand("decrease_cutoff");
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setAutoCreateGaps(true);
		JPanel graphAPanel = new JPanel();
		JPanel graphBPanel = new JPanel();
		graphAPanel.setBorder(BorderFactory.createLineBorder(Color.red, 2));
		graphBPanel.setBorder(BorderFactory.createLineBorder(Color.blue, 2));
		graphAPanel.setLayout(new BorderLayout());
		graphBPanel.setLayout(new BorderLayout());
		graphAPanel.add(graphALayout, BorderLayout.CENTER);
		graphBPanel.add(graphBLayout, BorderLayout.CENTER);
		
		
		// Horizontal
		gl_contentPane.setHorizontalGroup(
				gl_contentPane.createSequentialGroup()
						
					// Graph A, Boutons
					.addGroup(gl_contentPane.createParallelGroup()
						.addComponent(graphAPanel)
						.addGroup(gl_contentPane.createSequentialGroup().addComponent(btnPrevA).addComponent(btnNextA))
					)
							
					// Graph B, Boutons
					.addGroup(gl_contentPane.createParallelGroup()
						.addComponent(graphBPanel)
						.addGroup(gl_contentPane.createSequentialGroup().addComponent(btnPrevB).addComponent(btnNextB))
					)
							
					// Boutons + / -
					.addGroup(gl_contentPane.createParallelGroup()
						.addComponent(btnPlus)
						.addComponent(btnLess)
						.addComponent(btnMoreCutoff)
						.addComponent(btnLessCutoff)
					)
		);
		
		// Vertical
		gl_contentPane.setVerticalGroup(
				gl_contentPane.createSequentialGroup()
					
					// Boutons + / - , Graph A, Graph B
					.addGroup(gl_contentPane.createParallelGroup()
						.addGroup(gl_contentPane.createSequentialGroup()
								.addComponent(btnPlus)
								.addComponent(btnLess)
								.addComponent(btnMoreCutoff)
								.addComponent(btnLessCutoff)
								)
						.addComponent(graphAPanel)
						.addComponent(graphBPanel)
					)
					
					// Boutons prevA, prevB, nextA, nextB
					.addGroup(gl_contentPane.createParallelGroup()
						.addComponent(btnPrevA)
						.addComponent(btnPrevB)
						.addComponent(btnNextA)
						.addComponent(btnNextB)
					)
		);
		
		addComponentListener(new java.awt.event.ComponentAdapter() {
			  public void componentResized(ComponentEvent event) {
				  	
				  }
			});
		
		contentPane.setLayout(gl_contentPane);
		pack();
	}

	/**
	 * Reads the EEG data from {@link #dataFile}   
	 * @param channel EEG Channel to read 
	 * @return the SGTData data used by the graph layouts
	 */
	private SGTData readTheData(int channel) {
		return readTheData(dataSettings.file, channel );
	}
	
	
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
	    	double plopStartLimit = timeFrame.from * dataSettings.frequency;
	    	double plopEndLimit = timeFrame.to * dataSettings.frequency;
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
	    	for(int y=0; y<cueDuration * dataSettings.frequency &&
	    			(int)(times.get(i) - timeFrame.from * dataSettings.frequency) + y < yArr.length 
	    			; y++) {
		    	yArr[(int)(times.get(i) - timeFrame.from * dataSettings.frequency) + y] 
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
	    int toSkip = (timeFrame.from * dataSettings.frequency) / dataSettings.subSampling;
		while(line != null && i<toSkip) {
			try {
				line = in.readLine();
				i++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
		int toRead = ((timeFrame.to - timeFrame.from) * dataSettings.frequency) / dataSettings.subSampling; 
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
	    		+ dataSettings.frequency + "Hz channel " + channel + "'s data => " 
	    		+ (i*dataSettings.subSampling)/dataSettings.frequency + "s record"
	    		);
	    
	    
	    double xArr[] = new double[list.size()];
	    double yArr[] = new double[list.size()];
	    Iterator<Point2D> it = list.iterator();
	    i = 0;
	    while(it.hasNext()) {
	    	p = (Point2D.Double) it.next();
	    	xArr[i] = p.x;
	    	yArr[i] = p.y;
	    	i++;
	    }
	    SimpleLine data = processSignal(new double[][] {xArr, yArr});
	    data.setId(getDataId(file,dataSettings.subSampling, channel, amplitudeCutoff, timeFrame, frequencyRange));
	    data.setXMetaData(new SGTMetaData("Samples", dataSettings.frequency / dataSettings.subSampling + " Hz", false, false));
	    data.setYMetaData(new SGTMetaData("Potential", "µV", false, false));
	    
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
		if(amplitudeCutoff.low > 0 || amplitudeCutoff.high > 0) {
			Logger.log("applying cutoff " 
					+ "Low:" + ((amplitudeCutoff.low > 0) ? amplitudeCutoff.low + "µV " : "none ") 
					+ "High:" + ((amplitudeCutoff.high > 0) ? amplitudeCutoff.high + "µV " : "none ")
					+"(" + amplitudeCutoff.passes + "passes)");
			
			for(int i=0; i<amplitudeCutoff.passes; i++) {
			//	if(amplitudeCutoff.high > 0) data = CutOff.highAmplitude(data, amplitudeCutoff.high);
			//	if(amplitudeCutoff.low > 0) data = CutOff.lowAmplitude(data, amplitudeCutoff.low);
			}
		}
		
		if(frequencyRange.lower > 0 || frequencyRange.higher > 0) {
		//	Logger.log("showing frequency range [" + frequencyRange.lower + " ; " + frequencyRange.higher + "]");
		//	data = CutOff.frequencyRange(data, frequencyRange.lower, frequencyRange.higher);
		}
		data = WelchMethod.compute(data);
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

	/**
	 * Manage the UI buttons events, update the graphs accordingly
	 * @see {@link #updateGraphs()}
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("preva")) {
			if(channels.a > 1) {
				channels.a--;
			}
		} else if (e.getActionCommand().equals("nexta")) {
			if(channels.a < dataSettings.channelsCount) {
				channels.a++;
			}
		} else if (e.getActionCommand().equals("prevb")) {
			if(channels.b > 1) {
				channels.b--;
			}
		} else if (e.getActionCommand().equals("nextb")) {
			if(channels.a < dataSettings.channelsCount) {
				channels.b++;
			}
		} else if (e.getActionCommand().equals("decrease_sampling")) {
			dataSettings.subSampling *= 10;
		} else if (e.getActionCommand().equals("increase_sampling")) {
			if(dataSettings.subSampling >= 10) {
				dataSettings.subSampling /= 10;
			}
		}
		updateGraphs();
	}
	
	public File getDataFile() {
		return dataSettings.file;
	}

	public void setDataFile(File dataFile) {
		dataSettings.file = dataFile;
	}
}
