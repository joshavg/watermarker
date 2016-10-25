package de.joshavg.pdfwatermark.gui;

/*
 * Copyright (c) 2015, Veluria
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 * This is a drop-in replacement for Swing's file chooser. Instead of displaying
 * the Swing file chooser, this method makes use of the JavaFX file chooser.
 * JavaFX uses the OS's native file chooser, which is much better than the Java
 * one. Technically, this class is a waste of memory, but its use is convenient.
 * Furthermore, if JavaFX is not available, the default file chooser will be
 * displayed instead. Of course, this class will not compile if you don't have
 * an JDK 8 or higher that has JavaFX support. Since this class will have to
 * call the constructor of JFileChooser, it won't increase the performance of
 * the file chooser, though; if anything, it might further decrease it. Please
 * note that some methods have not been overwritten and may not have any impact
 * on the file chooser. Sometimes, the new JavaFX file chooser does not provide
 * a certain functionality, or the native file chooser provides them itself, but
 * there are no methods to access them. For example, one feature that is not
 * supported is the selection of files AND directories. If trying to set this
 * using setFileSelectionMode(), still only files will be selectable.
 *
 * @author Veluria
 * @version 1.6.2
 */
public class NativeJFileChooser extends JFileChooser {

    public static final boolean FX_AVAILABLE;
    private List<File> currentFiles;
    private FileChooser fileChooser;
    private File currentFile;
    private DirectoryChooser directoryChooser;

    static {
        boolean isFx;
        try {
            Class.forName("javafx.stage.FileChooser");
            isFx = true;
            final JFXPanel jfxPanel = new JFXPanel(); // initializes JavaFX
                                                      // environment
        } catch (final ClassNotFoundException e) {
            isFx = false;
        }

        FX_AVAILABLE = isFx;
    }

    public NativeJFileChooser() {
        initFxFileChooser(null);
    }

    public NativeJFileChooser(final String currentDirectoryPath) {
        super(currentDirectoryPath);
        initFxFileChooser(new File(currentDirectoryPath));
    }

    public NativeJFileChooser(final File currentDirectory) {
        super(currentDirectory);
        initFxFileChooser(currentDirectory);
    }

    public NativeJFileChooser(final FileSystemView fsv) {
        super(fsv);
        initFxFileChooser(fsv.getDefaultDirectory());
    }

    public NativeJFileChooser(final File currentDirectory, final FileSystemView fsv) {
        super(currentDirectory, fsv);
        initFxFileChooser(currentDirectory);
    }

    public NativeJFileChooser(final String currentDirectoryPath, final FileSystemView fsv) {
        super(currentDirectoryPath, fsv);
        initFxFileChooser(new File(currentDirectoryPath));
    }

    @Override
    public int showOpenDialog(final Component parent) throws HeadlessException {
        if (!FX_AVAILABLE) {
            return super.showOpenDialog(parent);
        }

        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // parent.setEnabled(false);
                if (isDirectorySelectionEnabled()) {
                    NativeJFileChooser.this.currentFile = NativeJFileChooser.this.directoryChooser.showDialog(null);
                } else {
                    if (isMultiSelectionEnabled()) {
                        NativeJFileChooser.this.currentFiles = NativeJFileChooser.this.fileChooser
                                .showOpenMultipleDialog(null);
                    } else {
                        NativeJFileChooser.this.currentFile = NativeJFileChooser.this.fileChooser.showOpenDialog(null);
                    }
                }
                latch.countDown();
                // parent.setEnabled(true);
            }

        });
        try {
            latch.await();
        } catch (final InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        if (isMultiSelectionEnabled()) {
            if (this.currentFiles != null) {
                return JFileChooser.APPROVE_OPTION;
            } else {
                return JFileChooser.CANCEL_OPTION;
            }
        } else {
            if (this.currentFile != null) {
                return JFileChooser.APPROVE_OPTION;
            } else {
                return JFileChooser.CANCEL_OPTION;
            }
        }

    }

    @Override
    public int showSaveDialog(final Component parent) throws HeadlessException {
        if (!FX_AVAILABLE) {
            return super.showSaveDialog(parent);
        }

        final CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // parent.setEnabled(false);
                if (isDirectorySelectionEnabled()) {
                    NativeJFileChooser.this.currentFile = NativeJFileChooser.this.directoryChooser.showDialog(null);
                } else {
                    NativeJFileChooser.this.currentFile = NativeJFileChooser.this.fileChooser.showSaveDialog(null);
                }
                latch.countDown();
                // parent.setEnabled(true);
            }

        });
        try {
            latch.await();
        } catch (final InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        if (this.currentFile != null) {
            return JFileChooser.APPROVE_OPTION;
        } else {
            return JFileChooser.CANCEL_OPTION;
        }
    }

    @Override
    public int showDialog(final Component parent, final String approveButtonText) throws HeadlessException {
        if (!FX_AVAILABLE) {
            return super.showDialog(parent, approveButtonText);
        }
        return showOpenDialog(parent);
    }

    @Override
    public File[] getSelectedFiles() {
        if (!FX_AVAILABLE) {
            return super.getSelectedFiles();
        }
        if (this.currentFiles == null) {
            return null;
        }
        return this.currentFiles.toArray(new File[this.currentFiles.size()]);
    }

    @Override
    public File getSelectedFile() {
        if (!FX_AVAILABLE) {
            return super.getSelectedFile();
        }
        return this.currentFile;
    }

    @Override
    public void setSelectedFiles(final File[] selectedFiles) {
        if (!FX_AVAILABLE) {
            super.setSelectedFiles(selectedFiles);
            return;
        }
        if (selectedFiles == null || selectedFiles.length == 0) {
            this.currentFiles = null;
        } else {
            setSelectedFile(selectedFiles[0]);
            this.currentFiles = new ArrayList<>(Arrays.asList(selectedFiles));
        }
    }

    @Override
    public void setSelectedFile(final File file) {
        if (!FX_AVAILABLE) {
            super.setSelectedFile(file);
            return;
        }
        this.currentFile = file;
        if (file != null) {
            if (file.isDirectory()) {
                this.fileChooser.setInitialDirectory(file.getAbsoluteFile());

                if (this.directoryChooser != null) {
                    this.directoryChooser.setInitialDirectory(file.getAbsoluteFile());
                }
            } else if (file.isFile()) {
                this.fileChooser.setInitialDirectory(file.getParentFile());
                this.fileChooser.setInitialFileName(file.getName());

                if (this.directoryChooser != null) {
                    this.directoryChooser.setInitialDirectory(file.getParentFile());
                }
            }

        }
    }

    @Override
    public void setFileSelectionMode(final int mode) {
        super.setFileSelectionMode(mode);
        if (!FX_AVAILABLE) {
            return;
        }
        if (mode == DIRECTORIES_ONLY) {
            if (this.directoryChooser == null) {
                this.directoryChooser = new DirectoryChooser();
            }
            setSelectedFile(this.currentFile); // Set file again, so directory
                                               // chooser will be affected by it
            setDialogTitle(getDialogTitle());
        }
    }

    @Override
    public void setDialogTitle(final String dialogTitle) {
        if (!FX_AVAILABLE) {
            super.setDialogTitle(dialogTitle);
            return;
        }
        this.fileChooser.setTitle(dialogTitle);
        if (this.directoryChooser != null) {
            this.directoryChooser.setTitle(dialogTitle);
        }
    }

    @Override
    public String getDialogTitle() {
        if (!FX_AVAILABLE) {
            return super.getDialogTitle();
        }
        return this.fileChooser.getTitle();
    }

    @Override
    public void changeToParentDirectory() {
        if (!FX_AVAILABLE) {
            super.changeToParentDirectory();
            return;
        }
        final File parentDir = this.fileChooser.getInitialDirectory().getParentFile();
        if (parentDir.isDirectory()) {
            this.fileChooser.setInitialDirectory(parentDir);
            if (this.directoryChooser != null) {
                this.directoryChooser.setInitialDirectory(parentDir);
            }
        }
    }

    @Override
    public void addChoosableFileFilter(final FileFilter filter) {
        super.addChoosableFileFilter(filter);
        if (!FX_AVAILABLE || filter == null) {
            return;
        }
        if (filter.getClass().equals(FileNameExtensionFilter.class)) {
            final FileNameExtensionFilter f = (FileNameExtensionFilter) filter;

            final List<String> ext = new ArrayList<>();
            for (final String extension : f.getExtensions()) {
                ext.add(extension.replaceAll("^\\*?\\.?(.*)$", "*.$1"));
            }
            this.fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(f.getDescription(), ext));
        }
    }

    @Override
    public void setAcceptAllFileFilterUsed(final boolean bool) {
        final boolean differs = isAcceptAllFileFilterUsed() ^ bool;
        super.setAcceptAllFileFilterUsed(bool);
        if (!FX_AVAILABLE) {
            return;
        }
        if (differs) {
            if (bool) {
                this.fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files", "*.*"));
            } else {
                for (final Iterator<FileChooser.ExtensionFilter> it = this.fileChooser.getExtensionFilters()
                        .iterator(); it.hasNext();) {
                    final FileChooser.ExtensionFilter filter = it.next();
                    if (filter.getExtensions().contains("*.*")) {
                        it.remove();
                    }
                }
            }
        }
    }

    private void initFxFileChooser(final File currentFile) {
        if (FX_AVAILABLE) {
            this.fileChooser = new FileChooser();
            this.currentFile = currentFile;
            setSelectedFile(currentFile);
        }
    }

}
