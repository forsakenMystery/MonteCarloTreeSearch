package main;

public abstract class Value {

    public static int modelNumber = 1;
    public int num;
    public double value;
    protected double bestValue;

    public int getNum() {
        return num;
    }

    public Value(int num, double value) {
        this.num = num;
        this.value = value;
        bestValue = value;
    }

    public Value() {

    }

    public int compareTo(Value vv) {
        if (value < vv.value) {
            return -1;
        } else if (value > vv.value) {
            return 1;
        } else {
            return 0;
        }
    }

    public int compareToBest(Value vv) {
        if (bestValue < vv.bestValue) {
            return -1;
        } else {
            return 1;
        }
    }

    public int compareTo_UCT(Value vv, int total_number) {
        double u1 = value + Math.sqrt(2 * Math.log(total_number) / num);
        double u2 = vv.value + Math.sqrt(2 * Math.log(total_number) / vv.num);
        if (u1 < u2) {
            return -1;
        } else {
            return 1;
        }
    }

    public abstract Value update(State state, Value simulationResult);

    @Override
    public String toString() {
        return "{num: " + num + ", value: " + value + ", maxValue: " + bestValue + "}";
    }
}
