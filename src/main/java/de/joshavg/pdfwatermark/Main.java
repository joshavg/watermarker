package de.joshavg.pdfwatermark;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.itextpdf.text.DocumentException;

import de.joshavg.pdfwatermark.gui.Gui;
import de.joshavg.pdfwatermark.transform.Watermarker;

public class Main {
    private static final String OUTPUT_PARAM = "--output";
    private static final String INPUT_PARAM = "--input";
    private static final String WATERMARK_PARAM = "--watermark";

    public static void main(final String[] args) {
        if (args.length == 0 || !args[0].equals("--headless")) {
            SwingUtilities.invokeLater(() -> new Gui().setVisible(true));
        } else {
            headless(args);
        }
    }

    private static void headless(final String[] args) {
        final List<String> listargs = Arrays.asList(args);
        if (!(listargs.contains(INPUT_PARAM) && listargs.contains(OUTPUT_PARAM) && listargs.contains(WATERMARK_PARAM)
                && listargs.size() == 7)) {
            Logger.err(
                    new IllegalArgumentException(String.format("not all necessary input arguments provided: %s, %s, %s",
                            OUTPUT_PARAM, INPUT_PARAM, WATERMARK_PARAM)));
            return;
        }

        final Map<String, String> params = new HashMap<>();
        String lastkey = "";
        for (final String s : listargs) {
            if (s.startsWith("--")) {
                lastkey = s;
            } else {
                params.put(lastkey, s);
            }
        }

        try {
            new Watermarker(params.get(WATERMARK_PARAM), params.get(INPUT_PARAM), params.get(OUTPUT_PARAM)).transform();
        } catch (final IOException | DocumentException e) {
            Logger.err(e);
        }
    }
}
