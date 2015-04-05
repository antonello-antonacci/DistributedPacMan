import java.rmi.RemoteException;

public interface AuthClientInterface extends java.rmi.Remote {
    public void callBack(int[] playersToken) throws RemoteException;
    public final static String LOOKUPNAME = "AuthClient";
}


	