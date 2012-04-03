package graphwindow;

import gov.noaa.pmel.sgt.dm.SGTData;
import graphwindow.graphlayouts.IGraphLayout;
import graphwindow.plot.IPlot;
import graphwindow.plot.Plot;
import graphwindow.plot.graphtype;
import graphwindow.plot.implementations.GraphSetting;
import graphwindow.plot.implementations.WaveformPlot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;
import java.util.HashMap;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import main.MainWindow;
import main.R;
import main.utils.Logger;

/**
 * PlotFrame is the window containing a plot
 * @author knoodrake
 * @see Plot
 */
public class PlotFrame extends JInternalFrame implements ActionListener {
	private static final long serialVersionUID = 2796714104577643465L;
	private IGraphLayout plotLayout;
	private IPlot plot;
	private HashMap<String, SGTData> linkedDatas = new HashMap<String, SGTData>();
	private String plotId;
	private JPanel plotPanel;
	private JScrollPane dynSettingsSB;
	private JButton btnShowSettings;
	private JPanel dynSettingsPanel;
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

		JButton btnPrev = new JButton("<< Prev Ch.");
		JButton btnNext = new JButton("Next Ch. >>");
		btnShowSettings = new JButton("Show settings");
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.weightx = 0.5;
		btnPanel.add(btnPrev, c);
		c.gridx = 1;
		c.weightx = 0.5;
		btnPanel.add(btnNext, c);
		c.gridx = 2;
		c.weightx = 0.2;
		btnPanel.add(btnShowSettings, c);

		dynSettingsPanel = new JPanel();
		dynSettingsSB = new JScrollPane(dynSettingsPanel);
		dynSettingsSB.setAlignmentY(TOP_ALIGNMENT);
		
		dynSettingsPanel.setBackground(Color.black);
		dynSettingsPanel.setLayout(new GridBagLayout());
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		dynSettingsSB.setVisible(false);
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		groupLayout.setHorizontalGroup(
			groupLayout.createSequentialGroup()
			.addGroup(groupLayout.createParallelGroup()
				.addComponent(plotPanel, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.DEFAULT_SIZE)
				.addComponent(btnPanel, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.DEFAULT_SIZE)
			).addComponent(dynSettingsSB)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup()
				.addComponent(dynSettingsSB)
			.addGroup(groupLayout.createSequentialGroup()
				.addComponent(plotPanel, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.DEFAULT_SIZE)
				.addComponent(btnPanel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
		));
		getContentPane().setLayout(groupLayout);

		btnPrev.addActionListener(this);
		btnNext.addActionListener(this);
		btnShowSettings.addActionListener(this);
		btnPrev.setActionCommand("prev");
		btnNext.setActionCommand("next");
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

	private void readPlotSettings() {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		for(Field f : plot.getClass().getFields()) {
			if(f.isAnnotationPresent(GraphSetting.class)) {
				GraphSetting gs = f.getAnnotation(GraphSetting.class);
				Component comp = new JButton(gs.label());
				dynSettingsPanel.add(comp);
				comp.addPropertyChangeListener("value", new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent arg0) {
						//TODO
					}
				});
			}
		}
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
		} else if (e.getActionCommand().equals("swap_settings_visibility")) {
			if(dynSettingsSB.isVisible()) {
				dynSettingsSB.setVisible(false);
				btnShowSettings.setText("Show Settings");
			} else {
				dynSettingsSB.setVisible(true);
				btnShowSettings.setText("Hide Settings");
			}
			revalidate();
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
		//plotLayout.addData(plot.getData(), new LineAttribute(LineAttribute.SOLID, Color.MAGENTA));
		plotLayout.addData(plot.getData());
		plotLayout.setTitles(new String[]{
				"Channel #" + plot.getInfos().channel + "(" + plot.getInfos().getChannelCode() +")",
				"Waves: " + plot.getWaveClass().getName(),
				plot.getInfos().file.getName()});
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
		plotLayout.endOperations();
		readPlotSettings();
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
