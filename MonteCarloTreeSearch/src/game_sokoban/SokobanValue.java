/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_sokoban;

import main.State;
import main.Value;

/**
 *
 * @author Hamed Khashehchi
 */
public class SokobanValue extends Value {

    boolean mark[];

    public SokobanValue(int num, double value) {
        super(num, value);
    }

    public SokobanValue(int i, double d, boolean[] m) {
        this.num = i;
        this.value = d;
        bestValue = value;
        this.mark = m;
    }

    public int compareTo(SokobanValue vv) {
        if (value < vv.value) {
            return -1;
        } else {
            return 1;
        }
    }

    public int compareToBest(SokobanValue vv) {
        if (bestValue < vv.bestValue) {
            return -1;
        } else {
            return 1;
        }
    }

    public int compareTo_UCT(SokobanValue vv, int total_number) {
        double u1 = value + Math.sqrt(2 * Math.log(total_number) / num);
        double u2 = vv.value + Math.sqrt(2 * Math.log(total_number) / vv.num);
        if (u1 < u2) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public SokobanValue update(State state, Value simulationResult) {
        SokobanState st = (SokobanState) state;
        SokobanValue simulation_result = (SokobanValue) simulationResult;
        ++num;
        bestValue = Math.max(bestValue,
                simulation_result.value);
        switch (modelNumber) {
            case 1:
            case 2:
            case 3:
                value = (value * (num - 1) + simulation_result.value) / num;
                break;
            case 4:
            case 5:
            case 6:
                value = bestValue;
                break;
            default:
                break;
        }
        return this;
    }

    @Override
    public String toString() {
        return "{num: " + num + ", value: " + value + ", maxValue: " + bestValue + "}";
    }

}
