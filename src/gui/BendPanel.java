package gui;

import geometry.LBend;
import geometry.Point;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by s148327 on 5-3-2017.
 */
public class BendPanel extends JPanel {

    private List<LBend> bends;
    private List<Point> points;

    private int minX, maxX, minY, maxY;

    private int horPadding = 30, verPadding = 30;

    public void setBends(List<LBend> bends) {
        this.bends = bends;
        this.points = new ArrayList<>();

        minX = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        minY = Integer.MAX_VALUE;
        maxY = Integer.MIN_VALUE;
        for (LBend bend : bends) {
            for (Point point : Arrays.asList(bend.getHorizontal().getFrom(), bend.getVertical().getFrom())) {
                minX = Math.min(minX, point.getX());
                maxX = Math.max(maxX, point.getX());
                minY = Math.min(minY, point.getY());
                maxY = Math.max(maxY, point.getY());
                points.add(point);
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bends == null) return;

        Graphics2D g2 = ((Graphics2D) g);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        /** Draw grid */
        g.setColor(Color.LIGHT_GRAY);
        for (int x = minX; x <= maxX; x++) {
            g.drawLine(pointToPixelX(x), pointToPixelY(minY), pointToPixelX(x), pointToPixelY(maxY));
        }
        for (int y = minY; y <= maxY; y++) {
            g.drawLine(pointToPixelX(minX), pointToPixelY(y), pointToPixelX(maxX), pointToPixelY(y));
        }

        drawLabel(g, "" + minX, minX, minY - 0.1, false);
        drawLabel(g, "" + maxX, maxX, minY - 0.1, false);
        drawLabel(g, "" + minY, minX - 0.1, minY, false);
        drawLabel(g, "" + maxY, minX - 0.1, maxY, false);

        /** Draw points */
        g.setColor(Color.DARK_GRAY);
        int pr = 6;
        for (Point point : points) {
            g.fillOval(pointToPixelX(point.getX()) - pr, pointToPixelY(point.getY()) - pr,
                    2 * pr + 1, 2 * pr + 1);
        }

        /** Draw tree and mapping */
        for (LBend bend : bends) {

            drawLBend(g, bend);
        }

    }

    private int pointToPixelX(double px) {
        int pointWidth = maxX - minX;

        return (int) (horPadding + (((px - minX) * (getWidth() - 2 * horPadding)) / pointWidth));
    }

    private int pointToPixelY(double py) {
        int pointHeight = maxY - minY;

        return (int) (getHeight() - verPadding - (((py - minY) * (getHeight() - 2 * verPadding)) / pointHeight));
    }

    private void drawLBend(Graphics g, LBend bend) {
        Graphics2D g2 = ((Graphics2D) g.create());
        Stroke stroke = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);//new BasicStroke(2.4f);
        g2.setStroke(stroke);

        g2.drawLine(pointToPixelX(bend.getHorizontal().getFrom().getX()),
                pointToPixelY(bend.getHorizontal().getFrom().getY()),
                pointToPixelX(bend.getHorizontal().getTo().getX()),
                pointToPixelY(bend.getHorizontal().getTo().getY()));
        g2.drawLine(pointToPixelX(bend.getVertical().getFrom().getX()),
                pointToPixelY(bend.getVertical().getFrom().getY()),
                pointToPixelX(bend.getVertical().getTo().getX()),
                pointToPixelY(bend.getVertical().getTo().getY()));

        g2.dispose();
    }

    private void drawLabel(Graphics g, String text, double px, double py, boolean fillbackground) {
        FontMetrics fm = g.getFontMetrics();

        int cx = pointToPixelX(px);
        int cy = pointToPixelY(py);
        int x = cx - fm.stringWidth(text) / 2;
        int y = cy - fm.getHeight() / 2 + fm.getAscent();

        if (fillbackground) {
            g.setColor(getBackground());
            int padding = 2;
            g.fillRect(x - padding, 2 * cy - y - padding, 2 * (cx - x + padding) + 1, 2 * (y - cy + padding) + 1);
        }

        g.setColor(Color.BLACK);
        g.drawString(text, x, y);
    }

    public static BendPanel showAsFrame() {
        JFrame frame = new JFrame("Tree Embedding LBend Panel");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(400, 400));

        BendPanel panel = new BendPanel();
        frame.add(panel);

        frame.pack();
//        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        return panel;
    }
}
