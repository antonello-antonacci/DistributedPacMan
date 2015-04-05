import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;

import javax.swing.JPanel;
import javax.swing.Timer;


public class World extends JPanel {
    public static CharacterController myCharacterController;
    public static Hashtable<String, Character> otherCharacters;

    private ArrayList<Point> powerdotsEaten = new ArrayList<Point>();
    
    private Timer timer;
    private Maze mz;
    public static boolean lastPaint = true;
    public static boolean reverse = false;
    
    public final int INITCHA = 0;
    public final int CHA = 1;
    public final int MAZE = 2;
    public final int REVERSE = 3;
    public final int STOPREVERSE = 4;

     

    public World() {
        mz = new Maze(840, 480);
        myCharacterController = new CharacterController();
        myCharacterController.setMaze(mz);

        otherCharacters = new Hashtable<String, Character>();

        this.setOpaque(true);

        this.setBackground(new Color(18, 51, 91));
        timer = new Timer(10, new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        timer.start();
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(840, 480);
    }

    public void createAndSendMyCharacter(int type,int index) {
        myCharacterController.createAndSendMyCharacter(type,index);
    }

    public void startGame() {
        myCharacterController.startTimer();
    }

    public void paint(Graphics g) {

        super.paint(g);


        paintMaze(g);
        Graphics2D brush = (Graphics2D) g;

        brush.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        try
        {
        for (String k : otherCharacters.keySet()) {

        	Character cha = otherCharacters.get(k);   	
        	brush.drawImage(cha.img,(int)cha.x,(int)cha.y,mz.getChaDim(),mz.getChaDim(), null);}


        if (myCharacterController.getCharacter() != null) {
        	Character cha = myCharacterController.getCharacter();
        	brush.drawImage(cha.img,(int)cha.x,(int)cha.y,mz.getChaDim(),mz.getChaDim(), null);

        }
        }
        catch (ConcurrentModificationException e){ 
        	//System.out.println(e.getMessage() + " Davoli ti vede !!!");
        	System.out.println(e.getMessage() + "");
        } 

    }
    
    public void removeLooser(String player) {
        otherCharacters.remove(player);
        //System.out.println("OtherCharacter: ");
        //for(String k: otherCharacters.keySet())
        //    System.out.print(k + " ");
    }
    public void paintMaze(Graphics g) {

        Color wallColor = new Color(95, 140, 205);

        int dotNum = 0;
        Graphics2D brush = (Graphics2D) g;

        String[][] iMaze = mz.getIMaze();
        Dimension cellDim = mz.getCellDim();

        for (int j = 0; j < iMaze.length; j++)
            for (int i = 0; i < iMaze[0].length; i++) {

                switch (iMaze[j][i]) {
                    case "BLANK":
                        break;
                    case "WALL":
                        brush.setColor(wallColor);
                        brush.fillRect(j * cellDim.width, i * cellDim.height, cellDim.width, cellDim.height);
                        break;
                    case "DOT":
                    	dotNum++;
                        brush.setColor(Color.WHITE);
                        brush.fillOval(j * cellDim.width + (cellDim.width / 2), i * cellDim.height + (cellDim.height / 2), 3, 3);
                        break;
                    case "POWERDOT":
                    	dotNum++;
                        brush.setColor(Color.WHITE);
                        brush.fillOval(j * cellDim.width + (cellDim.width / 2), i * cellDim.height + (cellDim.height / 2), 8, 8);
                        break;
                    case "DOOR":
                        break;
                    default:
                        break;
                }
            }
        
        if(dotNum == 0 && !mz.GameOverGhost){
        	//I win
        	//send to all 
        	gameOver(Character.PAC);      	
        }
        
    }
    
    public void gameOver(int winner){ 	
    	switch(winner){
    	case(Character.PAC):
    		mz.setGameOver("Pacman");
    		break;
    	case(Character.GHOST):
    		mz.setGameOver("Ghost");
    		break;
    	}   	
    }
    
    
    public void updateWorld(int item,String msg,String client){
    	switch(item){
    	case INITCHA :
    		updateInitCharacter(client,msg);
    		break;
    	case CHA :
    		updateCharacter(client,msg);
    		break;
    	case MAZE :
    		updateMaze(msg);
    		break;
    	case REVERSE :
    		updatePowerdots(msg);
    		reverse = true ;
    		break;
    	case STOPREVERSE :
    		reverse = false ;
    		break;
    	}    	
    }
    
    public void updateMaze(String maze) {
        String[] colums = maze.split(";");
        String[][] localmaze = mz.getIMaze();
        for (int i = 0; i < localmaze.length; i++) {
            String[] elems = colums[i].split(",");
            localmaze[i] = elems;
        }
        mz.setIMaze(localmaze);
 
        if(Node.getNode().checkLeader()){
        	if (checkPowerdots()){
        		myCharacterController.sendData(REVERSE);        	
        		Node.getNode().startReverseTimer();
        	}
        }
    }

    public void handlePowerDot(){
        checkPowerdots();
        myCharacterController.sendData(REVERSE);
        Node.getNode().startReverseTimer();
    }
    
    
    private void updatePowerdots(String msg){
    	if(!msg.isEmpty()){
        String[] elem = msg.split(";");
        mz.powerdotsPos.clear();
    	for (int i=0; i<elem.length; i++ ){
    		String[] elem2 = elem[i].split(",");
    		mz.powerdotsPos.add(new Point(Integer.parseInt(elem2[0]),Integer.parseInt(elem2[1])));
    	}}
    }
    
    public boolean checkPowerdots(){
        for(int i=0; i< mz.powerdotsPos.size();i++){
            if(mz.iMaze[(int)mz.powerdotsPos.get(i).getX()][(int)mz.powerdotsPos.get(i).getY()].equals("BLANK")){
    			mz.powerdotsPos.remove(i);
    			return true;
    		}
    	}
    	return false;
    }
    
    public void updateCharacter(String k, String c) {
        String[] data = c.split(" ");
        Character cobj = otherCharacters.get(k);
        cobj.direction = (int) Float.parseFloat(data[0]);
        cobj.x = (int) Float.parseFloat(data[1]);
        cobj.y = (int) Float.parseFloat(data[2]);

        otherCharacters.put(k, cobj);
    }
    
    public void updateInitCharacter(String k, String c) {
    	Character cobj = null ;
    	String[] data = c.split(" ");
    	int direction = (int) Float.parseFloat(data[0]);
    	int x = (int) Float.parseFloat(data[1]);
    	int y = (int) Float.parseFloat(data[2]);
    	int index = (int) Float.parseFloat(data[4]);

    	if((int) Float.parseFloat(data[3]) == Character.PAC){ cobj = new Pacman(x,y,direction,index);}
    	else{ cobj = new Ghost(x,y,direction,index);}

    	otherCharacters.put(k, cobj);
    	
    }
    
    public int[] getNGhostAndPacs() {
    	int cPacs = 0, cGhosts = 0;
    	for(String key: otherCharacters.keySet()){
    		Character c = otherCharacters.get(key);
    		if(c instanceof Pacman)
    			cPacs++;
    		else if(c instanceof Ghost)
    			cGhosts++;
    	}
    	if (myCharacterController.getCharacter() instanceof Pacman )
    		cPacs++;
    	else if (myCharacterController.getCharacter() instanceof Ghost ) 
    		cGhosts++;
    	
    	int[] data = new int[] {cPacs, cGhosts};
    	return data;
    }
}
