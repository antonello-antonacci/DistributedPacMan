import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("serial")

public class AuthServerImpl extends UnicastRemoteObject implements AuthServerInterface {

    private Hashtable<String, AuthClientInterface> players;
	List<String> playersList = new ArrayList<String>();
    
    //Game players settings
    private  int NPACS = 1;
    private  int NGHOSTS = 1;
    private int NPLAYERS ;
    private int[] playersSettings;

    protected AuthServerImpl(int nPacs,int nGhosts) throws RemoteException {
        super();
        NPACS = nPacs;
        NGHOSTS = nGhosts;
        NPLAYERS = nPacs + nGhosts;
        playersSettings = new int[]{NPACS, NGHOSTS};
        players = new Hashtable<String, AuthClientInterface>();
    }

    public void doRegistration(AuthClientInterface client) {
        try {
            System.out.println(getClientHost() + " Connesso N Clients: " + players.size());

            //maintain an address indexed list
            players.put(getClientHost(), client);
            playersList.add(getClientHost());
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
        if (players.size() == NPLAYERS) {
            callAllBack();
        }
    }

    public String[] getPlayers() {
        String[] arr = new String[playersList.size()];

        for(int i=0;i<playersList.size();i++){
        	arr[i] = playersList.get(i);
        	System.out.println("item :"+ arr[i]);
        }
        return arr;
    }

    public String getPlayer() {
        try {
            return getClientHost();

        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void callAllBack() {
        Iterator<String> iterator = players.keySet().iterator();

        while (iterator.hasNext()) {
            String next = iterator.next();
            try {
                players.get(next).callBack(playersSettings);

            } catch (RemoteException e) {

                e.printStackTrace();
            }
        }
        
        players.clear();
        playersList.clear();
        System.out.println("The game can start now ...");
        System.out.println("Waiting for other players to build a new game");

    }
}
