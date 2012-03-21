package plot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Field;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import main.MainWindow;



public class GraphMenu extends JMenuBar implements ActionListener {

	private static final long serialVersionUID = -5364900148201046220L;
	private static File currentDir = null;
	private PlotFrame parentWindow;


	public PlotFrame getParentWindow() {
		return parentWindow;
	}

	public GraphMenu(PlotFrame parentWindow) {
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
					getParentWindow().updateGraph();
			    }

			}
		});
		file.add(loadDataFile);
		add(file);
		
		JMenu waveClasses = new JMenu("Wave classes");
		for(Field c : WaveClass.class.getDeclaredFields()) {
			if(WaveClass.class.getName().equals( c.getType().getName())) {
				JMenuItem item = new JMenuItem(c.getName());
				item.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							getParentWindow().setWaveClass(WaveClass.get(((JMenuItem)e.getSource()).getText()));
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					
					}
				});
				waveClasses.add(item);
			}
		}
		add(waveClasses);
		
		JMenu graphType = new JMenu("Visualize..");
		JMenuItem PSDItem= new JMenuItem("Energy Spectral Density Periodogram");
		JMenuItem waveItem = new JMenuItem("Waveform");
		PSDItem.setActionCommand("view_psd");
		waveItem.setActionCommand("view_waveform");
		PSDItem.addActionListener(this);
		waveItem.addActionListener(this);
		graphType.add(PSDItem);
		graphType.add(waveItem);
		add(graphType);
		
		JMenu clone = new JMenu("Clone");
		JMenuItem cloneItem = new JMenuItem("Clone this plot");
		cloneItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MainWindow.getInstance().createNewPlot(getParentWindow());
			}
		});
		clone.add(cloneItem);
		add(clone);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String c = arg0.getActionCommand();
		if(c.equals("view_psd")) {
			getParentWindow().setGraphType(GraphType.EnergySpectralDensity);
		} else if (c.equals("view_waveform")) {
			getParentWindow().setGraphType(GraphType.WaveForm);
		}
	}
}
