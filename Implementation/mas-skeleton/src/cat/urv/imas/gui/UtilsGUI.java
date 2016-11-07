/**
 * IMAS base code for the practical work. 
 * Copyright (C) 2014 DEIM - URV
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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Class containing several tools used in different classes of the GUI. I/O
 * functions, constants, other static functions reused in different elements.
 */
public class UtilsGUI {

    // icon directory. this path is set up for running from within Netbeans.
    public static String IMAGE_DIR
            = System.getProperty("user.dir")
            + System.getProperty("file.separator") + "build"
            + System.getProperty("file.separator") + "classes"
            + System.getProperty("file.separator") + "cat"
            + System.getProperty("file.separator") + "urv"
            + System.getProperty("file.separator") + "imas"
            + System.getProperty("file.separator") + "gui"
            + System.getProperty("file.separator") + "image"
            + System.getProperty("file.separator");

    public static String gameIconPath = UtilsGUI.IMAGE_DIR + "meneame.gif";
    public static String logsIconPath = UtilsGUI.IMAGE_DIR + "ico_mas.gif";
    public static String statisticsIconPath = UtilsGUI.IMAGE_DIR + "ico_mas.gif";
    public static String buttonIconPath = UtilsGUI.IMAGE_DIR + "ico_bajar.gif";

    public static String buttonSaveIconPath = UtilsGUI.IMAGE_DIR + "save.gif";

    // constants de les TextAreas
    public static Color backgroundColor = new Color(250, 250, 250); //light gray
    public static Color textColor = Color.BLACK;

    // constants globals
    public static Font messageTextFont = new java.awt.Font("Helvetica", 0, 11);
    public static Font textFont = new Font("Arial", 0, 11);
    public static Font smallTextFont = new Font("Arial", 0, 10);

    public static Color buttonGgColor = new Color(240, 210, 160); //dark brown
    public static Font buttonFont = new Font("Arial", 0, 11);
    public static Color buttonTextColor = Color.BLACK;

    static public Color frameBgColor = new Color(240, 210, 160); //dark brown
    static public Color textFgColor = new Color(0, 0, 100); //dark blue

    static public Font titleFont = new Font("Arial Narrow", Font.PLAIN, 15);

    static public Font italicTextFont = new Font("Arial", Font.ITALIC, 12);

    static public Font dataTextFont = new Font("Arial", Font.PLAIN, 12);
    static public Font titlesFont = new Font("Dialog", Font.PLAIN, 12);

    /**
     * Do not use it
     */
    private UtilsGUI() {
    }

    /**
     * Create a button with a pre-defined look-and-feel
     *
     * @param text String Text to put inside the button
     * @param icon String Image embedded into the button
     * @return JButton initialised
     */
    public static JButton createButton(String text, String icon) {
        ImageIcon iconRefresh = new ImageIcon(icon);
        JButton jButton = new JButton(iconRefresh);
        jButton.setBorderPainted(false);
        jButton.setBackground(buttonGgColor);
        jButton.setForeground(buttonTextColor);
        jButton.setFont(buttonFont);
        jButton.setText(text);
        jButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jButton.setEnabled(true);
        return jButton;
    }

    /**
     * Default look and feel titled border used in all interfaces.
     *
     * @param message String Title to put in the top of the border
     * @return Border tunned accurately
     */
    public static Border createBorder(String message) {
        return new TitledBorder(
                new EtchedBorder(), message, 1, 2, messageTextFont);
    }

    /**
     * Creates a read-only text area.
     * @return text area.
     */
    public static JTextArea createTextArea() {
        JTextArea result = new JTextArea();
        result.setBorder(new BevelBorder(1));
        result.setBackground(backgroundColor);
        result.setForeground(textColor);
        result.setName("Information area");
        result.setEditable(false);
        result.setSelectedTextColor(Color.BLUE);
        result.setFont(UtilsGUI.messageTextFont);
        return result;
    }

    /**
     * We write the string specified into a file.
     *
     * @param content String to write
     * @param file Pathname of the file
     * @throws java.io.IOException
     */
    public static void writeFile(String content, File file) throws IOException {
        PrintStream outFile = new PrintStream(new FileOutputStream(file));
        for (int i = 0; i < content.length(); i++) {
            outFile.print(content.charAt(i));
        }
        outFile.flush();
        outFile.close();
    }

}
