/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_npuzzle;

import main.State;
import main.Value;

/**
 *
 * @author Hamed Khashehchi
 */
public class NPuzzleValue extends Value {

    NPuzzleValue(int num, double value) {
        super(num, value);

    }

    @Override
    public Value update(State state, Value simulationResult) {
        NPuzzleState st = (NPuzzleState) state;
        NPuzzleValue simulation_result = (NPuzzleValue) simulationResult;
        num = st.value.getNum() + 1;
        bestValue = Math.max(bestValue, simulation_result.value);
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
    public int compareTo_UCT(Value vv, int total_number) {
        double u1 = value + Math.sqrt(2 * Math.log(total_number) / num);
        double u2 = vv.value + Math.sqrt(2 * Math.log(total_number) / vv.num);
        if (u1 < u2) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return "{num: " + num + ", value: " + value + ", maxValue: " + bestValue + "}";
    }

}
