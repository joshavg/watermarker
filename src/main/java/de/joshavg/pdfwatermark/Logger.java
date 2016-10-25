package de.joshavg.pdfwatermark;

public class Logger {

	public static void log(final String msg) {
		System.out.println(msg);
	}

	public static void err(final Exception e) {
		System.err.println(e.getMessage());
		e.printStackTrace();
	}

}
