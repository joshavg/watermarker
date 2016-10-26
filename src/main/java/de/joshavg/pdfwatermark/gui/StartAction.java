package de.joshavg.pdfwatermark.gui;

import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import com.itextpdf.text.DocumentException;

import de.joshavg.pdfwatermark.Logger;
import de.joshavg.pdfwatermark.transform.Watermarker;

class StartAction {

    void action(final DefaultListModel<String> listmodel, final String watermarkPath) {
        for (final Object sourcePdf : listmodel.toArray()) {
            try {
                final String sourceName = sourcePdf.toString();
                final String targetName = sourceName.replaceAll("\\.pdf$", "-watermarked.pdf");

                final Watermarker marker = new Watermarker(watermarkPath, sourceName, targetName);
                marker.transform();
            } catch (final IOException | DocumentException e) {
                Logger.err(e);
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

}
