package generators;

import java.util.Arrays;
import java.util.Iterator;

public class SequenceGenerator implements Iterable<int[]> {
    protected int[] code;
    int n;

    public SequenceGenerator(int[] code) {
        this.code = code;

        this.n = 2;
        for (int i : code) {
            this.n += i;
        }
    }

    protected int[] map(int[] source) {
        int[] sequence = new int[n - 2];

        int index = 0;
        for (int c = 0; c < code.length; c++) {
            for (int i = 0; i < code[c]; i++)
                sequence[index + i] = source[c];

            index += code[c];
        }

        return sequence;
    }

    @Override
    public Iterator<int[]> iterator() {
        return new Iterator<int[]>() {

            int[] source;

            {
                this.source = new int[code.length];
                for (int i = 0; i < source.length; i++) {
                    source[i] = n - source.length - i - 1;
                }
            }

            @Override
            public boolean hasNext() {
                return !(source == null);
            }

            @Override
            public int[] next() {
                int len = source.length;
                int[] next = Arrays.copyOf(source, len);

                if (len == 0) {
                    source = null;
                } else {
                    int idx = len - 1;
                    source[idx]++;

                    while(source[idx] < n && ((idx > 0) && source[idx] >= source[idx - 1])) {
                        idx--;
                        source[idx]++;
                        source[idx + 1] = n - 2 - source.length - idx;
                    }

                    if (source[0] == n) {
                        source = null;
                    }
                }

                return map(next);
            }
        };
    }
}
