package gui;

import com.google.common.collect.Lists;

import geometry.Edge;
import geometry.LBend;
import geometry.MappingValidator2SAT;
import geometry.Point;
import geometry.Tree;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.List;

@RequiredArgsConstructor
public class TreeEmbeddingPanel extends JPanel {
    @Getter
    private final @NonNull JFrame frame;

    private @Nullable List<Point> points;
    private @Nullable Tree tree;
    private @Nullable int[] mapping;

    private int minX, maxX, minY, maxY;

    private int horPadding = 30, verPadding = 30;

    public static TreeEmbeddingPanel create() {
        JFrame frame = new JFrame("Tree Embedding Panel");
        frame.setPreferredSize(new Dimension(800, 800));

        TreeEmbeddingPanel panel = new TreeEmbeddingPanel(frame);
        frame.add(panel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        return panel;
    }

    public void setTreeEmbedding(List<Point> points) {
        setTreeEmbedding(points, null, null);
    }

    public void setTreeEmbedding(List<Point> points, Tree tree, int[] mapping) {
        this.points = points;

        if (tree == null ^ mapping == null) {
            throw new IllegalArgumentException("tree and mapping must either be both non-null or both null");
        }

        this.tree = tree;
        this.mapping = mapping;

        minX = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        minY = Integer.MAX_VALUE;
        maxY = Integer.MIN_VALUE;
        for (Point point : points) {
            minX = Math.min(minX, point.getX());
            maxX = Math.max(maxX, point.getX());
            minY = Math.min(minY, point.getY());
            maxY = Math.max(maxY, point.getY());
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (points == null)
            return;

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
        int pr = 7;
        for (Point point : points) {
            g.fillOval(pointToPixelX(point.getX()) - pr, pointToPixelY(point.getY()) - pr,
                    2 * pr + 1, 2 * pr + 1);
        }

        /** Draw tree and mapping */
        if (tree != null) {

            MappingValidator2SAT mappingValidator = new MappingValidator2SAT(points.size());
            boolean[] solution = mappingValidator.validateWithSolution(tree, mapping, points);
            boolean valid = solution != null;

            List<Edge> edges = Lists.newArrayList(tree.edgeIterator());
            for (int i = 0; i < edges.size(); i++) {
                Edge edge = edges.get(i);
                int from = mapping[edge.getFrom()];
                int to = mapping[edge.getTo()];

                int x1 = points.get(from).getX();
                int y1 = points.get(from).getY();
                int x2 = points.get(to).getX();
                int y2 = points.get(to).getY();
                g.setColor(valid ? Color.DARK_GRAY : Color.RED.darker());
                drawDottedLine(g,
                        pointToPixelX(x1),
                        pointToPixelY(y1),
                        pointToPixelX(x2),
                        pointToPixelY(y2));

                drawLabel(g, Integer.toString(i), (x1 + x2) / 2d, (y1 + y2) / 2d, true);
            }

            if (valid) {
                g.setColor(Color.DARK_GRAY);
                for (int i = 0; i < edges.size(); i++) {
                    Edge edge = edges.get(i);

                    // true: |-
                    // false: -|
                    boolean complement = !solution[i];
                    drawLBend(g, LBend.getBend(
                            points.get(mapping[edge.getFrom()]),
                            points.get(mapping[edge.getTo()]), complement));
                }
            }
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

    private void drawDottedLine(Graphics g, int fromX, int fromY, int toX, int toY) {
        Graphics2D g2 = ((Graphics2D) g.create());
        Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0);
        g2.setStroke(dashed);
        g2.drawLine(fromX, fromY, toX, toY);

        g2.dispose();
    }

    private void drawLBend(Graphics g, LBend bend) {
        Graphics2D g2 = ((Graphics2D) g.create());
        Stroke stroke = new BasicStroke(2.4f);
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
            int padding = 1;
            g.fillRect(x - padding, 2 * cy - y - padding, 2 * (cx - x + padding) + 1, 2 * (y - cy + padding) + 1);
        }

        g.setColor(Color.BLACK);
        g.drawString(text, x, y);
    }
}
