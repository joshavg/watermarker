package de.joshavg.pdfwatermark.gui;

import java.io.File;
import java.util.function.Predicate;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

class FileChooseAction {

    private static JFileChooser createChooser(final String fileTypeDescription, final Predicate<File> fileacceptor) {
        final JFileChooser chooser = new NativeJFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return fileTypeDescription;
            }

            @Override
            public boolean accept(final File f) {
                return fileacceptor.test(f);
            }
        });
        return chooser;
    }

    static File chooseSingle(final String fileTypeDescription, final Predicate<File> fileacceptor) {
        final JFileChooser chooser = createChooser(fileTypeDescription, fileacceptor);
        chooser.setMultiSelectionEnabled(false);

        final int optionselected = chooser.showOpenDialog(null);
        if (optionselected == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }

        return null;
    }

    static File[] chooseMultiple(final String fileTypeDescription, final Predicate<File> fileacceptor) {
        final JFileChooser chooser = createChooser(fileTypeDescription, fileacceptor);
        chooser.setMultiSelectionEnabled(true);

        final int optionselected = chooser.showOpenDialog(null);
        if (optionselected == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFiles();
        }

        return null;
    }

}
