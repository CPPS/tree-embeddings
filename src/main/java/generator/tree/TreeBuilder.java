package generator.tree;

import geometry.Tree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TreeBuilder {
    public static Tree fromSequence(int[] sequence) {
        int n = sequence.length + 2;
        Tree T = new Tree(n);
        int[] count = new int[n];

        for (int i : sequence) {
            count[i]++;
        }

        int index = 0;

        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < n; i++) list.add(i);

        if (list.size() < 2) return T;
        while (list.size() > 2) {
            Iterator<Integer> iterator = list.iterator();
            int element;

            do { element = iterator.next(); }
            while (!(count[element] == 0));
            iterator.remove();

            T.connect(element, sequence[index]);
            count[sequence[index]]--;
            index++;
        }

        T.connect(list.get(0), list.get(1));

        return T;
    }
}
