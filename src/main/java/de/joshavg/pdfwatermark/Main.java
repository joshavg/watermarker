package de.joshavg.pdfwatermark;

import javax.swing.SwingUtilities;

import de.joshavg.pdfwatermark.gui.Gui;

public class Main {
	public static void main(final String[] args) {
		if (args.length == 0 || !args[0].equals("--headless")) {
			SwingUtilities.invokeLater(() -> new Gui().setVisible(true));
		}
	}
}
