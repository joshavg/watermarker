package de.joshavg.pdfwatermark.gui;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.DefaultListModel;
import javax.swing.JList;

class RemoveAction {

    private final DefaultListModel<String> model;
    private final JList<String> list;

    RemoveAction(final DefaultListModel<String> model, final JList<String> list) {
        this.model = model;
        this.list = list;
    }

    void action(final ActionEvent e) {
        final int[] ixs = this.list.getSelectedIndices();
        final int min = Arrays.stream(ixs).reduce(Math::min).orElse(-1);

        if (min == -1) {
            return;
        }

        Stream.iterate(0, i -> i).limit(ixs.length).forEach(i -> this.model.remove(min));
    }

}
