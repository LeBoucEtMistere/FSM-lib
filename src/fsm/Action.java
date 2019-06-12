package fsm;

/**
 * An abstract class to represent an action fired by a transition.
 * 
 * @author Arthur Depasse
 * @version 0.1
 * 
 * @see FSMServer
 *
 */
public abstract class Action {
	
	/**
	 * An abstract function that execute the content of the action, given the fsm and event that triggered it
	 * @param fsm the owning fsm
	 * @param e the event that triggered the action
	 */
	abstract public void execute(FSM fsm, Event e);

}
