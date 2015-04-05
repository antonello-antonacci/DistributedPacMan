import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;


@SuppressWarnings("serial")

public class NodeImpl extends UnicastRemoteObject implements NodeInterface {

    public NodeImpl() throws RemoteException {
        super();    // sets up networking
    }

    public String getHello() throws RemoteException {
        return "Ciao, sono " + Node.getNode().getName();
    }

    public void adviceOfAttack(String p1, String p2) throws RemoteException {
    	String looser = Node.getNode().sendAttackResult(p1, p2);
        Node.getNode().manageLooser(looser);
    }
    
    public void ping() throws RemoteException {
        Node.getNode().ping();
    }
    
    public void tokenRequest() throws RemoteException {
    	try {
			Node.getNode().resendToken(getClientHost());
		} catch (ServerNotActiveException e) { }
    }
    
    public void adviceGameOverOfPlayer(String p) throws RemoteException {
    	Node.getNode().manageLooser(p);
    }
    
    public void receiveData(int item,String msg) throws RemoteException {
        try {
            Node.getNode().rcvData(item, msg, getClientHost());
        } catch (ServerNotActiveException e) {
        	//TODO
            e.printStackTrace();
        }
    }

    @Override
    public void receiveCharacterToken(String token) throws RemoteException {
        Node.getNode().receiveCharacterToken(token);

    }

    @Override
    public void startGame() throws RemoteException {
        Node.getNode().startGame();
    }
}
