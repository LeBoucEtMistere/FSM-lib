package fsm;

import java.util.ArrayList;

/**
 * A class that represent a state of the FSM.
 * 
 * @author Arthur Depasse
 * @version 0.1
 * 
 * @see FSMServer
 *
 */
public class State {

	/**
	 * The name of the state.
	 */
	private String name;

	/**
	 * A getter for the state name
	 * 
	 * @return the state name
	 */
	public String getName() {
		return name;
	}

	/**
	 * A reference to the owning fsm.
	 * 
	 * @see State#setOwningFSM(FSMServer)
	 */
	private FSM owningFSM;

	/**
	 * An array of the outgoing transitions from this state.
	 * 
	 * @see State#addTransitionFrom(Transition)
	 */
	private ArrayList<Transition> outgoingTransitions;

	/**
	 * An array of the incoming transitions to this state.
	 * 
	 * @see State#addTransitionTo(Transition)
	 */
	private ArrayList<Transition> incomingTransitions;
	
	private ArrayList<Action> startActions;
	
	private ArrayList<Action> exitActions;

	/**
	 * Constructor of a state
	 * 
	 * @param name the name of the state
	 */
	public State(String name) {
		this.name = name;

		outgoingTransitions = new ArrayList<Transition>();
		incomingTransitions = new ArrayList<Transition>();
		startActions = new ArrayList<Action>();
		exitActions = new ArrayList<Action>();
	}

	/**
	 * A setter for the owning FSM
	 * 
	 * @param fsm the owning fsm
	 */
	public void setOwningFSM(FSM fsm) {
		this.owningFSM = fsm;
	}

	/**
	 * A getter for the owning fsm
	 * 
	 * @return the owning fsm
	 */
	public FSM owningFSM() {
		return owningFSM;
	}

	/**
	 * A function to add an outgoing transition
	 * 
	 * @param t the transition to add
	 * 
	 * @see State#outgoingTransitions
	 */
	public void addTransitionFrom(Transition t) {
		outgoingTransitions.add(t);
	}

	/**
	 * A function to add an incoming transition
	 * 
	 * @param t the transition to add
	 * 
	 * @see State#incomingTransitions
	 */
	public void addTransitionTo(Transition t) {
		incomingTransitions.add(t);
	}
	
	/**
	 * A function to add an Action that will be executed when the state is entered
	 * 
	 * @param a action to add
	 */
	public void onEnteredAction(Action a) {
		startActions.add(a);
	}
	
	/**
	 * A function to add an Action that will be executed when the state is exited
	 * 
	 * @param a action to add
	 */
	public void onExitAction(Action a) {
		exitActions.add(a);
	}

	/**
	 * A function called by the fsm every time it needs to propagate an incoming
	 * Event to its current state. It forwards it to all of its outgoing
	 * transitions.
	 * 
	 * @param event the event to forward
	 */
	public void processEvent(Event event) {
		assert (owningFSM != null);
		owningFSM.LOGGER.info("State " + name + " processing event : " + event.name);
		for (Transition t : outgoingTransitions) {
			t.processEvent(event);
		}
	}
	
	public void entered() {
		Event e = new Event();
		e.name = "StateEntered";
		for(Action a : startActions) {
			a.execute(owningFSM, e);
		}
	}
	
	public void exited() {
		Event e = new Event();
		e.name = "StateExited";
		for(Action a : exitActions) {
			a.execute(owningFSM, e);
		}
	}

}
