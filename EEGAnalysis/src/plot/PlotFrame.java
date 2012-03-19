package plot;

import gov.noaa.pmel.sgt.LineAttribute;
import gov.noaa.pmel.sgt.swing.JPlotLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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


public class PlotFrame extends JInternalFrame implements ActionListener {

	private static final long serialVersionUID = 2796714104577643465L;
	private JPlotLayout plotLayout;
	private Plot plot;
	public Plot getPlot() {
		return plot;
	}
	
	public PlotFrame(String plotId, PlotFrame p) {
		initialize(plotId, p.getPlot().getChanel(), p.getDataFile());
	}
	
	public PlotFrame(String plotID, int channel, File file) {
		initialize(plotID, channel, file);
	}
	
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
		
		this.plot = new Plot(channel, file);
		setWaveClass(WaveClass.ALPHA);
		
		setVisible(true);
	}	

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("prev")) {
			if(plot.dataSettings.channel > 1) {
				plot.dataSettings.channel--;
			}
		} else if (e.getActionCommand().equals("next")) {
			if(plot.dataSettings.channel < plot.dataSettings.channelsCount) {
				plot.dataSettings.channel++;
			}
		}
		updateGraph();
	}

	public void updateGraph() {
		plotLayout.setBatch(true);
		plot.update();
		plotLayout.clear();
		plotLayout.addData(plot.getData(), new LineAttribute(LineAttribute.SOLID, Color.MAGENTA));
		plotLayout.setTitles(
				"Channel #" + plot.dataSettings.channel, 
				"Waves: " + plot.waveClass.getName(), 
				plot.getDataFile().getName());
		plotLayout.setBatch(false);
	}

	public void setWaveClass(WaveClass waveClass) {
		plot.setWaveClass(waveClass);
		updateGraph();
	}

	public File getDataFile() {
		return plot.getDataFile();
	}

	public void setDataFile(File selectedFile) {
		plot.setDataFile(selectedFile);
	}
}
