package main;


import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Point;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.UIManager;

import main.components.BGDesktopPane;
import main.components.MainMenu;
import main.components.SettingsPanel;
import plotframes.PlotFrame;
import plotframes.components.GraphMenu;
import plotframes.plots.fromXml.PlotXmlReader;
import plotframes.plots.fromXml.XmlPlotObject;
import utils.MessageBox;



public class MainWindow {

	private JFrame frmEegAnalysis;
	private BGDesktopPane desktopPane;
	private ArrayList<PlotFrame> plots = new ArrayList<>();

	private static MainWindow _instance = null;
    private static Preferences prefs;

    public static Object zoom = null;
    public static ArrayList<XmlPlotObject> xmls = new ArrayList<>();

    private final static String NL = System.getProperty("line.separator");
	/**
	 * Préférences keys
	 */
	public final static String PREF_TIME_DURATION= "time.duration";
	public final static String PREF_TIME_FROM = "time.from";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					_instance = new MainWindow();
					_instance.frmEegAnalysis.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	private MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmEegAnalysis = new JFrame();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		prefs = Preferences.userNodeForPackage(getClass());
		setDefaultPreferences();
		frmEegAnalysis.setTitle("EEG Analysis");
		frmEegAnalysis.setJMenuBar(new MainMenu(this));
		frmEegAnalysis.setBounds(100, 100, 600, 600);
		frmEegAnalysis.setExtendedState(frmEegAnalysis.getExtendedState() | Frame.MAXIMIZED_BOTH);
		frmEegAnalysis.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmEegAnalysis.getContentPane().setLayout(new BorderLayout());
		desktopPane = new BGDesktopPane();
		frmEegAnalysis.getContentPane().add(desktopPane);

		SettingsPanel settingPanel = new SettingsPanel();
		frmEegAnalysis.getContentPane().add(settingPanel, BorderLayout.EAST);

		String defaultFile = System.getenv("EEGAnalysis_default_file");
		if(defaultFile != null)
			createNewPlot(new File(defaultFile));
		loadXmlPlots();
	}

	private void loadXmlPlots() {
		File dir = new File(R.get("xmlplots"));
		for(File f : dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if(name.endsWith(".eep")) return true;
				return false;
			}
		})) {
			String badFiles = "";
			try {
				xmls.add(new PlotXmlReader(f).getXmlPlot());
			} catch (Exception e) {
				badFiles += " - " + f.getName() + NL;
			} finally {
				if(!badFiles.isEmpty()) {
					new MessageBox("Error: was unable to open the following "
							+"plot files during initialization:" + NL + badFiles);
				}
			}
		}
	}

	private void setDefaultPreferences() {
		getPrefs().putInt(PREF_TIME_DURATION, 30);
		getPrefs().putInt(PREF_TIME_FROM, 10);
	}

	public void createNewPlot(File selectedFile) {
		createNewPlot(selectedFile, 1);
	}

	public void createNewPlot(File file, int channel) {
		PlotFrame plot = new PlotFrame("EEG data #" + (plots.size()+1) , channel, file);
		placePlot(plot);
	}

	public void createNewPlot(PlotFrame p) {
		PlotFrame plot = new PlotFrame(p.getTitle() + "clone #" + (plots.size()+1), p);
		placePlot(plot);
	}

	private void placePlot(PlotFrame plot) {
		desktopPane.add(plot);
		if(!plots.isEmpty()) {
			Point p = plots.get(plots.size()-1).getLocation();
			int padding = 50;
			plot.setLocation(p.x + padding, p.y + padding);
			desktopPane.getDesktopManager().activateFrame(plot);
		}
		plots.add(plot);
		GraphMenu.updatePlotsLinkMenu();
	}

	public void removePlot(PlotFrame p) {
		plots.remove(p);
		GraphMenu.updatePlotsLinkMenu();
	}

	public static MainWindow getInstance() {
		return _instance;
	}

	public void updateEveryGraphs() {
		for(PlotFrame p : plots) {
			p.updateGraph();
		}
	}

	public static Preferences getPrefs() {
		return prefs;
	}

	public String[] getPlotsNames() {
		String[] plotsNames = new String[plots.size()];
		for(int i=0; i<plots.size(); i++) {
			plotsNames[i] = plots.get(i).getTitle();
		}
		return plotsNames;
	}

	public PlotFrame getPlotByTitle(String plotTitle) {
		for(PlotFrame p : plots)
			if(p.getTitle().equals(plotTitle)) return p;
		return null;
	}
}
