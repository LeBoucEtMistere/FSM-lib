package fsm;

import java.io.DataInputStream;
import java.net.InetAddress;
import java.util.UUID;
import java.util.function.BiFunction;
import javafx.util.Pair;

import network.NetworkMessageParser;
import network.NetworkSSLClient;

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
public class FSMClient extends FSM {

	/**
	 * An instance of a NetworkServer which is managing new connections and clients
	 * sockets.
	 * 
	 * @see FSMServer#getNetworkServer()
	 */
	private NetworkSSLClient networkClient;

	/**
	 * A function used by the machine to instantiate messageParser for each new
	 * connection. It must provide a way to parse appropriately the input of the
	 * client channel according to the current state. Currently, the system restrict
	 * the user to the use of a single type of external message per state. This
	 * limitation is imposed by the library jasn1 because it must know the type of
	 * the expected message to parse the InputStream
	 * 
	 * @see FSMServer#FSMServer(String, BiFunction, int, String)
	 */
	private BiFunction<Pair<UUID, DataInputStream>, FSMClient, NetworkMessageParser> parserFactory;


	/**
	 * The constructor of the class.
	 * 
	 * @param name          the name of the FSM to create
	 * @param parserFactory the factory used to instantiate new parsers
	 * @param port          the port on which the server must listen
	 * @param keystorePwd   the password of the keystore used for SSL and TLS
	 *                      protocols
	 */
	public FSMClient(String name,
			BiFunction<Pair<UUID, DataInputStream>, FSMClient, NetworkMessageParser> parserFactory,InetAddress address, int port,
			String keystorePwd) {
		
		super(name);

		this.networkClient = new NetworkSSLClient(this, address, port, keystorePwd);
		this.parserFactory = parserFactory;
	}

	/**
	 * A getter for the Network Server.
	 * 
	 * @return the Network Server
	 * 
	 * @see FSMServer#networkServer
	 */
	public NetworkSSLClient getNetworkClient() {
		return this.networkClient;
	}
	
	/**
	 * A function that instantiate a new NetworkMessageParser or an inherited class
	 * according to the factory function provided by the user via the constructor
	 * {@link FSMServer#FSMServer(String, BiFunction, int, String)}
	 * 
	 * @param dis the DataInputStream associated with the socket of the server
	 * @return an instance of the NetworkMessageParser or an inherited class
	 *         specified by the factory the client gave
	 * 
	 * @see FSMServer#parserFactory
	 */
	public NetworkMessageParser newParser(DataInputStream dis) {
		try {
			return parserFactory.apply(new Pair<>(null, dis), this);
		} catch (Exception e) {
			e.printStackTrace();
			this.LOGGER.severe("Cannot instantiate a parser");
			return null;
		}
	}

	/**
	 * The main function that starts and runs the machine according to how it was
	 * configured.
	 */
	@Override
	public void start() {
		this.LOGGER.info("FSM '" + name + "' starting.");

		this.networkClient.start();

		setCurrentState(initialState);

		if (this.finalState == null)
			this.LOGGER
					.warning("FSM '" + name + "' has no final state set, it will never end running unless you kill it");

		this.running = true;
		while (this.running || !this.eventsQueue.isEmpty()) {

			// check for end of loop
			if (this.currentState == this.finalState) {
				this.LOGGER.info("FSM '" + name + "' has reached final state");
				this.running = false;
			}

			// dispatch events
			if (!this.eventsQueue.isEmpty()) {
				Event e = this.eventsQueue.remove();
				this.LOGGER.info("FSM '" + name + "' processing event : " + e.name);
				this.currentState.processEvent(e);
			}

		}
		try {
			this.networkClient.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

