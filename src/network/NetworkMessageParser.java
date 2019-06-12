package network;

import java.io.DataInputStream;
import java.util.UUID;
import javafx.util.Pair;
import java.util.concurrent.Semaphore;

import fsm.FSM;
import fsm.FSMServer;

/**
 * An abstract class that defines the interface of a NetworkMessageParser. It
 * inherits from Thread and therefore is runnable in its own thread. This class
 * is made to handle and parse data from a single associated client.
 * 
 * @author Arthur Depasse
 * @version 0.1
 * 
 * @see NetworkSSLServer
 * @see FSMServer
 *
 */
public abstract class NetworkMessageParser extends Thread {

	/**
	 * The DataInputSTream that is linked with the clients socket or the server socket
	 */
	protected DataInputStream input;

	/**
	 * The UUID associated with the client, null if it is associated wit ha server
	 */
	protected UUID originId;

	/**
	 * The fsm associated with this parser
	 */
	protected FSM fsm;

	/**
	 * The mutex shared with the FSM on the currentState. It prevents the parser to
	 * read it while the FSM is modifying it.
	 * 
	 * @see NetworkMessageParser#getMutex()
	 */
	protected Semaphore mutex;

	/**
	 * A boolean to know whether or not this parser is running.
	 */
	public boolean running;

	/**
	 * The constructor of the class used to parse messages from a client.
	 * 
	 * @param origin the UUID of the associated client
	 * @param fsm    the owning fsm
	 */
	public NetworkMessageParser(Pair<UUID, DataInputStream> origin, FSM fsm) {
		try {
			this.input = origin.getValue();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.fsm = fsm;
		this.mutex = fsm.getMutex();
		this.originId = origin.getKey();
	}
	
	/**
	 * A getter for the mutex
	 * @return the mutex
	 * 
	 * @see NetworkMessageParser#mutex
	 */
	public Semaphore getMutex() {
		return mutex;
	}

	/**
	 * An abstract function that does the actual parsing in regard of the
	 * currentState of the machine. It must be redefined by the users.
	 * 
	 * @param currentState the current state of the FSM
	 */
	public abstract void parse(String currentState);

	/**
	 * The run method of the Thread, it is responsible for fetching the currentState
	 * and passing it on to the parser while the thread is running.
	 */
	@Override
	public void run() {
		running = true;
		while (running) {
			try {
				mutex.acquire();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String currentState = fsm.getCurrentState().getName();
			mutex.release();
			parse(currentState);
		}
	}

}
