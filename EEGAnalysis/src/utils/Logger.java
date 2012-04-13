package utils;

public class Logger {

	public static void log(Object txt) {
		StackTraceElement s = Thread.currentThread().getStackTrace()[2];
		System.out.println( "(" + s.getFileName() + ":" + s.getLineNumber() +")" + " " + txt.toString());
	}
}
