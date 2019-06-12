package fsm;

import java.util.TimerTask;

public class FSMTimerTask extends TimerTask {
	
	FSM fsm;
	Event callback;
	
	public FSMTimerTask(FSM owningFSM, Event callback) {
		super();
		fsm = owningFSM;
		this.callback = callback;
	}
	@Override
	public void run() {
		fsm.queueEvent(callback);
	}
	

}
