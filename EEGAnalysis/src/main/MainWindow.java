package main;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.UIManager;

import java.awt.FlowLayout;
import java.io.File;
import java.util.ArrayList;

import plot.PlotFrame;
import plot.WaveClass;

public class MainWindow {

	private JFrame frmEegAnalysis;
	private ArrayList<PlotFrame> plots = new ArrayList<PlotFrame>();
	private static MainWindow _instance = null;

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
		frmEegAnalysis.setTitle("EEG Analysis");
		frmEegAnalysis.setJMenuBar(new MainMenu(this));
		frmEegAnalysis.setBounds(100, 100, 600, 600);
		frmEegAnalysis.setExtendedState(frmEegAnalysis.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frmEegAnalysis.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmEegAnalysis.getContentPane().setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));
	
		BGDesktopPane desktopPane = new BGDesktopPane();
		frmEegAnalysis.getContentPane().add(desktopPane);
		
		createNewPlot(new File(System.getenv("EEGDATA") + "\\" + R.get("datafile")));
	}

	public void createNewPlot(File selectedFile) {
		createNewPlot(selectedFile, 1, WaveClass.ALPHA);
	}
	
	public void createNewPlot(File file, int channel, WaveClass wc) {
		PlotFrame plot = new PlotFrame("EEG data #" + (plots.size()+1) , channel, file);
		plot.setWaveClass(wc);
		plots.add(plot);
		frmEegAnalysis.add(plot);
	}
	
	public void createNewPlot(PlotFrame p) {
		PlotFrame plot = new PlotFrame(p.getTitle() + "clone #" + (plots.size()+1), p);
		plots.add(plot);
		frmEegAnalysis.add(plot);
	}
	
	public void removePlot(PlotFrame p) {
		plots.remove(p);
	}
	
	public static MainWindow getInstance() {
		return _instance;
	}
}
