package fsm;

/**
 * An abstract class that represent a guard for a transition
 * 
 * @author Arthur Depasse
 * @version 0.1
 * 
 * @see FSMServer
 * @see Transition
 *
 */
public abstract class Guard {
	
	/**
	 * An abstract function that evaluate a boolean to decide whether the transition should be fired or not
	 * @param fsm the fsm that owns the transition
	 * @param e the event that was fired
	 * @return true if the transition should be fired, false if not
	 */
	abstract public boolean eval(FSM fsm, Event e);
	
}

