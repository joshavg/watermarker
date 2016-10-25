package de.joshavg.pdfwatermark.gui;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JButton;

class WatermarkAction {

    private final Gui gui;

    WatermarkAction(final Gui gui) {
        this.gui = gui;
    }

    void action(final ActionEvent e) {
        final File file = FileChooseAction.chooseSingle("images", f -> {
            final String lcname = f.getName().toLowerCase();
            return lcname.endsWith("jpg") || lcname.endsWith("png");
        });

        if (file != null) {
            this.gui.setWatermarkPath(file.getAbsolutePath());
            ((JButton) e.getSource()).setText(file.getAbsolutePath());
        }
    }

}
