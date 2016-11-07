/**
 * IMAS base code for the practical work. Copyright (C) 2014 DEIM - URV
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package cat.urv.imas.gui;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;

/**
 * Class showing the area of logs. Initially it contains a textarea and a button
 * to save the logs in a file.
 *
 */
public class LogPanel extends JPanel {

    /**
     * Text area where to show logs.
     */
    private static JTextArea jTextArea;
    /**
     * Button to save the logs content into a text file.
     */
    private JButton doSave;
    /**
     * File selector when storing logs.
     */
    private final JFileChooser fc = new JFileChooser();
    /**
     * Margin.
     */
    private static final int INSET = 50;

    /**
     * Default constructor.
     */
    public LogPanel() {
        super();
        this.initComponents();
        this.setVisible(true);
    }

    /**
     * Adds the message into the logs area.
     *
     * @param msg Message to append.
     */
    public void log(String msg) {
        jTextArea.append(msg);
    }

    /**
     * Initializes the log panel.
     */
    private void initComponents() {
        this.setLayout(new AbsoluteLayout());
        this.setBorder(UtilsGUI.createBorder("Logs"));
        this.setMinimumSize(new Dimension(640, 480));
        this.setPreferredSize(new Dimension(640, 480));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        jTextArea = UtilsGUI.createTextArea();
        JScrollPane jScrollPane = new JScrollPane(jTextArea);

        this.add(jScrollPane,
                new AbsoluteConstraints(15,
                        20,
                        screenSize.width - (3 * INSET),
                        screenSize.height - (4 * INSET)));

        /**
         * button doSave
         */
        doSave = UtilsGUI.createButton("Save log ...", UtilsGUI.buttonSaveIconPath);
        doSave.setToolTipText("Save the information into a log file");
        doSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                FileSaver.saveContent(evt, fc, LogPanel.this, jTextArea.getText(), "logs");
            }
        });
        this.add(doSave,
                new AbsoluteConstraints(15,
                        screenSize.height - (INSET * 2) - 80,
                        120,
                        20));
    }

}
