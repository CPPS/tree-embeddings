package nl.tue.cpps.lbend;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import nl.tue.cpps.lbend.geometry.Edge;
import nl.tue.cpps.lbend.geometry.LBend;
import nl.tue.cpps.lbend.geometry.Point;
import nl.tue.cpps.lbend.geometry.Tree;
import nl.tue.cpps.lbend.gui.BendDrawer;

public class Dumper {
    private final File targetDir = new File("target/dump");
    private final int w = 512;
    private final int h = w;
    private final int[] EMPTY_ROW = new int[w];
    private final BufferedImage bi = new BufferedImage(
            w, h, BufferedImage.TYPE_INT_ARGB);
    private final Graphics2D graphics = bi.createGraphics();
    private final BendDrawer drawer = new BendDrawer();

    public Dumper() {
        boolean suc = targetDir.mkdirs();
        assert suc;
        drawer.setWidth(w);
        drawer.setHeight(h);
        drawer.setBackground(new Color(0, 0, 0, 0));
    }

    public void draw(
            int idx, Tree tree, List<Point> points,
            int[] mapping, boolean[] solution) {
        List<LBend> bends = new ArrayList<>(points.size());

        Iterator<Edge> it = tree.edgeIterator();
        int i = 0;
        while (it.hasNext()) {
            Edge edge = it.next();

            // true: |-
            // false: -|
            boolean complement = !solution[i];
            bends.add(LBend.getBend(
                    points.get(mapping[edge.getFrom()]),
                    points.get(mapping[edge.getTo()]), complement));

            i++;
        }

        drawer.setBends(bends);
        drawer.draw(graphics);

        try {
            ImageIO.write(bi, "png", new File(targetDir, idx + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        bi.setRGB(0, 0, w, h, EMPTY_ROW, 0, 0);
    }
}
