package xo;

import main.*;

public class XO_Game extends Game {

	@Override
	public void init() {
		myState = new XO_State();
	}

}
