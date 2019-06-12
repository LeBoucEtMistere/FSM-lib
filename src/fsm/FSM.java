package fsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.Timer;

import utils.FSMLogger;

/**
 * FSMServer is an implementation of a Mealy Machine specialized to represent
 * a Server with all its interactions
 * <p>
 * An FSMServer is characterized by :
 * <ul>
 * <li>A name</li>
 * <li>A list of States owned by the machine</li>
 * <li>References to a starting one, a current one and a final one</li>
 * <li>A collection to store data in the fsm characterizing the current state of
 * the application</li>
 * </ul>
 * </p>
 * 
 * 
 * @author Arthur Depasse
 * @version 0.1
 */
abstract public class FSM {

	/**
	 * A Logger instance publicly accessible to Log any message in the system
	 */
	public Logger LOGGER;

	/**
	 * The name of the machine, used in logging.
	 * 
	 * @see FSMServer#FSMServer(String, BiFunction, int, String)
	 */
	protected String name;

	/**
	 * A reference to the initial state of the machine
	 * 
	 * @see FSMServer#setInitialState(State)
	 */
	protected State initialState;

	/**
	 * A reference to the current state of the machine
	 * 
	 * @see FSMServer#setCurrentState(State)
	 */
	protected State currentState = null;

	/**
	 * A reference to the final state of the machine.
	 * 
	 * @see FSMServer#setFinalState(State)
	 */
	protected State finalState;

	/**
	 * An array of states possessed by the machine.
	 * 
	 * @see FSMServer#addState(State)
	 * @see FSMServer#addState(List)
	 */
	protected ArrayList<State> ownedStates;

	/**
	 * A boolean to know whether the machine is running.
	 */
	protected boolean running = false;

	/**
	 * A queue of events used internally. It allows the system to sequentially react
	 * to events.
	 * 
	 * @see FSMServer#queueEvent(Event)
	 */
	protected BlockingQueue<Event> eventsQueue;

	/**
	 * A mutex used internally to synchronize the current State of the machine.
	 * 
	 * @see FSMServer#getMutex()
	 */
	protected Semaphore mutex;

	/**
	 * An heterogeneous collection for the user to store any kind of data in the fsm
	 * and later access and modify it at its will in the actions.
	 * 
	 * @see FSMServer#setData(String, Object)
	 * @see FSMServer#getData(String)
	 */
	protected Map<String, Object> data;

	/**
	 * The constructor of the class.
	 * 
	 * @param name          the name of the FSM to create
	 * @param parserFactory the factory used to instantiate new parsers
	 * @param port          the port on which the server must listen
	 * @param keystorePwd   the password of the keystore used for SSL and TLS
	 *                      protocols
	 */
	public FSM(String name) {

		this.name = name;
		this.mutex = new Semaphore(1);
		this.ownedStates = new ArrayList<State>();
		this.initialState = null;
		this.finalState = null;
		this.eventsQueue = new ArrayBlockingQueue<Event>(1024);
		this.LOGGER = FSMLogger.setup();
		this.data = new HashMap<String, Object>();
	}

	/**
	 * A function to set data in the internal storage of the FSM. This data may
	 * later be accessed through {@link FSMServer#getData(String)}.
	 * 
	 * @param name the string identifier of the data
	 * @param data the data
	 *
	 * @see FSMServer#getData(String)
	 * @see FSMServer#data
	 */
	public void setData(String name, Object data) {
		this.data.put(name, data);
	}

	/**
	 * A function to access data in the internal storage of the FSM. Data may be set
	 * with {@link FSMServer#setData(String, Object)}.
	 * 
	 * @param name the string identifier of the data to fetch.
	 * @return An Object corresponding to the data. You must cast it back to its
	 *         original type before using it.
	 * 
	 * @see FSMServer#setData(String, Object)
	 * @see FSMServer#data
	 */
	public Object getData(String name) {
		return this.data.get(name);
	}

	/**
	 * A getter for the Semaphore mutex.
	 * 
	 * @return the mutex
	 * 
	 * @see FSMServer#mutex
	 */
	public Semaphore getMutex() {
		return this.mutex;
	}

	/**
	 * A function to add a state to the machine.
	 * 
	 * @param state the state to add
	 * 
	 * @see FSMServer#addState(List)
	 * @see FSMServer#ownedStates
	 */
	public void addState(State state) {
		this.ownedStates.add(state);
		state.setOwningFSM(this);
	}

	/**
	 * A function to add a list of states to the machine.
	 * 
	 * @param states the list of states to add
	 * 
	 * @see FSMServer#addState(State)
	 * @see FSMServer#ownedStates
	 */
	public void addState(List<State> states) {
		this.ownedStates.addAll(ownedStates.size(), states);
		for (State s : states) {
			s.setOwningFSM(this);
		}
	}

	/**
	 * A setter to set the initial state of the machine
	 * 
	 * @param state the state to set as initial state
	 * 
	 * @see FSMServer#initialState
	 */
	public void setInitialState(State state) {
		this.initialState = state;
	}

	/**
	 * A setter to set the final state of the machine
	 * 
	 * @param state the state to set as final state
	 * 
	 * @see FSMServer#finalState
	 */
	public void setFinalState(State state) {
		this.finalState = state;
	}

	/**
	 * A setter to set the current state of the machine
	 * 
	 * @param state the state to set as current state
	 * 
	 * @see FSMServer#currentState
	 */
	public void setCurrentState(State state) {
		if(this.currentState != null) {
			this.currentState.exited();
		}
		try {
			mutex.acquire();
		} catch (Exception e) {
		}
		this.currentState = state;
		this.mutex.release();
		this.currentState.entered();
		this.LOGGER.info("FSM '" + name + "' current state : " + currentState.getName());
	}

	/**
	 * A getter that returns the current state of the machine.
	 * 
	 * @return the current state of the machine.
	 */
	public State getCurrentState() {
		return this.currentState;
	}
	
	
	/**
	 * A setter to stop the machine.
	 * 
	 */
	public void stopRunning() {
		running = false;
	}

	/**
	 * A function to queue an event for the machine to retrieve and treat.
	 * 
	 * @param event the event to queue.
	 * 
	 * @see FSMServer#eventsQueue
	 */
	public void queueEvent(Event event) {
		this.eventsQueue.add(event);
	}
	
	/**
	 * register and launch a timer, that will queue the passed event when it expires
	 * 
	 * @param duration in milliseconds
	 * @param callback the event to register
	 */
	public void launchTimer(int duration, Event callback) {
		Timer timer = new Timer();
		timer.schedule(new FSMTimerTask(this, callback), duration);
		
	}

	/**
	 * The main function that starts and runs the machine according to how it was
	 * configured.
	 */
	public abstract void start();
}

