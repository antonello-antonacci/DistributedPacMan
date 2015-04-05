import java.rmi.RemoteException;


public interface NodeInterface extends java.rmi.Remote {
    public void receiveData(int item,String msg) throws RemoteException;

    public void receiveCharacterToken(String token) throws RemoteException;
    public void ping() throws RemoteException;
    public void tokenRequest() throws RemoteException;
    public void startGame() throws RemoteException;
    public void adviceOfAttack(String p1, String p2) throws RemoteException;
    public void adviceGameOverOfPlayer(String p1) throws RemoteException;
    /**
     * The name used in the RMI registry service.
     */
    public final static String LOOKUPNAME = "DistributedPacMan";
}
