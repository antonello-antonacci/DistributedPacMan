import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;


public class Character extends Ellipse2D.Double implements Serializable {
    public int direction;
    public int index;
    public static final int W = 26;
    public static final int H = 26;
    public static final int PAC = 0;
    public static final int GHOST = 1;
    
    public BufferedImage img;
    public boolean openMouth = true;

    public Character(int x, int y, int d, int index) {
        super(x, y, W, H);
        this.index = index;
        direction = d;
    }
    
    public ArrayList<Boolean> eat(ArrayList<String> values, boolean reverse){
        return null;
    }
    
    public void setGhostImage(){}
    
    public void setWeakGhostImg(){}
    
    public void openMouth(){}
    
    public void closeMouth(){}
    
}
