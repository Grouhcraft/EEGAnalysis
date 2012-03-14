package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class GraphMenu extends JMenuBar {

	private static final long serialVersionUID = -5364900148201046220L;
	private static File currentDir = null;
	private GraphWindow parentWindow;


	public GraphWindow getParentWindow() {
		return parentWindow;
	}

	public GraphMenu(GraphWindow parentWindow) {
		this.parentWindow = parentWindow;
		JMenu file = new JMenu("File");
		JMenuItem loadDataFile = new JMenuItem("Load data file");
		loadDataFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser;
				currentDir = getParentWindow().getDataFile().getParentFile();
				if (currentDir == null) {
					fileChooser = new JFileChooser();
				} else {
					fileChooser = new JFileChooser(currentDir);
				}
				int returnVal = fileChooser.showOpenDialog(getParent());
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	File selectedFile = fileChooser.getSelectedFile();
					getParentWindow().setDataFile(selectedFile);
					System.out.println(selectedFile.getPath());
					getParentWindow().updateGraphs();
			    }

			}
		});
		file.add(loadDataFile);
		add(file);
	}
}