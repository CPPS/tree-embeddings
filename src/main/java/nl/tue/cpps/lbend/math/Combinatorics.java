package nl.tue.cpps.lbend.math;

import java.util.ArrayList;
import java.util.Collection;

public class Combinatorics {
    public static Collection<int[]> partitions(int n, int k){
        Collection<int[]> collection = new ArrayList<>();

        if (n == 0) {
            collection.add(new int[0]);
            return collection;
        }

        for (int[] p : partitions(n-1, k)) {
            int[] partition = new int[p.length + 1];

            System.arraycopy(p, 0, partition, 1, p.length);
            partition[0] = 1;

            collection.add(partition);

            if (p.length > 0) {
                if (p.length < 2 || p[1] > p[0]) {
                    if (p[0] + 1 < k) {
                        p[0]++;
                        collection.add(p);
                    }
                }
            }
        }

        return collection;
    }
}
