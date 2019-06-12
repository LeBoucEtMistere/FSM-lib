package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.security.Security;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import fsm.FSMServer;

import fsm.Event;
import fsm.FSMClient;

/**
 * A class that handle a SSL server for the FSM. It inherits from Thread and
 * therefore is runnable in its own thread.
 * 
 * @author Arthur Depasse
 * @version 0.1
 * 
 * @see FSMServer
 *
 */
public class NetworkSSLClient extends Thread {

	/**
	 * the port on which the client should try to connect
	 */
	private int port = 50300;
	
	/**
	 * the port on which the client should try to connect
	 */
	private InetAddress address;

	/**
	 * The password of the keystore used for SSL identifications
	 */
	private String keystorePwd = "clientpw";

	/**
	 * A SSLSocket representing the server
	 * 
	 */
	private SSLSocket server;

	/**
	 * A MessageParser instance to parse incoming server message
	 */
	private NetworkMessageParser parser;

	/**
	 * A reference to the owning fsm
	 */
	private FSMClient fsm;

	/**
	 * The constructor of the class
	 * 
	 * @param fsm         a reference to the owning fsm
	 * @param port        the port on which the server should start
	 * @param keystorePwd the password of the keystore
	 */
	public NetworkSSLClient(FSMClient fsm, InetAddress address, int port, String keystorePwd) {
		this.address = address;
		this.port = port;
		this.keystorePwd = keystorePwd;
		Security.setProperty("crypto.policy", "unlimited");
		this.fsm = fsm;

	}

	/**
	 * A function that returns the DataInputStream associated with a client
	 * 
	 * @param clientId the UUID that identifies the client
	 * @return The DataInputStream associated with the client
	 */
	public DataInputStream getInputStream() {
		try {
			return new DataInputStream(server.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * A function that returns the DataOutputStream associated with a client
	 * 
	 * @param clientId the UUID that identifies the client
	 * @return The DataOutputStream associated with the client
	 */
	public DataOutputStream getOutputStream() {
		try {
			return new DataOutputStream(server.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * A function that disconnects the client associated with this UUID and closes
	 * the associated socket
	 * 
	 * @param uuid the identifier of the client to disconnect
	 */
	public void disconnect() {
		fsm.LOGGER.info("Disconnecting from server ");
		parser.running = false;
		try {
			parser.join(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The main function that defines the behavior of the thread once it is
	 * launched. It handles new clients connections, fires an associated event, and
	 * creates a parser for this client and starts it in its own separate thread.
	 */
	@Override
	public void run() {
		
		Security.setProperty("crypto.policy", "unlimited");

		try {
			SSLClientConfig ssc = new SSLClientConfig(keystorePwd);

			SSLSocketFactory sf = ssc.getSSLContext().getSocketFactory();
			SSLSocket socket = (SSLSocket) sf.createSocket(address, port);
			
			socket.startHandshake();

			fsm.LOGGER.info("Connected to " + socket);
			
			Event event = new Event();
			event.name = "newConnection";

			fsm.queueEvent(event);

			fsm.LOGGER.info("Creating a new parser for server ");
			parser = fsm.newParser(new DataInputStream(socket.getInputStream()));
			parser.start();

			server = socket;

			
		} catch (IOException ie) {
			ie.printStackTrace();
		}

	}
}
