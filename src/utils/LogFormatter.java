package utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * A class that describes the formatter used by the logger of the FSM. Relies on
 * the ANSI console plugin under eclipse
 * 
 * @author Arthur Depasse
 * @version 0.1
 * 
 * @see FSMLogger
 *
 */
public class LogFormatter extends Formatter {
	/**
	 * A series of ANSI escape codes. The use of them requires an eclipse plugin
	 * that manages ANSI, for instance ANSI console
	 */
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	/**
	 * Main function that format the message accordingly to the style defined for
	 * this formatter
	 */
	@Override
	public String format(LogRecord record) {
		StringBuilder builder = new StringBuilder();
		builder.append(ANSI_RESET);
		switch (record.getLevel().getName()) {
		case "INFO":
			builder.append(ANSI_WHITE);
			break;
		case "WARNING":
			builder.append(ANSI_YELLOW);
			break;
		case "SEVERE":
			builder.append(ANSI_RED);
			break;

		default:
			builder.append(ANSI_WHITE);
			break;
		}

		builder.append("[");
		builder.append(calcDate(record.getMillis()));
		builder.append("]");

		builder.append(" [");
		builder.append(record.getSourceClassName());
		builder.append("]");

		builder.append(" [");
		builder.append(record.getLevel().getName());
		builder.append("]");

		// builder.append(ANSI_BLACK);
		builder.append(" - ");
		builder.append(record.getMessage());

		Object[] params = record.getParameters();

		if (params != null) {
			builder.append("\t");
			for (int i = 0; i < params.length; i++) {
				builder.append(params[i]);
				if (i < params.length - 1)
					builder.append(", ");
			}
		}

		builder.append(ANSI_RESET);
		builder.append("\n");
		return builder.toString();
	}

	/**
	 * Helper function that convert a millis Date into a readable time in format
	 * HH:mm:ss
	 * 
	 * @param millisecs the millis Date
	 * @return the string representing the time
	 */
	private String calcDate(long millisecs) {
		SimpleDateFormat date_format = new SimpleDateFormat("HH:mm:ss");
		Date resultdate = new Date(millisecs);
		return date_format.format(resultdate);
	}
}
