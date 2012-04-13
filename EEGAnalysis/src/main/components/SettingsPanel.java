package main.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.MainWindow;

public class SettingsPanel extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 640029470434166332L;

	/**
	 * @wbp.parser.constructor
	 */
	public SettingsPanel() {
		super();
		setBorder(UIManager.getBorder("ScrollPane.border"));
		JSlider sliderTimeFrom = new JSlider();
		sliderTimeFrom.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				MainWindow.getPrefs().putInt(MainWindow.PREF_TIME_FROM, ((JSlider)arg0.getSource()).getValue());
			}
		});
		sliderTimeFrom.setMajorTickSpacing(20);
		sliderTimeFrom.setMinorTickSpacing(1);
		sliderTimeFrom.setSnapToTicks(true);
		sliderTimeFrom.setPaintLabels(true);
		sliderTimeFrom.setPaintTicks(true);
		sliderTimeFrom.setValue(MainWindow.getPrefs().getInt(MainWindow.PREF_TIME_FROM, 30));

		JLabel lblTime = new JLabel("Analyze from time:");

		JLabel lblAnalyze = new JLabel("Taking");

		JSlider sliderTimeDuration = new JSlider();
		sliderTimeDuration.setMajorTickSpacing(5);
		sliderTimeDuration.setMinorTickSpacing(1);
		sliderTimeDuration.setMinimum(0);
		sliderTimeDuration.setMaximum(30);
		sliderTimeDuration.setSnapToTicks(true);
		sliderTimeDuration.setPaintLabels(true);
		sliderTimeDuration.setPaintTicks(true);
		sliderTimeDuration.setValue(MainWindow.getPrefs().getInt(MainWindow.PREF_TIME_DURATION, 30));
		sliderTimeDuration.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				int value = ((JSlider)arg0.getSource()).getValue();
				MainWindow.getPrefs().putInt(MainWindow.PREF_TIME_DURATION, value==0 ? 1 : value);
			}
		});

		JLabel lblSecondes = new JLabel("secondes");

		JSeparator separator = new JSeparator();
		separator.setBackground(Color.GRAY);
		separator.setForeground(Color.DARK_GRAY);

		JButton btnNewButton = new JButton("Upda' ze fuckin' graphs");
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MainWindow.getInstance().updateEveryGraphs();
			}
		});
		btnNewButton.setFont(new Font("Segoe Script", Font.BOLD, 14));

		ChanPositionsPanel imagePanel = new ChanPositionsPanel();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(imagePanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
						.addComponent(separator, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
						.addComponent(lblTime, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
						.addComponent(sliderTimeFrom, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addComponent(lblAnalyze)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(sliderTimeDuration, GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblSecondes))
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.UNRELATED))
						.addComponent(btnNewButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblTime, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(sliderTimeFrom, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblAnalyze)
						.addComponent(lblSecondes)
						.addComponent(sliderTimeDuration, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(13)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, 6, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(imagePanel, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton)
					.addContainerGap())
		);
		setLayout(groupLayout);
	}
}
