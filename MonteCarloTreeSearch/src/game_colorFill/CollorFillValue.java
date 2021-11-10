/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_colorFill;

import main.State;
import main.Value;

/**
 *
 * @author Hamed Khashehchi
 */
public class CollorFillValue extends Value {

    CollorFillValue(int num, double value) {
        super(num, value);
    }

    @Override
    public Value update(State state, Value simulationResult) {
        CollorFillState st = (CollorFillState) state;
        CollorFillValue simulation_result = (CollorFillValue) simulationResult;
        num = st.value.getNum()+1;
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
