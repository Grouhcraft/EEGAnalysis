package main;

import filters.CutOff;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.dm.SGTMetaData;
import gov.noaa.pmel.sgt.dm.SimpleLine;
import gov.noaa.pmel.sgt.swing.JPlotLayout;
import gov.noaa.pmel.util.Point2D;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GraphWindow extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2649145625138349841L;
	private JPanel contentPane;
	private int[] channels = {1,2};
	private int subSampling = 1;
	private JPlotLayout graphALayout;
	private JPlotLayout graphBLayout;
	private String dataFile = "F:\\BCICIV_1_asc\\BCICIV_eval_ds1a_cnt.txt";
	private int channelsCount = 59;
	private int dataFreq = 1000;
	private int LowCutOff = 30;
	private int HighCutOff = 5;
	private int cutOffPasses = 3;
	private int[] frequencyRange = {13,30};
	private int[] timeFrame = {10,11};
	
	private final int X = 0;
	private final int Y = 1;
	

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
					getDataId(dataFile, subSampling, channels[0], LowCutOff, HighCutOff));
		} else {
			return !((SGTData)graphBLayout.getData().firstElement()).getId().equals(
					getDataId(dataFile, subSampling, channels[1], LowCutOff, HighCutOff));
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
			SGTData dataA = readTheData(channels[0]);
			graphALayout.addData(dataA);
			
			graphALayout.setBatch(false);
		}
		
		if(force || settingsChanged("B")) {
			graphBLayout.setBatch(true);
			
			graphBLayout.clear();
			SGTData dataB = readTheData(channels[1]);
			graphBLayout.addData(dataB);
			
			graphBLayout.setBatch(false);
		}
 	}

	/**
	 * Create the frame
	 */
	public GraphWindow() {
		setTitle("EEG Viewer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 882, 410);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		graphALayout = new JPlotLayout(false, false, false, false, "A", null, false);
		graphBLayout = new JPlotLayout(false, false, false, false, "B", null, false);
		graphALayout.setTitles("Ch.A", "", "");
		graphBLayout.setTitles("Ch.B", "", "");
		
		setWaveClass(WavesClasses.GAMMA);
		updateGraphs(true);
				
		setContentPane(contentPane);
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
		
		// Horizontal
		gl_contentPane.setHorizontalGroup(
				gl_contentPane.createSequentialGroup()
						
					// Graph A, Boutons
					.addGroup(gl_contentPane.createParallelGroup()
						.addComponent(graphALayout)
						.addGroup(gl_contentPane.createSequentialGroup().addComponent(btnPrevA).addComponent(btnNextA))
					)
							
					// Graph B, Boutons
					.addGroup(gl_contentPane.createParallelGroup()
						.addComponent(graphBLayout)
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
						.addComponent(graphALayout)
						.addComponent(graphBLayout)
					)
					
					// Boutons prevA, prevB, nextA, nextB
					.addGroup(gl_contentPane.createParallelGroup()
						.addComponent(btnPrevA)
						.addComponent(btnPrevB)
						.addComponent(btnNextA)
						.addComponent(btnNextB)
					)
		);
		
		contentPane.setLayout(gl_contentPane);
	}

	/**
	 * Reads the EEG data from {@link #dataFile}   
	 * @param channel EEG Channel to read 
	 * @return the SGTData data used by the graph layouts
	 */
	private SGTData readTheData(int channel) {
		return readTheData(dataFile, subSampling, channel, LowCutOff, HighCutOff);
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
	private SGTData readTheData(String file, int subsamplingFactor, int channel, int LowCutOff, int HighCutOff) {
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
	    int toSkip = (timeFrame[0] * dataFreq) / subsamplingFactor;
		while(line != null && i<toSkip) {
			try {
				line = in.readLine();
				i++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
		int toRead = ((timeFrame[1] - timeFrame[0]) * dataFreq) / subsamplingFactor; 
	    i = 0;
	    while(line != null && i < toRead) {
	    	x = i; 
	    	y = Integer.parseInt(line.split("\t")[channel]);
	    	p = new Point2D.Double(x, y);
	    	list.add(p);
	    	
	    	i++;
	    	try {
	    		for(int ii=0; ii < (subsamplingFactor > 1 ? subsamplingFactor-1 : 1); ii++ ) {
	    			line = in.readLine();
	    		}
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    Logger.log("parsing " + i/1000 + "K samples (over " + (i*subsamplingFactor)/1000 + "K ones) from " 
	    		+ dataFreq + "Hz channel " + channel + "'s data => " 
	    		+ (i*subsamplingFactor)/dataFreq + "s record"
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
	    data.setId(getDataId(file,subsamplingFactor, channel, LowCutOff, HighCutOff));
	    data.setXMetaData(new SGTMetaData("Time", "1000 / " + subsamplingFactor + " Hz", false, false));
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
		LowCutOff = wc.getUpperAmpl();
		HighCutOff = wc.getLowerAmpl();
		frequencyRange = new int[] {
			wc.getLowerFreq(),
			wc.getUpperFreq()
		};
	}
	
	/**
	 * Apply treatments to the signal data then transforms it to a {@link SimpleLine}, 
	 * used by the {@link JPlotLayout graph}
	 * @param data index 0 = X, index 1 = Y
	 * @return the drawable SimpleLine curve
	 */
	private SimpleLine processSignal(double[][] data) {
		if(LowCutOff > 0 || HighCutOff > 0) {
			Logger.log("applying cutoff " 
					+ "Low:" + ((LowCutOff > 0) ? LowCutOff + "µV " : "none ") 
					+ "High:" + ((HighCutOff > 0) ? HighCutOff + "µV " : "none ")
					+"(" + cutOffPasses + "passes)");
			
			for(int i=0; i<cutOffPasses; i++) {
				if(HighCutOff > 0) data = CutOff.highAmplitude(data, HighCutOff);
				if(LowCutOff > 0) data = CutOff.lowAmplitude(data, LowCutOff);
			}
		}
		
		if(frequencyRange[0] > 0 || frequencyRange[1] > 0) {
			Logger.log("showing frequency range [" + frequencyRange[0] + " ; " + frequencyRange[1] + "]");
			data = CutOff.frequencyRange(data, frequencyRange[0], frequencyRange[1]);
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
	private String getDataId(String file, int sampling, int channel, int LowCutOff, int HighCutOff) {
		return file + sampling + "_" + channel + "_" + LowCutOff + "_" + HighCutOff;
	}

	/**
	 * Manage the UI buttons events, update the graphs accordingly
	 * @see {@link #updateGraphs()}
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("preva")) {
			if(channels[0] > 1) {
				channels[0]--;
			}
		} else if (e.getActionCommand().equals("nexta")) {
			if(channels[0] < channelsCount) {
				channels[0]++;
			}
		} else if (e.getActionCommand().equals("prevb")) {
			if(channels[1] > 1) {
				channels[1]--;
			}
		} else if (e.getActionCommand().equals("nextb")) {
			if(channels[0] < channelsCount) {
				channels[1]++;
			}
		} else if (e.getActionCommand().equals("decrease_sampling")) {
				subSampling *= 10;
		} else if (e.getActionCommand().equals("increase_sampling")) {
			if(subSampling >= 10) {
				subSampling /= 10;
			}
		} else if (e.getActionCommand().equals("increase_cutoff")) {
			if(HighCutOff < 2000) {
				HighCutOff += 200;
			}
		} else if (e.getActionCommand().equals("decrease_cutoff")) {
			if(HighCutOff >= 200) {
				HighCutOff -= 200;
			}
		}
		updateGraphs();
	}
}
