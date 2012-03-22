package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MainMenu extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3507152594933758384L;
	private static File currentDir = null;
	private MainWindow parentWindow;

	public MainWindow getParentWindow() {
		return parentWindow;
	}

	public MainMenu(MainWindow parentWindow) {
		this.parentWindow = parentWindow;
		JMenu file = new JMenu("Plots");
		JMenuItem newEEGPlot = new JMenuItem("Open new EEG plot");
		newEEGPlot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser;
				currentDir = new File(System.getenv("EEGDATA"));
				if (currentDir == null) {
					fileChooser = new JFileChooser();
				} else {
					fileChooser = new JFileChooser(currentDir);
				}
				int returnVal = fileChooser.showOpenDialog(getParent());
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	File selectedFile = fileChooser.getSelectedFile();
					getParentWindow().createNewPlot(selectedFile);
			    }

			}
		});
		file.add(newEEGPlot);
		add(file);
	}

}
