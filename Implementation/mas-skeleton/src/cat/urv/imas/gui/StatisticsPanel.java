package cat.urv.imas.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.Dimension;

/**
 * <b>Company:</b> Universitat Rovira i Virgili (<a
 * href="http://www.urv.cat">URV</a>)
 */
public class StatisticsPanel
        extends JPanel {

    private static JTextArea jTextArea;
    private JButton doSave;
    private final JFileChooser fc = new JFileChooser();

    private final int inset = 60;

    public StatisticsPanel() {
        super();
        this.initComponents();
        this.setVisible(true);
    }

    public void showMessage(String msg) {
        jTextArea.append(msg);
    }

    private void initComponents() {
        this.setLayout(new AbsoluteLayout());
        this.setBorder(UtilsGUI.createBorder("Statistics"));
        this.setMinimumSize(new Dimension(640, 480));
        this.setPreferredSize(new Dimension(640, 480));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        jTextArea = UtilsGUI.createTextArea();
        JScrollPane jScrollPane = new JScrollPane(jTextArea);

        this.add(jScrollPane,
                new AbsoluteConstraints(15,
                        20,
                        screenSize.width - (3 * inset),
                        screenSize.height - (4 * inset)));

        /**
         * button doSave
         */
        doSave = UtilsGUI.createButton("Save log", UtilsGUI.buttonSaveIconPath);
        doSave.setToolTipText("Save the information into a statistics file");
        doSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                FileSaver.saveContent(evt, fc, StatisticsPanel.this, jTextArea.getText(), "statistics");
            }
        });
        this.add(doSave,
                new AbsoluteConstraints(15,
                        screenSize.height - (inset * 2) - 80,
                        120,
                        20));
    }

}
