import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;


public class Pacman extends Character{
	
	public Pacman(int x, int y, int d, int i){
		super(x,y,d,i);
		openMouth();
	}

	public void openMouth(){
		BufferedImage myPicture = null;
		try 
		{
			myPicture = ImageIO.read(new File("../images/Pacman-"+index%3+".png"));
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		img = myPicture;
	}
	
	public void closeMouth(){
		BufferedImage myPicture = null;
		try 
		{
			myPicture = ImageIO.read(new File("../images/Pacman-"+index%3+"-close.png"));
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		img = myPicture;
	}
	
	//Rewrite the method
	public ArrayList<Boolean> eat(ArrayList<String> values,boolean reverse){
		ArrayList<Boolean> result = new ArrayList<Boolean>();
		
		Iterator<String> it = values.iterator();
		while(it.hasNext())
		{
		    String s = it.next();
		    String[] data = s.split("-");
		    
		    switch(data[0]){
		    	case "DOT":
		    		result.add(true);
		    		break;
		    	case "POWERDOT":
		    		result.add(true);
		    		break;
		    	case "Ghost":
		    		result.add(reverse);
		    		break;
		    	default:
		    		result.add(false);
		    		break;
		    }   
		}		
		return result;
	}
}
