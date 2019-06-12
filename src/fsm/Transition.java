package fsm;

import java.util.ArrayList;

/**
 * A class used to represent transitions in and FSM. A transition is defined by
 * a name, source and target states, an associated event that fire the
 * transition and a list of actions to be executed when the transition is fired
 * 
 * @author Arthur Depasse
 * @version 0.1
 * 
 * @see FSMServer
 *
 */
public class Transition {

	/**
	 * The name of the transition, used for logging
	 */
	private String name;

	/**
	 * The source state of the transition.
	 */
	private State source;

	/**
	 * the target state of the transition.
	 */
	private State target;

	/**
	 * the event that fire the transition.
	 */
	private String event;

	/**
	 * A Guard object that contains a predicate that decides if the transition
	 * should be fired if its event is raised.
	 * 
	 * @see Guard
	 */
	private Guard guard;

	/**
	 * The list of actions to do if the transition is fired.
	 */
	private ArrayList<Action> actions;

	/**
	 * The constructor of the Transition.
	 * 
	 * @param name the name of the transition.
	 */
	public Transition(String name) {
		this.name = name;
		actions = new ArrayList<Action>();
	}

	/**
	 * A setter to set the target state of the transition.
	 * 
	 * @param s the target state.
	 * 
	 * @see Transition#target
	 */
	public void setTarget(State s) {
		this.target = s;
		s.addTransitionTo(this);
	}

	/**
	 * A setter to set the source state of the transition
	 * 
	 * @param s the source state
	 * 
	 * @see Transition#source
	 */
	public void setSource(State s) {
		this.source = s;
		s.addTransitionFrom(this);
	}

	/**
	 * A function to register the vent associated with the transition.
	 * 
	 * @param event the event to set
	 * 
	 * @see Transition#event
	 */
	public void registerEvent(String event) {
		this.event = event;
	}

	/**
	 * A function to register actions to launch when the transition is fired. Action
	 * are fired in the order they are added.
	 * 
	 * @param action an action to add to the list of actions.
	 * 
	 * @see Transition#actions
	 */
	public void registerAction(Action action) {
		this.actions.add(action);
	}

	/**
	 * A function to register a Guard object associated with this action.
	 * 
	 * @param g the guard to register
	 * 
	 * @see Transition#guard
	 */
	public void registerGuard(Guard g) {
		this.guard = g;
	}

	/**
	 * A function called by {@link State#processEvent(Event)}. It checks if the
	 * passed event match its one, then if the guard, if present, is verified. If
	 * so, it fires the transition, executing its actions and changing the current
	 * state of the FSM. Else, it does nothing.
	 * 
	 * @param event the event to which the transition must or must not react.
	 */
	public void processEvent(Event event) {
		if (this.event.equals(event.name) && (guard == null || guard.eval(source.owningFSM(), event))) {

			source.owningFSM().LOGGER.info("Transition '" + name + "' processing event : '" + event.name + "'");
			if (!actions.isEmpty()) {
				for (Action a : actions) {
					a.execute(source.owningFSM(), event);
				}
			}
			this.source.owningFSM().setCurrentState(target);
		}
	}

	/**
	 * A getter to get the name of the transition.
	 * 
	 * @return the name of the transition.
	 * 
	 * @see Transition#name
	 */
	public String getName() {
		return name;
	}

}
