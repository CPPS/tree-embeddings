public class Interval {
    protected int min, max;

    public Interval(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException();
        }

        this.min = min;
        this.max = max;
    }

    public int getLength() {
        return max - min;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
