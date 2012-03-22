package main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.UIManager;
import java.io.File;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import plot.PlotFrame;
import plot.WaveClass;

public class MainWindow {

	private JFrame frmEegAnalysis;
	private BGDesktopPane desktopPane;
	private ArrayList<PlotFrame> plots = new ArrayList<PlotFrame>();
	private static MainWindow _instance = null;
    private static Preferences prefs;
    
	/**
	 * Préférences keys
	 */
	public final static String PREF_WELCH_SEG_LENGTH = "welch.segmentsLength";
	public final static String PREF_WELCH_USE_SQ_WIN = "welch.useSquareWindowing";
	public final static String PREF_TIME_DURATION= "time.duration";
	public final static String PREF_TIME_FROM = "time.from";
	public final static String PREF_PERIO_USE_DBSCALE = "periodograms.useDBScale";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
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
		frmEegAnalysis.setTitle("EEG Analysis");
		frmEegAnalysis.setJMenuBar(new MainMenu(this));
		frmEegAnalysis.setBounds(100, 100, 600, 600);
		frmEegAnalysis.setExtendedState(frmEegAnalysis.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frmEegAnalysis.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmEegAnalysis.getContentPane().setLayout(new BorderLayout());
		desktopPane = new BGDesktopPane();
		frmEegAnalysis.getContentPane().add(desktopPane);
		
		SettingsPanel settingPanel = new SettingsPanel();
		frmEegAnalysis.getContentPane().add(settingPanel, BorderLayout.EAST);
		
		createNewPlot(new File(System.getenv("EEGDATA") + "\\" + R.get("datafile")));
	}

	public void createNewPlot(File selectedFile) {
		createNewPlot(selectedFile, 1, WaveClass.ALPHA);
	}
	
	public void createNewPlot(File file, int channel, WaveClass wc) {
		PlotFrame plot = new PlotFrame("EEG data #" + (plots.size()+1) , channel, file);
		plot.setWaveClass(wc);
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
	}
	
	public void removePlot(PlotFrame p) {
		plots.remove(p);
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
}
