package fsm;

/**
 * A basic action that disconnects a client from the server
 * 
 * @author Arthur Depasse
 * @version 0.1
 * 
 * @see Action
 * @see FSMServer
 *
 */
public class DisconnectFromServerAction extends Action{
	
	/**
	 * The name of the action
	 */
	String name = "disconnectFromServer";
	
	/**
	 * The override of the execute action, actually disconnecting the client.
	 * 
	 * @param fsm the owning fsm
	 * @param e the associated event
	 * 
	 */
	@Override
	public void execute(FSM fsm, Event e) {
		((FSMClient)fsm).getNetworkClient().disconnect();
	}

}

