package utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * A console logger designed to log the activity of the FSM according to the
 * style defined in {@link LogFormatter}
 * 
 * @author Arthur Depasse
 * @version 0.1
 * 
 * @see LogFormatter
 *
 */
public class FSMLogger {

	/**
	 * The static instance of the logger
	 */
	private static Logger LOGGER = null;

	/**
	 * A static function that setups the logger and returns it for use.
	 * 
	 * @return the logger
	 */
	public static Logger setup() {
		LOGGER = Logger.getLogger(FSMLogger.class.getName());
		LOGGER.setUseParentHandlers(false);

		LogFormatter formatter = new LogFormatter();
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(formatter);

		LOGGER.addHandler(handler);
		return LOGGER;
	}
}
