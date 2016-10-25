package de.joshavg.pdfwatermark.gui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;

import javax.swing.DefaultListModel;

class AddAction {

    private final DefaultListModel<String> model;

    AddAction(final DefaultListModel<String> model) {
        this.model = model;
    }

    void action(final ActionEvent e) {
        final File[] files = FileChooseAction.chooseMultiple("images", f -> f.getName().toLowerCase().endsWith("pdf"));

        if (files != null) {
            Arrays.asList(files).forEach(f -> this.model.addElement(f.getAbsolutePath()));
        }
    }

}
