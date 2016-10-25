package de.joshavg.pdfwatermark.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.joshavg.pdfwatermark.Logger;

@SuppressWarnings("serial")
public class Gui extends JFrame {

    private String watermarkPath;
    private JList<String> jlist;

    public Gui() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            Logger.err(e);
        }

        setLayout(new BorderLayout());
        setTitle("Watermarker");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        final Gui gui = this;
        final Container pane = getContentPane();

        final JPanel northPanel = new JPanel() {
            {
                setLayout(new BorderLayout());
                add(new JLabel("Wasserzeichen") {
                    {
                        setHorizontalAlignment(CENTER);
                    }
                }, BorderLayout.NORTH);
                add(new JButton() {
                    {
                        setText("Auswählen");
                        addActionListener(new WatermarkAction(gui)::action);
                    }
                }, BorderLayout.SOUTH);
            }
        };
        pane.add(northPanel, BorderLayout.NORTH);

        final DefaultListModel<String> listmodel = new DefaultListModel<>();
        final JPanel centerPanel = new JPanel() {
            {
                setLayout(new BorderLayout());
                Gui.this.jlist = new JList<String>() {
                    {
                        setModel(listmodel);
                    }
                };
                add(new JScrollPane() {
                    {
                        setViewportView(Gui.this.jlist);
                    }
                }, BorderLayout.CENTER);

                final JPanel controlPanel = new JPanel() {
                    {
                        setLayout(new GridLayout(0, 1));
                        add(new JButton() {
                            {
                                setText("  +  ");
                                addActionListener(new AddAction(listmodel)::action);
                            }
                        }, BorderLayout.EAST);
                        add(new JButton() {
                            {
                                setText("  -  ");
                                addActionListener(new RemoveAction(listmodel, Gui.this.jlist)::action);
                            }
                        }, BorderLayout.EAST);
                        add(new JButton() {
                            {
                                setText("  ↪  ");
                                addActionListener(
                                        e -> new StartAction(listmodel).action(listmodel, Gui.this.watermarkPath));
                            }
                        }, BorderLayout.EAST);
                    }
                };
                add(controlPanel, BorderLayout.EAST);
            }
        };
        pane.add(centerPanel, BorderLayout.CENTER);
    }

    void setWatermarkPath(final String path) {
        this.watermarkPath = path;
    }

}
