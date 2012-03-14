package main;

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
import java.util.List;

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
	private int subSampling = 100;
	private JPlotLayout graphALayout;
	private JPlotLayout graphBLayout;
	private String dataFile = "F:\\BCICIV_1_asc\\BCICIV_eval_ds1a_cnt.txt";
	private int channelsCount = 59;
	private int dataFreq = 1000;
	private int cutOff = 400;

	
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
	
	public void updateGraphs() { 
		updateGraphs(false);
	}
	
	public void updateGraphs(Boolean force) {
		if(force || !((SGTData)graphALayout.getData().firstElement()).getId().equals(
				getDataId(dataFile, subSampling, channels[0], cutOff)) ) {
			graphALayout.setBatch(true);
			
			graphALayout.clear();
			SGTData dataA = readTheData(dataFile, subSampling, channels[0], cutOff);
			graphALayout.addData(dataA);
			
			graphALayout.setBatch(false);
		}
		
		if(force || !((SGTData)graphBLayout.getData().firstElement()).getId().equals(
				getDataId(dataFile, subSampling, channels[1], cutOff)) ) {
			graphBLayout.setBatch(true);
			
			graphBLayout.clear();
			SGTData dataB = readTheData(dataFile, subSampling, channels[1], cutOff);
			graphBLayout.addData(dataB);
			
			graphBLayout.setBatch(false);
		}
 	}

	/**
	 * Create the frame.
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

	private SGTData readTheData(String file, int subsamplingFactor, int channel, int cutOff) {
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
	    
	    int i = 0;
	    while(line != null) {
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
	    Logger.log("parsing " + (i*subsamplingFactor)/1000 + "K samples from " 
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
	    data.setId(getDataId(file,subsamplingFactor, channel, cutOff));
	    data.setXMetaData(new SGTMetaData("Time", "1000 / " + subsamplingFactor + " Hz", false, false));
	    data.setYMetaData(new SGTMetaData("Potential", "µV", false, false));
	    
		return data;
	}
	
	private SimpleLine processSignal(double[][] data) {
		if(cutOff > 0) {
			data = highAmplitudeCutOff(data, cutOff);
			data = highAmplitudeCutOff(data, cutOff);
			data = highAmplitudeCutOff(data, cutOff);
		}
	    return new SimpleLine(data[X], data[Y], null);
	}

	private double[][] highAmplitudeCutOff(double[][] data, double amplThreshold) {
		double f[] = data[Y];
		List<Integer> toErease = new ArrayList<Integer>();
		
		int xx = 1;
		while(xx < f.length-2) {
			int x = xx;
			//Logger.log("xx=" + xx +  " f[x]=" + f[x] + " f[x+1]=" + f[x+1]);
			
			// Ca monte
			if(f[x+1] > f[x]) {			
				while((x+1 < f.length-1) && (f[x] < f[x+1] || f[x] == f[x+1])) {x++;}
				
				// ça redescend
				if(f[x+1] < f[x] && Math.abs(f[xx] - f[x]) < amplThreshold) {
					for(int i=xx; i<=x; i++) {
						toErease.add(i);
					}
				}
				
				xx = x+1;
				continue;
			} 
			
			// Ca descend 
			else if (f[x+1] < f[x]) {	
				while((x+1 < f.length-1) &&  (f[x] > f[x+1])) {x++;}
				
				// ça remonte
				if(f[x+1] > f[x] && Math.abs(f[xx] - f[x]) < amplThreshold) {
					for(int i=xx; i<=x; i++) {
						toErease.add(i);
					}
				}
				
				xx = x+1;
				continue;
			} 
			
			// Ca stagne 
			else {
				while((x+1 < f.length-1) && (f[x] == f[x+1])) {x++;}
				xx = x+1;
				continue;
			}
		}
		Logger.log(toErease.size() + " samples droped");
		
		double[] xArr = new double[data[Y].length - toErease.size()];
		double[] yArr = new double[data[Y].length - toErease.size()];
		
		for(int i=0, ii=0; i<data[Y].length; i++) {
			if(!toErease.contains(i)) {
				xArr[ii] = (double) i;
				yArr[ii] = data[Y][i];
				ii++;
			}
		}
		
		return new double[][] { xArr, yArr };
	}

	private String getDataId(String file, int sampling, int channel, int cutOff) {
		return file + sampling + "_" + channel + "_" + cutOff;
	}

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
			if(cutOff < 2000) {
				cutOff += 200;
			}
		} else if (e.getActionCommand().equals("decrease_cutoff")) {
			if(cutOff >= 200) {
				cutOff -= 200;
			}
		}
		updateGraphs();
	}
}
