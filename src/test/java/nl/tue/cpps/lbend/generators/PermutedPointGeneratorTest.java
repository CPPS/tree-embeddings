package nl.tue.cpps.lbend.generators;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;

import nl.tue.cpps.lbend.generator.point.PermutedPointGenerator;
import nl.tue.cpps.lbend.geometry.FixedPoint;
import nl.tue.cpps.lbend.geometry.MutablePoint;
import nl.tue.cpps.lbend.geometry.Point;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class PermutedPointGeneratorTest {

    int size = 7;

    @Rule
    public TestRule watcher = new TestWatcher() {
        double timeStart;

        protected void starting(Description description) {
            timeStart = System.currentTimeMillis();
            System.out
                    .println("===========================================================================");
            System.out.println("Test: " + description.getMethodName());
            System.out
                    .println("===========================================================================");
        }

        protected void finished(Description description) {
            double timeEnd = System.currentTimeMillis();
            double seconds = (timeEnd - timeStart) / 1000.0;
            System.out
                    .println("\n===========================================================================");
            System.out
                    .println("Test completed - ran in: " + new DecimalFormat("0.000").format(seconds) + " sec");
            System.out
                    .println("===========================================================================\n");

        }
    };

    @Test
    public void referenceSpeed() {
        Iterator<List<Point>> it = new PermutedPointGenerator(size).generate();
        while (it.hasNext()) {
            List<Point> points = it.next();
        }
    }

    @Test
    public void testMirrorRemoval() {

        // Amount of point sets
        int n = 1;
        for (int i = 1; i <= size; i++) {
            n *= i;
        }

        Set<Set<FixedPoint>> seen = new HashSet<>();
        Iterator<List<Point>> it = new PermutedPointGenerator(size).generate();
        List<List<Point>> listOfPoints = new ArrayList<>();
        List<List<Point>> regular = new ArrayList<>();
        List<List<Point>> skippingLastMax = new ArrayList<>();
        int countMirror = 0;
        int countReg = 0;
        while (it.hasNext()) {
            // Make copy
            List<Point> points = it.next();
            List<Point> clone = new ArrayList<>();
            for (Point p : points) {
                clone.add(new MutablePoint(p.getX(), p.getY()));
            }
            if (hasMirror(points, listOfPoints)) {
                // System.out.println(points.toString() + " mirror");
                countMirror++;

            } else {
                countReg++;
                //System.out.println(points.toString());

                listOfPoints.add(clone);

            }

            if (shouldSkip(points)) {
                regular.add(clone);
            } else {
                regular.add(clone);
                skippingLastMax.add(clone);
            }

            Set<FixedPoint> set = points.stream()
                    .map(new Function<Point, FixedPoint>() {
                        @Override
                        public FixedPoint apply(Point t) {
                            return FixedPoint.of(t);
                        }
                    })
                    .collect(Collectors.<FixedPoint>toSet());

            n--;

            //assertTrue(seen.add(set));
        }
        
        System.out.println(regular.size()+" "+skippingLastMax.size());
        
        Iterator<List<Point>> it2 = regular.iterator();
        while (it2.hasNext()) {
            List<Point> ps1 = it2.next();
            if (skippingLastMax.contains(ps1) || hasMirror(ps1, skippingLastMax)) {
                it2.remove();
            }
        }

        //remove mirrors from regular
        it2 = regular.iterator();
        while (it2.hasNext()) {
            List<Point> sp2 = it2.next();
            if (hasMirror(sp2, regular)) {
                it2.remove();
            }
        }

        for (List<Point> ps2 : regular) {
            System.out.println("Missing " + ps2.toString());
        }
        System.out.println(countMirror + " " + countReg + " " + (countReg + countMirror));

    }

    //check if the mirror is already in a list
    private boolean hasMirror(List<Point> points, List<List<Point>> listOfPoints) {
        //find mirror vertical
        int max = points.size() - 1;
        List<Point> verticalMirror = new ArrayList<Point>();
        for (Point p : points) {
            verticalMirror.add(new MutablePoint(max - p.getX(), p.getY()));
        }

        for (List<Point> points2 : listOfPoints) {
            if (points.containsAll(verticalMirror)) {
                return true;
            }

        }
        
        //find mirror horizontal
        List<Point> horizontalMirror = new ArrayList<Point>();
        for (Point p : points) {
            horizontalMirror.add(new MutablePoint(p.getX(), max - p.getY()));
        }

        for (List<Point> points2 : listOfPoints) {
            if (points2.containsAll(horizontalMirror)) {
                return true;
            }
        }
        //find mirror both
        ArrayList<Point> both = new ArrayList<>();
        for (Point p : horizontalMirror) {
            both.add(new MutablePoint(max - p.getX(), p.getY()));
        }
        for (List<Point> points2 : listOfPoints) {
            if (points2.containsAll(both)) {
                return true;
            }
        }

        return false;
    }

    private boolean shouldSkip(List<Point> points) {
        if (points.get(size - 1).getY() > size / 2 ) {
            return true;
        }
        return false;
    }

}
