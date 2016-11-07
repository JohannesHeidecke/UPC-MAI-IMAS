/**
 *  IMAS base code for the practical work.
 *  Copyright (C) 2014 DEIM - URV
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cat.urv.imas.gui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class FileSaver {

    /**
     * Helps in automatizing save content into a file.
     *
     * @param evt event
     * @param fc file chooser element.
     * @param panel panel whose content is going to be stored.
     * @param content content to save.
     * @param filenamePrefix prefix for the generated files.
     */
    public static void saveContent(ActionEvent evt, JFileChooser fc, JPanel panel, String content, String filenamePrefix) {
        System.out.print("Saving content ...");
        String fileToSave = filenamePrefix + "_log" + System.currentTimeMillis() + ".log";
        File dummy = new File(fileToSave);
        fc.setSelectedFile(dummy);
        fc.setName(fileToSave);
        fc.setDialogTitle("Save content in a file");
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        fc.setCurrentDirectory(dummy);
        
        if (JFileChooser.APPROVE_OPTION == fc.showSaveDialog(panel)) {
            File outputFile = fc.getSelectedFile();
            try {
                UtilsGUI.writeFile(content, outputFile);
                System.out.println(" done!\n");
            } catch (IOException e) {
                System.err.println("IO ERROR: " + e.toString() + "\n");
            }
        } else {
            System.err.println("Aborted. Cancelled by the user.\n");
        }
        System.out.println();
    }

}
