package nl.tue.cpps.lbend.generators;

import static org.junit.Assert.*;

import org.junit.Test;

import com.koloboke.collect.set.hash.HashLongSet;
import com.koloboke.collect.set.hash.HashLongSets;

import nl.tue.cpps.lbend.generator.BitsPerm;

public class BitsQuickPermTest {
    @Test
    public void test() {
        int n = 4;
        HashLongSet set = HashLongSets.newMutableSet(1 << n);
        BitsPerm it = new BitsPerm(n);

        int i = 0;
        while (it.hasNext()) {
            long[] longArray = it.next().toLongArray();
            long v = longArray.length == 0 ? 0 : longArray[0];
            if (!set.add(v)) {
                assertTrue("" + v, false);
            }
            i++;
        }

        assertEquals(i, (1 << n));
    }

}
