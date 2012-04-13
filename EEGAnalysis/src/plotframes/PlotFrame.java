package plotframes;

import gov.noaa.pmel.sgt.swing.JPlotLayout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.annotation.AnnotationFormatError;
import java.util.HashMap;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import plotframes.components.GraphContextualMenu;
import plotframes.components.GraphMenu;
import plotframes.components.GraphSettingsPanel;
import plotframes.data.WaveClass;
import plotframes.graphlayouts.IGraphLayout;
import plotframes.plots.IPlot;
import plotframes.plots.Plot;
import plotframes.plots.annotations.graphtype;
import plotframes.plots.implementations.WaveformPlot;

import utils.Logger;

import main.MainWindow;
import main.R;

/**
 * PlotFrame is the window containing a plot
 * @author knoodrake
 * @see Plot
 */
public class PlotFrame extends JInternalFrame implements ActionListener {
	private static final long serialVersionUID = 2796714104577643465L;
	private IGraphLayout plotLayout;
	private IPlot plot;
	private HashMap<String, Object> linkedDatas = new HashMap<>();
	private String plotId;
	private JPanel plotPanel;
	private JButton btnShowSettings;
	private GraphSettingsPanel dynSettingsPanel;
	public IPlot getPlot() {
		return plot;
	}
	static {
		String packageName = IPlot.class.getPackage().getName() + ".implementations.";
		for(String graphClassName : R.get("graphtypes").split(",")) {
			try {
				Logger.log("Loading graph type \"" + graphClassName + "\"");
				IPlot.graphTypes.add((Class<? extends IPlot>) Class.forName(packageName + graphClassName));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
		setSize(600, 500);
		setMinimumSize(new Dimension(240,250));
		setTitle("plot #" + plotID);
		plotId = plotID;
		JMenuBar menuBar = new GraphMenu(this);
		setJMenuBar(menuBar);

		plotPanel = new JPanel();
		JPanel btnPanel = new JPanel();
		plotPanel.setLayout(new BorderLayout());
		btnPanel.setLayout(new GridBagLayout());

		plot = new WaveformPlot(channel, file);
		try {
			plotLayout = getGraphTypeFor(plot.getClass()).getConstructor(String.class).newInstance(plotID);
		} catch (Exception e) {
			e.printStackTrace();
			dispose();
			return;
		}
		plotPanel.add((Component) plotLayout, BorderLayout.CENTER);

		JButton btnAvg	= new JButton("View Avg.");
		JButton btnPrev = new JButton("<< Prev Ch.");
		JButton btnNext = new JButton("Next Ch. >>");
		btnShowSettings = new JButton("Show settings");
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.weightx = 0.2;
		btnPanel.add(btnAvg, c);
		c.gridx = 1;
		c.weightx = 0.5;
		btnPanel.add(btnPrev, c);
		c.gridx = 2;
		c.weightx = 0.5;
		btnPanel.add(btnNext, c);
		c.gridx = 3;
		c.weightx = 0.2;
		btnPanel.add(btnShowSettings, c);

		dynSettingsPanel = new GraphSettingsPanel(this);		
		dynSettingsPanel.setVisible(false);
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		groupLayout.setHorizontalGroup(
			groupLayout.createSequentialGroup()
			.addGroup(groupLayout.createParallelGroup()
				.addComponent(plotPanel, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.DEFAULT_SIZE)
				.addComponent(btnPanel, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.DEFAULT_SIZE)
			).addComponent(dynSettingsPanel, 175, 200, 500)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup()
				.addComponent(dynSettingsPanel)
			.addGroup(groupLayout.createSequentialGroup()
				.addComponent(plotPanel, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.DEFAULT_SIZE)
				.addComponent(btnPanel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
		));
		getContentPane().setLayout(groupLayout);

		btnPrev.addActionListener(this);
		btnNext.addActionListener(this);
		btnAvg.addActionListener(this);
		btnShowSettings.addActionListener(this);
		btnPrev.setActionCommand("prev");
		btnNext.setActionCommand("next");
		btnAvg.setActionCommand("avg_channels");
		btnShowSettings.setActionCommand("swap_settings_visibility");

		setWaveClass(WaveClass.NONE);

		final PlotFrame that = this;
		((Component)plotLayout).addMouseListener(new MouseAdapter() {
		    @Override
			public void mousePressed(MouseEvent e){
		        if (e.isPopupTrigger())
		            doPop(e);
		    }

		    @Override
			public void mouseReleased(MouseEvent e){
		        if (e.isPopupTrigger())
		            doPop(e);
		    }

		    private void doPop(MouseEvent e){
		    	GraphContextualMenu menu = new GraphContextualMenu(that, plotLayout.supportZooming());
		        menu.show(e.getComponent(), e.getX(), e.getY());
		    }

		});

		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case "prev":
			if(plot.getInfos().channel > 1) 
				plot.getInfos().channel--;
			break;
		case "next":
			if(plot.getInfos().channel < plot.getInfos().channelsCount)
				plot.getInfos().channel++;
			break;
		case "swap_settings_visibility":
			if(dynSettingsPanel.isVisible()) {
				dynSettingsPanel.setVisible(false);
				btnShowSettings.setText("Show Settings");
			} else {
				dynSettingsPanel.setVisible(true);
				btnShowSettings.setText("Hide Settings");
			}
			revalidate();
			break;
		case "avg_channels":
			plot.setChannelsAveraged(!plot.areChannelsAveraged());
			break;
		}
		updateGraph();
	}

	/**
	 * Updates the {@link Plot} with the current parameters and data.
	 * This has for effect to reperform all the analysis and processing steps.
	 */
	public void updateGraph() {
		plotLayout.beginOperations();
		plot.update();
		plotLayout.clear();
		plotLayout.addData(plot.getData());
		plotLayout.setTitles(new String[]{
				"Channel #" + plot.getInfos().channel + "(" + plot.getInfos().getChannelCode() +")",
				"Waves: " + plot.getWaveClass().getName(),
				plot.getInfos().file.getName()});
		for(Object linkedData : linkedDatas.values()) {
			plotLayout.addData(linkedData);
		}
/*
		if(plot.getClass().isAssignableFrom(WaveformPlot.class)) {
			try {
				plotLayout.addData((new DataFileReader()).getMetaDataReader().readFromPlot(plot));
			} catch (IOException e) {
				Logger.log(e.getMessage());
			}
		}
		*/
		dynSettingsPanel.parseSettingsFrom(plot);
		plotLayout.endOperations();
	}
	
	public void updateLayout() {
		((JPlotLayout)plotLayout).resetZoom();
		((JPlotLayout)plotLayout).revalidate();
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

	/**
	 * Returns the {@link IGraphLayout} derived {@link Class} that can be used to
	 * render the given type of graph.
	 * @param clss graph to render in a layout
	 * @return the class of a suitable layout
	 */
	public static Class<? extends IGraphLayout> getGraphTypeFor(Class<? extends IPlot> clss) {
		if(!clss.isAnnotationPresent(graphtype.class))
			throw new AnnotationFormatError("Plots extendings IPlot must have annotation @graphtype");
		graphtype gt = clss.getAnnotation(graphtype.class);
		return gt.layout();
	}

	public void setGraphType(Class<? extends Plot> graphType) {
		try {
			plot = graphType.getConstructor(IPlot.class).newInstance(plot);
			MouseAdapter m = (MouseAdapter) ((Component)plotLayout).getMouseListeners()[1];
			plotLayout = getGraphTypeFor(plot.getClass()).getConstructor(String.class).newInstance(plotId);
			((Component)plotLayout).addMouseListener(m);
			plotPanel.remove(0);
			plotPanel.add((Component) plotLayout);
			plotPanel.validate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			updateGraph();
		}
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

	public void copyZoom() throws Exception {
		if(!plotLayout.supportZooming())
			throw new Exception("Graph layout dont support zooming !");
		MainWindow.zoom = plotLayout.getZoom();
	}

	public void pasteZoom() throws Exception {
		if(!plotLayout.supportZooming())
			throw new Exception("Graph layout dont support zooming !");
		plotLayout.setZoom(MainWindow.zoom);
	}
}
