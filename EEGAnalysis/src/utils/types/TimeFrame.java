package utils.types;

import main.MainWindow;

public class TimeFrame {
	public int getFrom() {
		return MainWindow.getPrefs().getInt(MainWindow.PREF_TIME_FROM, 30);
	}
	public void setFrom(int from) {
		MainWindow.getPrefs().putInt(MainWindow.PREF_TIME_FROM, from);
	}
	public int getTo() {
		return getFrom() + MainWindow.getPrefs().getInt(MainWindow.PREF_TIME_DURATION, 60);
	}
	public void setTo(int to) {
		MainWindow.getPrefs().putInt(MainWindow.PREF_TIME_DURATION, to - getFrom());
	}
}