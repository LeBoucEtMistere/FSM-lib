package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import fsm.FSMServer;

import fsm.Event;

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
public class NetworkSSLServer extends Thread {

	/**
	 * the port on which the server should start
	 */
	private int port = 50300;

	/**
	 * The password of the keystore used for SSL identifications
	 */
	private String keystorePwd = "serverpw";

	/**
	 * A map associating a SSLSocket representing a client with an UUID
	 * 
	 * @see NetworkSSLServer#getClientMap()
	 */
	private Map<UUID, SSLSocket> clientMap;

	/**
	 * A map associating a MessageParser instance with its client UUID
	 */
	private Map<UUID, NetworkMessageParser> parserMap;

	/**
	 * A getter that returns the client map
	 * 
	 * @return the client map
	 * 
	 * @see NetworkSSLServer#clientMap
	 */
	public Map<UUID, SSLSocket> getClientMap() {
		return clientMap;
	}

	/**
	 * A boolean to know whether the server is running.
	 */
	public boolean running = false;

	/**
	 * A reference to the owning fsm
	 */
	private FSMServer fsm;

	/**
	 * The constructor of the class
	 * 
	 * @param fsm         a reference to the owning fsm
	 * @param port        the port on which the server should start
	 * @param keystorePwd the password of the keystore
	 */
	public NetworkSSLServer(FSMServer fsm, int port, String keystorePwd) {
		this.port = port;
		this.keystorePwd = keystorePwd;
		Security.setProperty("crypto.policy", "unlimited");
		this.fsm = fsm;

		clientMap = new HashMap<UUID, SSLSocket>();
		parserMap = new HashMap<UUID, NetworkMessageParser>();
	}
	
	public void stopServer() {
		this.running = false;
	}

	/**
	 * A function that returns the DataInputStream associated with a client
	 * 
	 * @param clientId the UUID that identifies the client
	 * @return The DataInputStream associated with the client
	 */
	public DataInputStream getInputStreamByClient(UUID clientId) {
		try {
			return new DataInputStream(clientMap.get(clientId).getInputStream());
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
	public DataOutputStream getOutputStreamByClient(UUID clientId) {
		try {
			return new DataOutputStream(clientMap.get(clientId).getOutputStream());
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
	public void disconnectClient(UUID uuid) {
		fsm.LOGGER.info("Disconnecting client " + uuid);
		parserMap.get(uuid).running = false;
		try {
			parserMap.get(uuid).join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			clientMap.get(uuid).close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		parserMap.remove(uuid);
		clientMap.remove(uuid);

	}

	/**
	 * The main function that defines the behavior of the thread once it is
	 * launched. It handles new clients connections, fires an associated event, and
	 * creates a parser for this client and starts it in its own separate thread.
	 */
	@Override
	public void run() {

		try {

			SSLServerConfig ssc = new SSLServerConfig(keystorePwd);
			SSLServerSocketFactory sf = ssc.getSSLContext().getServerSocketFactory();
			SSLServerSocket ss = (SSLServerSocket) sf.createServerSocket(port);

			ss.setNeedClientAuth(true);

			fsm.LOGGER.info("Listening on port " + port);

			running = true;
			ss.setSoTimeout(1000);

			while (running) {
				try {
					SSLSocket socket = (SSLSocket) ss.accept();

					socket.startHandshake();

					UUID uuid = UUID.randomUUID();

					fsm.LOGGER.info("Got connection from " + socket);
					fsm.LOGGER.info("Associated uuid for connection : " + uuid);

					Event event = new Event();
					event.name = "newConnection";
					event.setData("uuid", uuid);

					fsm.queueEvent(event);

					fsm.LOGGER.info("Creating a new parser for client " + uuid);
					parserMap.put(uuid, fsm.newParser(uuid, new DataInputStream(socket.getInputStream())));
					parserMap.get(uuid).start();

					clientMap.put(uuid, socket);

				} catch (SocketTimeoutException e) {
					//no problemo
				}
				
			}
		
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
