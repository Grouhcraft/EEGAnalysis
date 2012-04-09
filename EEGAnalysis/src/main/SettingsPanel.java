package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

		JCheckBox chckbxUseSquareWindowing = new JCheckBox("Use square windowing instead of Hann");
		chckbxUseSquareWindowing.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				MainWindow.getPrefs().putBoolean(MainWindow.PREF_WELCH_USE_SQ_WIN, ((JCheckBox)e.getSource()).isSelected());
			}
		});
		chckbxUseSquareWindowing.setSelected(MainWindow.getPrefs().getBoolean(MainWindow.PREF_WELCH_USE_SQ_WIN, false));

		JLabel lblWelchPeriodogram = new JLabel("Welch Periodogram");
		lblWelchPeriodogram.setFont(new Font("Tahoma", Font.BOLD, 11));

		JLabel lblSegLength = new JLabel("Segments length");

		JSpinner spinnerWelchSegLen = new JSpinner();
		spinnerWelchSegLen.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				MainWindow.getPrefs().putInt(MainWindow.PREF_WELCH_SEG_LENGTH, (Integer)((JSpinner)e.getSource()).getValue());
			}
		});
		spinnerWelchSegLen.setModel(new SpinnerNumberModel(800, 200, 6000, 200));
		spinnerWelchSegLen.setValue(MainWindow.getPrefs().getInt(MainWindow.PREF_WELCH_SEG_LENGTH, 800));

		JSeparator separator_1 = new JSeparator();
		separator_1.setForeground(Color.DARK_GRAY);
		separator_1.setBackground(Color.GRAY);

		JLabel lblWelchAndSpd = new JLabel("Welch and SPD");
		lblWelchAndSpd.setFont(new Font("Segoe UI", Font.BOLD, 12));

		JCheckBox chckbxShowYAxisInDB = new JCheckBox("Show Y axis in dB (logarithmic)");
		chckbxShowYAxisInDB.setSelected(MainWindow.getPrefs().getBoolean(MainWindow.PREF_PERIO_USE_DBSCALE, true));
		chckbxShowYAxisInDB.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				MainWindow.getPrefs().putBoolean(MainWindow.PREF_PERIO_USE_DBSCALE, ((JCheckBox)e.getSource()).isSelected());
			}
		});

		JButton btnNewButton = new JButton("Upda' ze fuckin' graphs");
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MainWindow.getInstance().updateEveryGraphs();
			}
		});
		btnNewButton.setFont(new Font("Segoe Script", Font.BOLD, 14));

		LocationsPanel imagePanel = new LocationsPanel();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(imagePanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
						.addComponent(chckbxUseSquareWindowing, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
						.addComponent(lblWelchPeriodogram, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
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
							.addComponent(lblSegLength)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(spinnerWelchSegLen, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE))
						.addComponent(separator_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
						.addComponent(lblWelchAndSpd, Alignment.LEADING)
						.addComponent(chckbxShowYAxisInDB, Alignment.LEADING)
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
					.addComponent(lblWelchPeriodogram, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxUseSquareWindowing)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSegLength)
						.addComponent(spinnerWelchSegLen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 6, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblWelchAndSpd)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(chckbxShowYAxisInDB)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(imagePanel, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton)
					.addContainerGap())
		);
		setLayout(groupLayout);
	}
}
