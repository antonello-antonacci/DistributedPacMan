import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.ImageIO;


public class Ghost extends Character{
	
	public Ghost(int x, int y, int d, int i){
		super(x,y,d,i);
		setGhostImage();
	}
	
	
	public void setGhostImage(){
		BufferedImage myPicture = null;
		try 
		{
			myPicture = ImageIO.read(new File("../images/Ghost-"+index%3+".png"));
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		img = myPicture;
	}
	
	
	public void setWeakGhostImg(){
		BufferedImage myPicture = null;
		try 
		{
			myPicture = ImageIO.read(new File("../images/WeakGhost.png"));
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
		    	case "Pacman":
		    		result.add(!reverse);
		    		break;
		    	default:
		    		result.add(false);
		    		break;
		    }   
		}
		return result;
	}
	
}
