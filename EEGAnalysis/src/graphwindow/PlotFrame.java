package graphwindow;

import gov.noaa.pmel.sgt.LineAttribute;
import gov.noaa.pmel.sgt.dm.SGTData;
import gov.noaa.pmel.sgt.swing.JPlotLayout;
import graphwindow.plot.IPlot;
import graphwindow.plot.Plot;
import graphwindow.plot.WaveformPlot;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import main.MainWindow;
import main.utils.Logger;

/**
 * PlotFrame is the window containing a plot
 * @author knoodrake
 * @see Plot
 */
public class PlotFrame extends JInternalFrame implements ActionListener {

	private static final long serialVersionUID = 2796714104577643465L;
	private JPlotLayout plotLayout;
	private IPlot plot;
	private HashMap<String, SGTData> linkedDatas = new HashMap<String, SGTData>();
	public IPlot getPlot() {
		return plot;
	}
	
	/**
	 * This constructor create a new PlotFrame by cloning another.
	 * Note that nothing is <i>programmaticaly</i> cloned, it's just that 
	 * every settings and data sources are copied. 
	 * @see PlotFrame#PlotFrame(String, int, File)
	 */
	public PlotFrame(String plotId, PlotFrame p) {
		initialize(plotId, p.getPlot().getInfos().channel, p.getDataFile());
		plot.setWaveClass(p.getPlot().getWaveClass());
		updateGraph();
	}
	
	/**
	 * Constructor. Note that most stuff is done in {@link #initialize(String, int, File)}
	 * @param plotID	the ID string of the plot, currently also used as the frame title 
	 * @param channel	the channel initialy used
	 * @param file		the datafile initialy used
	 * @see PlotFrame#PlotFrame(String, PlotFrame)
	 * @wbp.parser.constructor
	 */
	public PlotFrame(String plotID, int channel, File file) {
		initialize(plotID, channel, file);
	}
	
	/**
	 * Called by the constructor
	 * @param plotID
	 * @param channel
	 * @param file
	 */
	private void initialize(String plotID, int channel, File file) {
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosed(InternalFrameEvent arg0) {
				MainWindow.getInstance().removePlot((PlotFrame)arg0.getInternalFrame());
			}
		});
		setClosable(true);
		setIconifiable(true);
		setMaximizable(true);
		setResizable(true);
		setSize(600, 400);
		setTitle("plot #" + plotID);
		JMenuBar menuBar = new GraphMenu(this);
		setJMenuBar(menuBar);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		plotLayout = new JPlotLayout(false, false, false, false, plotID, null, false);
		panel.add(plotLayout, BorderLayout.CENTER);

		
		JButton btnPrev = new JButton("<< Prev Ch.");
		JButton btnNext = new JButton("Next Ch. >>");

		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnPrev, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNext, GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnPrev)
						.addComponent(btnNext))
					.addContainerGap())
		);
		getContentPane().setLayout(groupLayout);
		
		btnPrev.addActionListener(this);
		btnNext.addActionListener(this);
		btnPrev.setActionCommand("prev");
		btnNext.setActionCommand("next");
		
		this.plot = new WaveformPlot(channel, file);
		setWaveClass(WaveClass.ALPHA);
		
		setVisible(true);
	}	

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("prev")) {
			if(plot.getInfos().channel > 1) {
				plot.getInfos().channel--;
			}
		} else if (e.getActionCommand().equals("next")) {
			if(plot.getInfos().channel < plot.getInfos().channelsCount) {
				plot.getInfos().channel++;
			}
		}
		updateGraph();
	}

	/**
	 * Updates the {@link Plot} with the current parameters and data.
	 * This has for effect to reperform all the analysis and processing steps. 
	 */
	public void updateGraph() {
		plotLayout.setBatch(true);
		plot.update();
		plotLayout.clear();
		plotLayout.addData(plot.getData(), new LineAttribute(LineAttribute.SOLID, Color.MAGENTA));
		plotLayout.setTitles(
				"Channel #" + plot.getInfos().channel + "(" + plot.getInfos().getChannelCode() +")", 
				"Waves: " + plot.getWaveClass().getName(), 
				plot.getInfos().file.getName());
		for(SGTData linkedData : linkedDatas.values()) {
			plotLayout.addData(linkedData);
		}

		if(plot.getClass().isAssignableFrom(WaveformPlot.class)) {
			try {
				plotLayout.addData((new DataFileReader()).metaDataReader.readFromPlot(plot));
			} catch (IOException e) {
				Logger.log(e.getMessage());
			}
		}
		plotLayout.setBatch(false);
	}

	/**
	 * Assigns a {@link WaveClass}, wich results in setting some 
	 * wave class attributes like frequency range filtering, etc..  
	 * @param waveClass
	 */
	public void setWaveClass(WaveClass waveClass) {
		plot.setWaveClass(waveClass);
		updateGraph();
	}

	public File getDataFile() {
		return plot.getInfos().file;
	}

	public void setDataFile(File selectedFile) {
		plot.getInfos().file = selectedFile;
	}

	public void setGraphType(Class<? extends Plot> graphType) {
		try {
			plot = graphType.getConstructor(IPlot.class).newInstance(plot);
		} catch (Exception e) {
			e.printStackTrace();
		}
		updateGraph();
	}

	/**
	 * Adds the curve of another {@link PlotFrame} to the plot
	 * @param plotTitle
	 */
	public void linkPlot(String plotTitle) {
		linkedDatas.put(plotTitle, MainWindow.getInstance().getPlotByTitle(plotTitle).getPlot().getData());
		updateGraph();
	}
	
	/**
	 * Returns true if some others curves are currently shown in the plot 
	 * @see {@link #linkPlot(String)}
	 */
	public boolean hasLinkedData() {
		return !linkedDatas.isEmpty();
	}

	/**
	 * Clears all linked graphs if there is any
	 * @see {@link #linkPlot(String)}
	 */
	public void unlinkAll() {
		linkedDatas.clear();
		updateGraph();
	}
}
