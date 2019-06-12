package fsm;

/**
 * A wrapper class to represent the inverse of a guard
 * 
 * @author Arthur Depasse
 * @version 0.1
 * 
 * @see Guard
 * @see FSMServer
 *
 */
public class InverseGuard extends Guard {
	
	/**
	 * the negated guard
	 */
	Guard g;
	
	/**
	 * A constructor to build the inverse guard
	 * @param g the guard to negate
	 */
	public InverseGuard(Guard g) {
		this.g = g;
	}
	
	/**
	 * override of the eval function that calls the original eval and negate it.
	 */
	@Override
	public boolean eval(FSM fsm, Event e) {
		return !g.eval(fsm, e);
	}
}
