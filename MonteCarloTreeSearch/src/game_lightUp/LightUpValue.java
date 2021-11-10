/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game_lightUp;

import main.State;
import main.Value;

/**
 *
 * @author Hamed Khashehchi
 */
public class LightUpValue extends Value {

    public LightUpValue(int num, double value) {
        super(num, value);
    }

    @Override
    public Value update(State state, Value simulationResult) {
        LightUpState st = (LightUpState) state;
        LightUpValue simulation_result = (LightUpValue) simulationResult;
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

}
