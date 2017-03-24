package gui;

import geometry.LBend;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by s148327 on 5-3-2017.
 */
public class BendPanel extends JPanel {
    private final BendDrawer drawer = new BendDrawer();

    public void setBends(List<LBend> bends) {
        drawer.setBends(bends);

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = ((Graphics2D) g);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawer.setBackground(getBackground());
        drawer.setWidth(getWidth());
        drawer.setHeight(getHeight());
        drawer.draw(g);

    }

    public static BendPanel showAsFrame() {
        JFrame frame = new JFrame("Tree Embedding LBend Panel");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(400, 400));

        BendPanel panel = new BendPanel();
        frame.add(panel);

        frame.pack();
        // frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        return panel;
    }
}
