package fsm;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A class that represent an event in the FSM. An event is composed of its name,
 * its origin, and heterogeneous data.
 * 
 * @author Arthur Depasse
 * @version 0.1
 * 
 * @see FSMServer
 *
 */
public class Event {

	/**
	 * The name of the event. The machine use it to identify them.
	 */
	public String name = "";

	/**
	 * The origin of the event. It traces the UUID of the client(Socket) that sent
	 * it or if it is internal, it will be the nil UUID (0).
	 */
	public UUID origin;
	
	/**
	 * The map storing the data associated with the event.
	 * 
	 * @see Event#setData(String, Object)
	 * @see Event#getData(String)
	 */
	private Map<String, Object> data = new HashMap<String, Object>();
	
	/**
	 * A function to populate the data in the Event.
	 * 
	 * @param name a string identifier to name the data
	 * @param data the data to store
	 * 
	 * @see Event#data
	 * @see Event#getData(String)
	 */
	public void setData(String name, Object data) {
		this.data.put(name, data);
	}
	
	/**
	 * A function to retrieve data from the Event
	 * 
	 * @param name a string identifier that is associated with the data
	 * @return the data as an Object, must be casted back to its original type to use
	 * 
	 * @see Event#setData(String, Object)
	 * @see Event#data
	 */
	public Object getData(String name) {
		return this.data.get(name);
	}

}
