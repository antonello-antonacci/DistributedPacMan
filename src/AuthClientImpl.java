import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class AuthClientImpl extends UnicastRemoteObject implements AuthClientInterface {

    protected AuthClientImpl() throws RemoteException {
        super();
    }

    @Override
    public void callBack(int[] playersToken) throws RemoteException {
        Node.getNode().initGame(playersToken);
    }
}
