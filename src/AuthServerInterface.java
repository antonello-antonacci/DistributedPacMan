import java.util.Hashtable;

public interface AuthServerInterface extends java.rmi.Remote {
    
	public void doRegistration(AuthClientInterface au) throws java.rmi.RemoteException;
    public String[] getPlayers() throws java.rmi.RemoteException;
    public String getPlayer() throws java.rmi.RemoteException;
    public final static String LOOKUPNAME = "AuthServer";
}
