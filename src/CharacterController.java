import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimerTask;

import javax.swing.Timer;


public class CharacterController implements KeyEventDispatcher {

    
    private Character myCharacter = null;
    private Point myChaPos;

    private final int UP = 0;
    private final int DOWN = 1;
    private final int LEFT = 2;
    private final int RIGHT = 3;
    
    private static final int INITCHA = 0;
    private static final int CHA = 1;
    private static final int MAZE = 2;
    private static final int REVERSE = 3;
    
    private static KeyboardFocusManager manager;
    private Timer posTimer;
    private Timer imgTimer;
    private java.util.Timer reverseTimer = new java.util.Timer();;
    private Maze maze;

    public CharacterController() {
    }

    public Character getCharacter() {
        return myCharacter;
    }

    public void createAndSendMyCharacter(int type,int index) {
    	
        myChaPos = maze.getPosOfMyCha(type,index);
        
        if(type == Character.PAC){
        	myCharacter = new Pacman(myChaPos.x, myChaPos.y, RIGHT,index);
        }else if(type == Character.GHOST){
        	myCharacter = new Ghost(myChaPos.x, myChaPos.y, RIGHT,index);
        }

        manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(this);
        
        posTimer = new Timer(10, new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {

                if(!imgTimer.isRunning())
                    imgTimer.start();

            	if(myCharacter!= null) {
                    ArrayList<String> cellValues = getCharactersInCell(myChaPos.x,myChaPos.y);
                    cellValues.add(maze.getContentsInCell(myChaPos.x,myChaPos.y));
                    if(!maze.winner)
                    	handleEat(cellValues);

                    switch (myCharacter.direction) {
                        case LEFT:
                            if (!maze.amIStuck(myChaPos.x, myChaPos.y, LEFT))
                                myChaPos.x -= 2;
                            break;
                        case RIGHT:
                            if (!maze.amIStuck(myChaPos.x, myChaPos.y, RIGHT))
                                myChaPos.x += 2;
                            break;
                        case UP:
                            if (!maze.amIStuck(myChaPos.x, myChaPos.y, UP))
                                myChaPos.y -= 2;
                            break;
                        case DOWN:
                            if (!maze.amIStuck(myChaPos.x, myChaPos.y, DOWN))
                                myChaPos.y += 2;
                            break;
                    }

                    myCharacter.y = myChaPos.y;
                    myCharacter.x = myChaPos.x;
                }
                for (String k : World.otherCharacters.keySet()) {
                    Character oc = World.otherCharacters.get(k);

                    switch (World.otherCharacters.get(k).direction) {
                        case LEFT:
                            if (!maze.amIStuck((int) oc.x, (int) oc.y, LEFT))
                                oc.x -= 2;
                            break;
                        case RIGHT:
                            if (!maze.amIStuck((int) oc.x, (int) oc.y, RIGHT))
                                oc.x += 2;
                            break;
                        case UP:
                            if (!maze.amIStuck((int) oc.x, (int) oc.y, UP))
                                oc.y -= 2;
                            break;
                        case DOWN:
                            if (!maze.amIStuck((int) oc.x, (int) oc.y, DOWN))
                                oc.y += 2;
                            break;
                    }

                    World.otherCharacters.put(k, oc);

                }
            }
        });

        imgTimer = new Timer(200, new ActionListener() {
        	public synchronized void actionPerformed(ActionEvent e) {
                if(myCharacter != null)
        		    myCharacter = updateImage(myCharacter);

        		for (String s : World.otherCharacters.keySet()) {


        			    Character oc = World.otherCharacters.get(s);
        			    oc = updateImage(oc);}

        	}
        });
        
        sendData(INITCHA);
        //sendInitCharacter();
    }

    public void startTimer() {
        posTimer.start();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {

        int keyCode = e.getKeyCode();
        boolean directionChanged = false;

        if (e.getID() == KeyEvent.KEY_PRESSED)
            switch (keyCode) {
                case KeyEvent.VK_UP:
                    if (maze.canIMove(myChaPos.x, myChaPos.y, UP) && (myCharacter.direction != UP)) {
                        myChaPos = getNewPosOnTurning(UP);
                        directionChanged = true;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (maze.canIMove(myChaPos.x, myChaPos.y, DOWN) && (myCharacter.direction != DOWN)) {
                        myChaPos = getNewPosOnTurning(DOWN);
                        directionChanged = true;
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if (maze.canIMove(myChaPos.x, myChaPos.y, LEFT) && (myCharacter.direction != LEFT)) {
                        myChaPos = getNewPosOnTurning(LEFT);
                        directionChanged = true;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (maze.canIMove(myChaPos.x, myChaPos.y, RIGHT) && (myCharacter.direction != RIGHT)) {
                        myChaPos = getNewPosOnTurning(RIGHT);
                        directionChanged = true;
                    }
                    break;
            }

        if (directionChanged) {
            myCharacter.y = myChaPos.y;
            myCharacter.x = myChaPos.x;
            sendData(CHA);
            myCharacter = updateImage(myCharacter);
        }   
        
        return false;
    }

    private Point getNewPosOnTurning(int direction) {
        Point cellCenter = maze.getCellCenter(myChaPos);
        Point p;
        switch (direction) {
            case UP:
                myCharacter.direction = UP;
                cellCenter.y = cellCenter.y - maze.getCellDim().height / 2;
                p = new Point(cellCenter.x - maze.getPacmanRadius(), cellCenter.y - maze.getPacmanRadius());
                break;
            case DOWN:
                myCharacter.direction = DOWN;
                cellCenter.y = cellCenter.y + maze.getCellDim().height / 2;
                p = new Point(cellCenter.x - maze.getPacmanRadius(), cellCenter.y - maze.getPacmanRadius());
                break;
            case LEFT:
                myCharacter.direction = LEFT;
                cellCenter.x = cellCenter.x - maze.getCellDim().width / 2;
                p = new Point(cellCenter.x - maze.getPacmanRadius(), cellCenter.y - maze.getPacmanRadius());
                break;
            case RIGHT:
                myCharacter.direction = RIGHT;
                cellCenter.x = cellCenter.x + maze.getCellDim().width / 2;
                p = new Point(cellCenter.x - maze.getPacmanRadius(), cellCenter.y - maze.getPacmanRadius());
                break;
            default:
                p = new Point();
                break;
        }
        return p;
    }


    public void setMaze(Maze mz) {
        this.maze = mz;
    }

    public void setMeAsLooser(){
    	manager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this);
    	//posTimer.stop();
    	myCharacter = null;
    }
    
    public ArrayList<String> getCharactersInCell(int chaPosX,int chaPosY){
    	int x = chaPosX + Character.W/2;
    	int y = chaPosY + Character.H/2;

    	Point myCell = new Point(maze.calcCellColumn(x),maze.calcCellRow(y));
    	ArrayList<String> chaInCell= new ArrayList<String>();

    	for(String k: World.otherCharacters.keySet()){
    		Character oc = World.otherCharacters.get(k);
    		x = (int)oc.x + Character.W/2;
    		y = (int)oc.y + Character.H/2;
    		Point oChaCell = new Point(maze.calcCellColumn(x),maze.calcCellRow(y));
    		if(oChaCell.equals(myCell)){
    			if (oc instanceof Ghost){
    				chaInCell.add("Ghost"+"-"+k);
    			}else{
    				chaInCell.add("Pacman"+"-"+k);
    			}
    			
    		}
    	}

    	return chaInCell;
    }

    public void handleEat(ArrayList<String> cellValues){

    	ArrayList<Boolean> results = myCharacter.eat(cellValues,World.reverse);

    	Iterator<Boolean> i = results.iterator();
    	Iterator<String> j = cellValues.iterator();
    	String eated = "";
    	
    	while(i.hasNext())
    	{
    		String s = j.next();
			String[] data = s.split("-");
    		String character = data[0];
    		String characterID = null;
    		
    		if (i.next()) {
    			switch(data[0]){
    			case "DOT":
    				maze.setCellBlank(myChaPos.x,myChaPos.y);
    				sendData(MAZE);
    				break;
    			case "POWERDOT":
    				maze.setCellBlank(myChaPos.x,myChaPos.y);
                    sendData(MAZE);
    			    if(Node.getNode().checkLeader()){
                        for(int b=0; b< maze.powerdotsPos.size();b++){
                            if(maze.iMaze[(int)maze.powerdotsPos.get(b).getX()][(int)maze.powerdotsPos.get(b).getY()].equals("BLANK")) {
                                maze.powerdotsPos.remove(b);
                            }}
                    sendData(REVERSE);
                    Node.getNode().startReverseTimer();
                    World.reverse=true;
                    }
    			    break;
    			
    			case "Pacman":
    				characterID = data[1];
    				Node.getNode().attack(characterID);
    				break;
    				
    			case "Ghost":
    				characterID = data[1];
    				//System.out.println("I must eat : " + character + "With ip:" + characterID);
                    Node.getNode().attack(characterID);
    				break;
    			}
    		}
    	} 





    }
        
    private Character updateImage(Character c){
    	AffineTransform tx = new AffineTransform();
    	AffineTransform txFlip = new AffineTransform();
    	AffineTransformOp op = null;
    	int alpha = 0;
    	
    	if(c instanceof Pacman){
    		if (c.openMouth) {
    			c.openMouth();
    		}else{c.closeMouth();}

    		switch (c.direction){
    		case LEFT:
    			txFlip = AffineTransform.getScaleInstance(-1, 1);
    			txFlip.translate(-c.img.getWidth(null), 0);	
    			break;
    		case RIGHT:
    			break;
    		case UP:
    			alpha = -90;
    			break;
    		case DOWN:  			
    			alpha = 90;
    			break;
    		}
    		tx.rotate(Math.toRadians(alpha), c.img.getWidth() / 2.0, c.img.getHeight() / 2.0);
    		tx.concatenate(txFlip);
    		op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
    		c.img = op.filter(c.img, null);

    		c.openMouth = !c.openMouth ; 
    	}else if(c instanceof Ghost){
    		if(World.reverse)
    			c.setWeakGhostImg();
    		else c.setGhostImage();
    	}
    	return c;
    }
    
    public void sendData(int item){
    	String s;
    	switch(item){
    	case INITCHA:
    		s=takeInitCharacter();
    		break;
    	case CHA:
    		s=takeCharacter();
    		break;
    	case MAZE:
    		s=takeIMaze();
    		break;
    	case REVERSE:
    		s=takePowerDots();
    		break;
    	default :
    		s="";
    	}
    	
    	Node.getNode().broadcastData(item , s);
    }
    
    private String takeInitCharacter() {
    	int type;
    	
    	if (myCharacter instanceof Pacman ){type = Character.PAC;}
    	else{type = Character.GHOST;}
        String c= myCharacter.direction + " " + myCharacter.x + " " + myCharacter.y + " " + type + " " + myCharacter.index;
        return c ;
    }
    
    private String takeCharacter() {
    	String c = myCharacter.direction + " " + myCharacter.x + " " + myCharacter.y;
    	return c;
    }
    
    private String takePowerDots(){
    	String stringPowerDots = "";
    	for(int i=0; i< maze.powerdotsPos.size();i++){
    		stringPowerDots = stringPowerDots.concat((int)maze.powerdotsPos.get(i).getX()+","+(int)maze.powerdotsPos.get(i).getY());
    		stringPowerDots = stringPowerDots.concat(";");
    	}
    	return stringPowerDots;
    }
    
    private String takeIMaze() {
        String[][] structMaze = maze.getIMaze();
        String stringMaze = "";

        for (int i = 0; i < structMaze.length; i++) {
            for (int j = 0; j < structMaze[0].length; j++) {
                stringMaze = stringMaze.concat(structMaze[i][j]);
                stringMaze = stringMaze.concat(",");
            }
            stringMaze = stringMaze.concat(";");
        }

        return stringMaze;
    }
    
}
