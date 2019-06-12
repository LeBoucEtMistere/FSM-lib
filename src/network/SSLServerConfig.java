package network;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * A class that manages the setup and configuration of the keystores and the SSL
 * context for a server.
 * 
 * @author Arthur Depasse
 * @version 0.1
 * 
 * @see NetworkSSLServer
 *
 */
public class SSLServerConfig {

	/**
	 * The client keystore containing the client public key
	 */
	private KeyStore clientKeyStore;

	/**
	 * The server keystore containing the server private key
	 */
	private KeyStore serverKeyStore;

	/**
	 * The passphrase of the server keystore
	 */
	private String passphrase;

	/**
	 * The SSLContext that is being setup
	 */
	private SSLContext sslContext;

	/**
	 * A secure random generator used to init the SSLContext
	 */
	static private SecureRandom secureRandom;

	/**
	 * The constructor of the class.
	 * 
	 * @param passphrase the passphrase of the server keystore
	 */
	public SSLServerConfig(String passphrase) {
		this.passphrase = passphrase;
		secureRandom = new SecureRandom();
		secureRandom.nextInt();
		try {
			setupClientKeyStore();
			setupServerKeyStore();
			setupSSLContext();
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	/**
	 * A function that does the setup of the client keystore
	 * 
	 * @throws GeneralSecurityException thrown if it cannot get an instance of the
	 *                                  JKS keystore
	 * @throws IOException              thrown if the actual keystore file cannot be
	 *                                  loaded
	 */
	private void setupClientKeyStore() throws GeneralSecurityException, IOException {
		clientKeyStore = KeyStore.getInstance("JKS");
		clientKeyStore.load(new FileInputStream("keys/client.public"), "public".toCharArray());
	}

	/**
	 * A function that does the setup of the server keystore
	 * 
	 * @throws GeneralSecurityException thrown if it cannot get an instance of the
	 *                                  JKS keystore
	 * @throws IOException              thrown if the actual keystore file cannot be
	 *                                  loaded
	 */
	private void setupServerKeyStore() throws GeneralSecurityException, IOException {
		serverKeyStore = KeyStore.getInstance("JKS");
		serverKeyStore.load(new FileInputStream("keys/server.private"), passphrase.toCharArray());
	}

	/**
	 * A function that setups the SSLContext that will be used to generate sockets.
	 * 
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	private void setupSSLContext() throws GeneralSecurityException, IOException {
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(clientKeyStore);

		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(serverKeyStore, passphrase.toCharArray());

		sslContext = SSLContext.getInstance("TLSv1.2");
		sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), secureRandom);
	}

	/**
	 * A getter for the SSLContext that was initialized.
	 * 
	 * @return the initialized and ready to use SSLContext
	 */
	public SSLContext getSSLContext() {
		return this.sslContext;
	}

}
