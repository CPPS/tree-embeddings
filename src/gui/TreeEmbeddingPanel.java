package gui;

import com.google.common.collect.Lists;
import generators.PointGenerator;
import generators.RandomPointGenerator;
import geometry.Edge;
import geometry.LBend;
import geometry.Line;
import geometry.MappingValidator2SAT;
import geometry.Point;
import geometry.Tree;
import math.Interval;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;

/**
 * Created by s148327 on 5-3-2017.
 */
public class TreeEmbeddingPanel extends JPanel {

    private List<Point> points;
    private Tree tree;
    private int[] mapping;

    private int minX, maxX, minY, maxY;

    private int horPadding = 30, verPadding = 30;

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

        if (points == null) return;

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
        Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
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

    public static TreeEmbeddingPanel showAsFrame() {
        JFrame frame = new JFrame("Tree Embedding Panel");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 800));

        TreeEmbeddingPanel panel = new TreeEmbeddingPanel();
        frame.add(panel);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        return panel;
    }

    public static void main(String[] args) throws InterruptedException {

        TreeEmbeddingPanel panel = showAsFrame();

        int n = 10;
        PointGenerator generator = new RandomPointGenerator(n, new Interval(0, n), new Interval(0, n));

        Tree tree = new Tree(10);
        tree.connect(0, 1);
        tree.connect(1, 3);
        tree.connect(1, 4);
        tree.connect(0, 2);
        tree.connect(2, 5);
        tree.connect(2, 6);
        tree.connect(6, 7);
        tree.connect(6, 8);
        tree.connect(7, 9);

        MouseAdapter mouseAdapter = new MouseAdapter() {

            boolean onlyShowValidMappings = true;

            @Override
            public void mouseClicked(MouseEvent e) {
                int[] mapping;
                List<Point> points;
                MappingValidator2SAT mappingValidator;
                boolean valid;
                do {
                    mappingValidator = new MappingValidator2SAT(n);
                    points = Lists.newArrayList(generator.generate());

                    mapping = new int[n];
                    for (int j = 0; j < n; j++) mapping[j] = j;

                    panel.setTreeEmbedding(points, tree, mapping);

                    long start = System.currentTimeMillis();
                    valid = mappingValidator.validate(tree, mapping, points);
                    System.out.println("ms: " + (System.currentTimeMillis() - start));
                } while (onlyShowValidMappings && !valid);
            }
        };
        panel.addMouseListener(mouseAdapter);
        mouseAdapter.mouseClicked(null);

        showTestPanel();
    }

    /**
     * Test panel, 2SAT (initially) says there is no valid placement,
     * while there is.
     */
    public static void showTestPanel() {
        int n = 7;
        Tree tree = new Tree(n);
        tree.connect(2, 1);
        tree.connect(2, 3);
        tree.connect(1, 0);
        tree.connect(1, 4);
        tree.connect(3, 5);
        tree.connect(3, 6);

        List<Point> points = Arrays.asList(
                new Point(0, 1),
                new Point(1, 3),
                new Point(2, 0),
                new Point(3, 4),
                new Point(4, 5),
                new Point(5, 6),
                new Point(6, 2));
        int[] mapping = new int[n];
        for (int i = 0; i < n; i++) mapping[i] = i;

        showAsFrame().setTreeEmbedding(points, tree, mapping);
    }
}
