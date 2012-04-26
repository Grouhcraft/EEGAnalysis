package plotframes.components;


import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import main.MainWindow;
import plotframes.PlotFrame;
import plotframes.data.EEGSourceFile;
import plotframes.data.WaveClass;
import plotframes.plots.IPlot;
import plotframes.plots.Plot;
import plotframes.plots.annotations.GraphType;
import plotframes.plots.fromXml.XmlPlot;
import plotframes.plots.implementations.Xml2DPlot;
import utils.Logger;



public class GraphMenu extends JMenuBar implements ActionListener {

	private static final long serialVersionUID = -5364900148201046220L;
	private static File currentDir = null;
	private PlotFrame parentWindow;
	private JMenu linkMenu;
	private JMenu xmlPlotsMenu;
	private static ArrayList<GraphMenu> _instances = new ArrayList<GraphMenu>();
	private static final String VIEWGRAPH_CMDSTR = "viewgraph";

	public PlotFrame getParentWindow() {
		return parentWindow;
	}

	@Override
	public void finalize() {
		_instances.remove(this);
	}

	public GraphMenu(PlotFrame parentWindow) {
		GraphMenu._instances.add(this);
		this.parentWindow = parentWindow;
		JMenu file = new JMenu("File");
		JMenuItem loadDataFile = new JMenuItem("Load data file");
		loadDataFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser;
				if(getParentWindow().getDataSource().isFile())
					currentDir = ((EEGSourceFile)getParentWindow().getDataSource()).getFile().getParentFile();
				if (currentDir == null) {
					fileChooser = new JFileChooser();
				} else {
					fileChooser = new JFileChooser(currentDir);
				}
				int returnVal = fileChooser.showOpenDialog(getParent());
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	File selectedFile = fileChooser.getSelectedFile();
					getParentWindow().setDataSource(new EEGSourceFile(selectedFile));
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
		for(Class<? extends IPlot> plotClass : IPlot.graphTypes) {
			if(plotClass.isAnnotationPresent(GraphType.class)) {
				GraphType gt = plotClass.getAnnotation(GraphType.class);
				JMenuItem item = new JMenuItem(gt.name());
				item.setActionCommand(VIEWGRAPH_CMDSTR + ":" + plotClass.getName());
				item.addActionListener(this);
				graphType.add(item);
			}
		}
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

		linkMenu = new JMenu("Link with plot..");
		linkMenu.setVisible(false);
		add(linkMenu);

		xmlPlotsMenu = new JMenu("Choose EEP file plot");
		xmlPlotsMenu.setVisible(false);
		xmlPlotsMenu.setFont(xmlPlotsMenu.getFont().deriveFont(Font.BOLD));
		add(xmlPlotsMenu);
	}

	public void updateXmlPLotsList() {
		if(MainWindow.getInstance() == null) return;
		xmlPlotsMenu.removeAll();

		//TODO a terminer

		if(parentWindow.getPlot() instanceof Xml2DPlot) {
			for(final XmlPlot p : MainWindow.getInstance().xmls) {
				JMenuItem pitem = new JMenuItem(p.getMetas().getName());
				pitem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						Logger.log("plop !!!!!!!!!!!!!!!");
						((Xml2DPlot)parentWindow.getPlot()).setXmlPlot(p);
						((Xml2DPlot)parentWindow.getPlot()).update();
						parentWindow.updateGraph();
						parentWindow.updateLayout();
					}
				});
				xmlPlotsMenu.add(pitem);
			}
			xmlPlotsMenu.setVisible(true);
		} else {
			xmlPlotsMenu.setVisible(false);
		}

		JMenuItem openANewXml = new JMenuItem("* Open a new EEP file *");
		xmlPlotsMenu.add(openANewXml);
	}

	public static void updatePlotsLinkMenu() {
		for(final GraphMenu menu : _instances) {
			if(MainWindow.getInstance() == null) return;
			menu.linkMenu.removeAll();
			for(String plotName : MainWindow.getInstance().getPlotsNames()) {
				if(!menu.getParentWindow().getTitle().equals(plotName)) {
					JMenuItem plotEntry = new JMenuItem(plotName);
					plotEntry.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							menu.getParentWindow().linkPlot(((JMenuItem)arg0.getSource()).getText());
						}
					});
					menu.linkMenu.add(plotEntry);
				}
			}
			if(menu.linkMenu.getSubElements().length > 0) {
				menu.linkMenu.setVisible(true);
				JMenuItem clearLinkedGraphs = new JMenuItem("* Clear linked datas *");
				clearLinkedGraphs.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						menu.getParentWindow().unlinkAll();
					}
				});
				menu.linkMenu.add(clearLinkedGraphs);
			} else {
				menu.linkMenu.setVisible(false);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String[] cmd = arg0.getActionCommand().split(":");
		if(cmd.length == 2 && cmd[0].equals(VIEWGRAPH_CMDSTR)) {
			for(Class<? extends IPlot> gt : IPlot.graphTypes) {
				if(gt.getName().equals(cmd[1])) {
					try {
						getParentWindow().setGraphType((Class<? extends Plot>) Class.forName(gt.getName()));
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}
}
