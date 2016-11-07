package cat.urv.imas.gui;

import javax.swing.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.Toolkit;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.RenderingHints;
import cat.urv.imas.map.*;

/**
 * Visualization of the map. There are several elements to depict, as buildings,
 * streets, the agents, and the rest of items.<br>
 * This class *should be* modified and improved in order to show as good as
 * possible all the changes in the simulation. We provide several high-level
 * methods which can be rewritten as needed.<br>
 *
 * <b>Company:</b> Universitat Rovira i Virgili (<a
 * href="http://www.urv.cat">URV</a>)
 */
public class MapVisualizer extends JPanel implements CellVisualizer {

    /**
     * Margin area surrouding the city map.
     */
    private static final int INSET = 50;
    /**
     * Cell gap.
     */
    private static final int GAP = 5;
    /**
     * City map.
     */
    private final Cell[][] map;
    /**
     * Cell width.
     */
    private final int dx;
    /**
     * Cell height.
     */
    private final int dy;
    /**
     * Dimensions for the cell's border.
     */
    private final Rectangle2D.Double cellBorder;
    /**
     * The way agents are shown into the map.
     */
    private final Ellipse2D.Double agentFigure;
    /**
     * Graphics when painting the city map.
     *
     * @see cat.urv.imas.gui.CellVisualizer
     */
    private Graphics2D temporaryGraphics;

    /**
     * Initializes values for a correct map painting.
     *
     * @param map
     */
    public MapVisualizer(Cell[][] map) {
        this.map = map;
        int nrows = map.length;
        int ncols = map[0].length;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Point start = new Point(INSET, INSET);
        Point end = new Point(screenSize.width - INSET * 2, screenSize.height - INSET * 2);

        dx = (end.x - start.x) / ncols;
        dy = ((end.y - start.y) / nrows) - 4;

        cellBorder = new Rectangle2D.Double(GAP + 10, GAP + 10, dx, dy);
        agentFigure = new Ellipse2D.Double(GAP + 10 + (dx / 4), GAP + 10 + (dy / 4), (dx / 2), (dy / 2));

    }

    /**
     * Show the whole city map.
     *
     * @param graphics
     */
    @Override
    public void paintComponent(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;

        // 0. initializing graphic element.
        clear(graphics);

        // 1. set graphics for painting
        this.updateGraphics(g2d);

        // 2. iterate cells and paint them.
        for (Cell[] row : map) {

            for (Cell cell : row) {
                // 2.1. draw the border.
                g2d.draw(cellBorder);
                // 2.2. draw the cell
                cell.draw(this);
                // 2.3. move on
                g2d.translate(dx, 0);
            }

            // 2.4. move on
            g2d.translate(-(dx * row.length), dy);
        }

        // 5. unsetting graphics
        this.updateGraphics(null);
        // 6. painting reinforcement
        this.repaint();
    }

    protected void clear(Graphics g) {
        super.paintComponent(g);
    }

    protected Ellipse2D.Double getCircle() {
        return (agentFigure);
    }

    protected void drawAgent(Color fillingColor, String message, Color textColor) {
        temporaryGraphics.setPaint(fillingColor);
        temporaryGraphics.translate((dx / 6), (dy / 6));
        temporaryGraphics.fill(agentFigure);
        temporaryGraphics.translate(-(dx / 6), -(dy / 6));
        drawString(message, textColor, dx - 80, dy - 10);
    }

    /**
     * Just draws a cell with the given filling in color, and the given border
     * color.
     *
     * @param fillingColor cell's color
     * @param borderColor color for the cell's border
     */
    protected void drawCell(Color fillingColor, Color borderColor) {
        temporaryGraphics.setPaint(fillingColor);
        temporaryGraphics.fill(cellBorder);
        temporaryGraphics.setPaint(borderColor);
        temporaryGraphics.draw(cellBorder);
    }

    /**
     * Draws the message into the cell, in the given position within the cell.
     *
     * @param message text to show.
     * @param textColor color for the text
     * @param x some value related to dx
     * @param y some value realted to dy
     */
    protected void drawString(String message, Color textColor, int x, int y) {
        if (!message.equals("")) {
            temporaryGraphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            Font font = new Font("Serif", Font.PLAIN, 11);
            temporaryGraphics.setFont(font);
            temporaryGraphics.setPaint(textColor);
            temporaryGraphics.drawString(message, x, y);
        }
    }

    /* ****************** CellVisualizer implementation ********************* */
    @Override
    public void updateGraphics(Graphics2D graphics) {
        this.temporaryGraphics = graphics;
    }

    @Override
    public void drawScout(StreetCell cell) {
        drawEmptyStreet(cell);
        drawAgent(Color.WHITE, cell.getMapMessage(), Color.BLACK);
    }

    @Override
    public void drawHarvester(StreetCell cell) {
        drawEmptyStreet(cell);
        drawAgent(Color.RED, cell.getMapMessage(), Color.BLACK);
    }

    @Override
    public void drawEmptyStreet(StreetCell cell) {
        drawCell(Color.LIGHT_GRAY, Color.DARK_GRAY);
    }

    @Override
    public void drawBuilding(BuildingCell cell) {
        drawCell(Color.CYAN.darker(), Color.DARK_GRAY);
        drawString(cell.getMapMessage(), Color.WHITE, dx - 40, dy);
    }

    @Override
    public void drawRecyclingCenter(RecyclingCenterCell cell) {
        drawCell(Color.RED, Color.GREEN);
        drawString(cell.getMapMessage(), Color.BLACK, dx - 80, dy);
    }

}
