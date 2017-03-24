package gui;

import com.google.common.collect.Lists;
import generator.IntQuickPerm;
import generator.point.PermutedPointGenerator;
import generator.point.PointSetGenerator;
import geometry.MappingValidator2SAT;
import geometry.Point;
import geometry.Tree;
import lombok.NonNull;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DebugMain {
    private static final class MouseHandler extends MouseAdapter {
        private final @NonNull PointSetGenerator generator;
        private final @NonNull Tree tree;
        private final @NonNull TreeEmbeddingPanel panel;

        private Iterator<List<Point>> pointIterator;
        private Collection<Point> currentPointSet;
        private final int[] mapping;

        MouseHandler(int n, PointSetGenerator generator, Tree tree, TreeEmbeddingPanel panel) {
            this.generator = generator;
            this.tree = tree;
            this.panel = panel;
            pointIterator = generator.generate();
            mapping = new int[n];
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.NOBUTTON) {
                return;
            }

            doUpdate();
        }

        private void doUpdate() {
            // Loop
            if (!pointIterator.hasNext()) {
                pointIterator = generator.generate();
            }
            currentPointSet = pointIterator.next();

            for (int i = 0; i < mapping.length; i++) {
                mapping[i] = i;
            }

            IntQuickPerm mapper = new IntQuickPerm(mapping);
            List<Point> points;
            MappingValidator2SAT mappingValidator;
            boolean valid;
            do {
                mappingValidator = new MappingValidator2SAT(mapping.length);
                points = Lists.newArrayList(currentPointSet);

                mapper.next();
                System.out.println(Arrays.toString(mapping));
                panel.setTreeEmbedding(points, tree, mapping);

                long start = System.currentTimeMillis();
                valid = mappingValidator.validate(tree, mapping, points);
                System.out.println("ms: " + (System.currentTimeMillis() - start) + " " + valid);
            } while (!valid && mapper.hasNext());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TreeEmbeddingPanel panel = TreeEmbeddingPanel.create();
        panel.getFrame().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        int n = 10;
        PointSetGenerator generator = new PermutedPointGenerator(n);

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

        MouseHandler mouseAdapter = new MouseHandler(n, generator, tree, panel);
        panel.addMouseListener(mouseAdapter);
        mouseAdapter.doUpdate();
    }
}
