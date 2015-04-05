import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class Node extends PacEnv {

    private static Hashtable<String, NodeInterface> netConns;
    private String[] players;
    public List<String> loosers = new ArrayList<String>();
    public String me;

    private KeyboardFocusManager manager;
    protected static AuthServerInterface authConn = null;
    protected static Node n;
    private int LEADER = 0;
    private int[] charactersToken;
    private int initnPacs = 0;
    private int initnGhosts = 0;
    private int myCharacterType;
    private int myCharacterIndex;
    private Timer callerFirstToken;
    private Timer pingTimer;
    private JFrame myframe = null;
    private boolean tokenReceived = false;
    private boolean tokenSent = false;
    
    private java.util.Timer reverseTimer = new java.util.Timer();
    
    
    public Node() {
        super();
        //runRmi();
        createGui();
        netConns = new Hashtable<String, NodeInterface>();
        NodeImpl ni;

        try {
            ni = new NodeImpl();
            Naming.rebind(NodeInterface.LOOKUPNAME, ni);
        } catch (RemoteException e) {
            String a = e.getCause().toString();
            a = a.replace("java.net.", "");
            a = a.replace("java.rmi.", "");
        } catch (MalformedURLException e) {
            String a = e.getCause().toString();
            a = a.replace("java.net.", "");
            a = a.replace("java.rmi.", "");
        }
    }

    public static Node getNode() {
        return n;
    }

    public static void main(String args[]) {
        n = new Node();
    }

    //abstract method in PacEnv
    public void registerMyself(String server) {

        if (server.isEmpty())
            JOptionPane.showMessageDialog(myframe, "Enter server address", "Inane warning", JOptionPane.WARNING_MESSAGE);
        else {
            try {
                //connecting to server for registration
                AuthClientImpl authClient = new AuthClientImpl();
                authConn = (AuthServerInterface) Naming.lookup("//" + server + "/" + AuthServerInterface.LOOKUPNAME);
                authConn.doRegistration(authClient);
            } catch (Exception e) {
                String a = e.getCause().toString();
                a = a.replace("java.net.", "");
                a = a.replace("Exception", "");
                JOptionPane.showMessageDialog(myframe, a, "Inane error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }


    public void initGame(int[] charactersToken) {
        try {
            players = authConn.getPlayers();
            me = authConn.getPlayer();
            this.charactersToken = charactersToken;
            initnPacs = charactersToken[Character.PAC];
            initnGhosts = charactersToken[Character.GHOST];
            for (int i = 0; i < players.length; i++) {
                if (!players[i].equals(me))
                    netConns.put(players[i], (NodeInterface) Naming.lookup("//" + players[i] + "/" + NodeInterface.LOOKUPNAME));
            }

            callerFirstToken = new Timer();
            pingTimer = new Timer();
            
            TimerTask action = new TimerTask() {
                public synchronized void run() {
                	if(tokenSent)
                		callerFirstToken.cancel();                		
                	//System.out.println(players[LEADER] +"/ "+ me +" /"+ tokenSent);
                    if (players[LEADER].equals(me) && !tokenSent) {
                        createAndSendCharacterToken();
                    }
                }

            };
            
            TimerTask ping = new TimerTask() {
            	public synchronized void run() {
            		int index = findMyPositionInList(players, me);
            		int previ = (index > 0) ? index - 1 : players.length - 1;
            		
            		while(previ != index && !tokenReceived) {
            			boolean crashed = false;
            			String prev = players[previ];
            			try {
            				netConns.get(prev).tokenRequest();
            			} catch (RemoteException e) {
            				crashed = true;
            				players = removeElementFromPlayers(prev);
            			}
            			
            			if(!crashed)
            				break;
            			index = findMyPositionInList(players, me);
            			previ = (index > 0) ? index - 1 : players.length - 1;
            			
            			//System.out.println("Leader: "+players[LEADER]);
            			
            		}
            		
            	}
            };
            
            callerFirstToken.schedule(action, 500, 500);
            pingTimer.schedule(ping, 1000, 1000);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void resendToken(String idReq){
    	if(tokenSent){
    		String token = this.charactersToken[Character.PAC] + ";" + this.charactersToken[Character.GHOST];
    		sendCharacterTokenTo(idReq, token);
    	}
    }
    private void createAndSendCharacterToken() {
    		
            int choice = showCharacterDialog(charactersToken);
            switch (choice) {
                case Character.PAC:
                	myCharacterIndex = initnPacs - charactersToken[Character.PAC];
                    myCharacterType = Character.PAC;
                    break;
                case Character.GHOST:
                	myCharacterIndex = initnGhosts - charactersToken[Character.GHOST];
                    myCharacterType = Character.GHOST;
                    break;
            }
            this.charactersToken[choice]--;
        if(players.length>1) {
            int next = (findMyPositionInList(players, me) + 1) % players.length;
            String token = this.charactersToken[Character.PAC] + ";" + this.charactersToken[Character.GHOST];
            while (!sendCharacterTokenTo(players[next], token)) {
                next = (findMyPositionInList(players, me) + 1) % players.length;
            }
        }else{
        	tokenSent = true;
        	startGame();
        }
        
        
    }

    public void receiveCharacterToken(String remainingCharacters) {
        
        if(tokenReceived)
        	return;
        
        tokenReceived = true;
        //System.out.println("Token received: " + remainingCharacters);        
        if (players[LEADER].equals(me)) {
        	startGame();
            for (String key : netConns.keySet()) {
                try {
                    netConns.get(key).startGame();
                } catch (RemoteException e) {
                	players = removeElementFromPlayers(key);
                }
            }            
        } else {
        	String[] rc = remainingCharacters.split(";");
            charactersToken[Character.PAC] = Integer.parseInt(rc[Character.PAC]);
            charactersToken[Character.GHOST] = Integer.parseInt(rc[Character.GHOST]);
            createAndSendCharacterToken();
        }
    }

    public boolean isLooser(String player){
        if(loosers.contains(player))
            return true;
        return false;
    }
    public void attack(String player) {
    	if (players[LEADER].equals(me)) {
    		String looser = sendAttackResult(me, player);
            manageLooser(looser);
    	} else {
    		try {
				netConns.get(players[LEADER]).adviceOfAttack(me, player);
    		} catch (RemoteException e) {

                manageCrash(players[LEADER]);
            }
        }
    }
    public String sendAttackResult(String p1, String p2) {
    	String pac = null, ghost = null, looser = null;
        Character c1 = null, c2 = null;

        if(me.equals(p1)){
            c1 = World.myCharacterController.getCharacter();
            c2 = World.otherCharacters.get(p2);
        } else if (me.equals(p2)) {
            c1 = World.otherCharacters.get(p1);
            c2 = World.myCharacterController.getCharacter();
        } else {
            c1 = World.otherCharacters.get(p1);
            c2 = World.otherCharacters.get(p2);
        }

    	if ( c1 instanceof Ghost)
    		ghost = p1;
    	else if (c1 instanceof Pacman)
    		pac = p1;
    	if (c2 instanceof Ghost)
    		ghost = p2;
    	else if (c2 instanceof Pacman)
    		pac = p2;

    	if(pac != null && ghost != null) {
    		if(World.reverse) {
    			looser = ghost;
    		} else {
    			looser = pac;
            }
    		
    		ArrayList<String> crashed = new ArrayList<String>();
    		for (String key : netConns.keySet()) {
    			try {
    				netConns.get(key).adviceGameOverOfPlayer(looser);
   	         	} catch (RemoteException e) {
                    crashed.add(key);
                }
   	     	}
    		for (String key: crashed){
    			manageCrash(key);
    		}

    	}
        //System.out.println("Looser: "+looser);
        return looser;
    }
   
    public void manageLooser(String looser) {
        if(!loosers.contains(looser)) {
            loosers.add(looser);
            //System.out.println("New Looser: "+looser);
            int i = 0;
            for (String k : loosers) {
                System.out.print("Looser "+i+": "+k + ". ");
                i++;
            }
            if (me.equals(looser)) {
                getWorld().myCharacterController.setMeAsLooser();
            } else {
                getWorld().removeLooser(looser);
            }
            
            int[] npng = getWorld().getNGhostAndPacs();
            if(npng[Character.PAC] == 0)
            	getWorld().gameOver(Character.GHOST);
        }

    }
    
    public void startGame() {	
    	//System.out.println("Type : "+myCharacterType+" index:" +myCharacterIndex );
    	pingTimer.cancel();
        getWorld().createAndSendMyCharacter(myCharacterType,myCharacterIndex);
        getWorld().startGame();
    }

    private int findMyPositionInList(String[] players, String me) {
        int i = 0;
        while (i < players.length) {
            if (players[i].equals(me))
                break;
            i++;
        }
        return i;
    }

    //Send message msg to player i*
    private boolean sendCharacterTokenTo(String id, String token) {
    	boolean sent = true;
        try {
            netConns.get(id).receiveCharacterToken(token);
        } catch (RemoteException e) {
        	players = removeElementFromPlayers(id);
        	sent = false;
        }
        if(sent) 
        	tokenSent = true;  
        return sent;
    }

    public void broadcastData(int item,String data) {
    	ArrayList<String> crashed = new ArrayList<String>();
        for (String key : netConns.keySet()) {
            try {
                netConns.get(key).receiveData(item,data);
            } catch (RemoteException e) {
            	crashed.add(key);
            }
        }
        for(String key : crashed){
        	manageCrash(key);
        }
    }

    public void rcvData(int item, String msg, String client) {
    	getWorld().updateWorld(item,msg,client);
    }

    public static void runRmi() {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            {
                try {
                    java.rmi.registry.LocateRegistry.getRegistry();
                } catch (Exception e1) {
                    e.printStackTrace();
                    //rmi già attivo
                }
            }
        }

    }
    
    public void removePlayerConn(String k){
    	netConns.remove(k);
    }

	public boolean checkLeader() {
		if (me.equals(players[LEADER])){
			return true;
		}else{
            return false;
            }
		
	}

	public void startReverseTimer() {
		
		TimerTask actReverse = new TimerTask() {
            public void run() {
            	reverseTimer.cancel();
            	getWorld().reverse = false;
            	broadcastData(getWorld().STOPREVERSE,"");
            }
        };
        	
    	reverseTimer.cancel();
    	reverseTimer = new java.util.Timer();
        getWorld().reverse=true;

    	reverseTimer.schedule(actReverse, 5000);
    	
	}

	private void manageCrash(String key){

		//System.out.println("Player "+ key + " crashed");
		if(key.equals(players[LEADER]))
			World.reverse=false;

		players = removeElementFromPlayers(key);
		World.otherCharacters.remove(key);
		netConns.remove(key);

		int[] npng = getWorld().getNGhostAndPacs();
		if(npng[Character.PAC] == 0)
			getWorld().gameOver(Character.GHOST);



	}
    
    private String[] removeElementFromPlayers(String player){
        int count = 0;
        for(int i=0; i<players.length; i++){
            if(players[i].equals(player)){
                count++;
            }
        }
        if (count == 0)
            return players;

        String[] new_players = new String[players.length - count];

        for(int i=0, j=0; i<players.length; i++){
            if(!players[i].equals(player)){
                new_players[j] = players[i];
                j++;
            }
        }

        return new_players;
    }
    
    public void ping(){
        //ignore
    }
}


