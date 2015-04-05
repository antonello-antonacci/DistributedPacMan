import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;


public class Maze {

    private Dimension mazeDim;
    private Dimension cellDim;
    private Dimension iMazeDim = new Dimension(21, 16);
    public boolean GameOverGhost = false;

    public String[][] iMaze;
    
    private ArrayList<Point> pacInitPos = new ArrayList<Point>();
    private ArrayList<Point> ghostInitPos = new ArrayList<Point>();
    public ArrayList<Point> powerdotsPos = new ArrayList<Point>();

    private final int UP = 0;
    private final int DOWN = 1;
    private final int LEFT = 2;
    private final int RIGHT = 3;

    public static boolean winner= false;
    // the maze difinition string
    public static String[] MazeDefine =
            {
                    "XXXXXXXXXXXXXXXXXXXXX",    // 1
                    "X.........X.........X",    // 2
                    "XOXXX.XXX.X.XXX.XXXOX",    // 3
                    "X......X..X.........X",    // 4
                    "XXX.XX.X.XXX.XX.X.X.X",    // 5
                    "X....X..........X.X.X",    // 6
                    "X.XX.X.XXX-XXX.XX.X.X",    // 7
                    "X.XX.X.XG   GX......X",    // 8
                    "X.XX...XG   GX.XXXX.X",    // 9
                    "X.XX.X.XXXXXXX.XXXX.X",    // 10
                    "X....X..PPPPP.......X",    // 11
                    "XXX.XX.XXXXXXX.X.X.XX",    // 12
                    "X.........X....X....X",    // 13
                    "XOXXXXXXX.X.XXXXXXXOX",    // 14
                    "X...................X",    // 15
                    "XXXXXXXXXXXXXXXXXXXXX",    // 16
            };


    public Dimension getPreferredSize() {
        return mazeDim;
    }

    public void setIMaze(String[][] maze) {
        this.iMaze = maze;
    }

    public Maze(int width, int height) {

        iMaze = new String[iMazeDim.width][iMazeDim.height];
        initIMaze();

        mazeDim = new Dimension(width, height);
        cellDim = new Dimension(width / iMazeDim.width, height / iMazeDim.height);
    }

    public Dimension getCellDim() {
        return cellDim;
    }
    
    
    public Point getPosOfMyCha(int type, int index) {
    	
    	switch(type){
		case Character.PAC :
			return chaPosInCell(pacInitPos.get(index).x,pacInitPos.get(index).y);
		case Character.GHOST :
			return chaPosInCell(ghostInitPos.get(index).x,ghostInitPos.get(index).y);
		default :
			return null;
	}
    	
    }

    public Point chaPosInCell(int cellX, int cellY) {
        int middleX = (cellDim.width - getChaDim()) / 2;
        int posX = cellX * cellDim.width + middleX;
        int middleY = (cellDim.height - getChaDim()) / 2;
        int posY = cellY * cellDim.height + middleY;
        return new Point(posX, posY);

    }

    public int getPacmanRadius() {
        return getChaDim() / 2;
    }

    public int getChaDim() {
        return 26;
    }

    private void initIMaze() {

        String k;

        for (int i = 0; i < iMazeDim.height; i++)
            for (int j = 0; j < iMazeDim.width; j++) {
                switch (MazeDefine[i].charAt(j)) {
                    case 'X':
                        k = "WALL";
                        //g.drawRoundRect(j*cellDim, i*cellDim, cellDim, cellDim,4,4);
                        break;
                    case '.':
                        k = "DOT";
                        break;
                    case 'O':
                        k = "POWERDOT";
                        powerdotsPos.add(new Point(j, i));
                        break;
                    case '-':
                        k = "DOOR";
                        break;
                    case 'P':
                    	k = "DOT";
                    	pacInitPos.add(new Point(j, i));
                        break;
                    case 'G':
                    	k = "BLANK";
                    	ghostInitPos.add(new Point(j, i));
                        break;
                    default:
                        k = "BLANK";
                        break;
                }
                iMaze[j][i] = k;
            }
    }

    public String[][] getIMaze() {
        return iMaze;
    }

    public boolean canIMove(int xi, int yi, int move) {
        int x = xi + Character.W / 2;
        int y = yi + Character.H / 2;

        int currentCol = x / cellDim.width;
        int currentRow = y / cellDim.height;
        int currentColOff = x % cellDim.width;
        int currentRowOff = y % cellDim.height;

        switch (move) {

            case RIGHT:
                if (!iMaze[currentCol + 1][currentRow].equals("WALL")) {
                    if (((currentRowOff) <= cellDim.height) && ((currentRowOff) >= 0)) {
                        return true;
                    }
                }
                return false;

            case DOWN:
                if (!iMaze[currentCol][currentRow + 1].equals("WALL")) {
                    if (((currentColOff) <= cellDim.width) && ((currentColOff) >= 0)) {
                        return true;
                    }
                }
                return false;

            case LEFT:
                if (!iMaze[currentCol - 1][currentRow].equals("WALL")) {
                    if (((currentRowOff) <= cellDim.height) && ((currentRowOff) >= 0)) {
                        return true;
                    }
                }
                return false;

            case UP:
                if (!iMaze[currentCol][currentRow - 1].equals("WALL")) {
                    if (((currentColOff) <= cellDim.width) && ((currentColOff) >= 0)) {
                        return true;
                    }
                }
                return false;

        }


        return false;
    }

    public boolean eatDot(int xi, int yi) {
        int x = xi + Character.W / 2;
        int y = yi + Character.H / 2;

        if (iMaze[calcCellColumn(x)][calcCellRow(y)].equals("DOT")) {
            iMaze[calcCellColumn(x)][calcCellRow(y)] = "BLANK";
            return true;
        } else {
            return false;
        }

    }

    public void updateCells(int i, int j) {
        iMaze[i][j] = "BLANK";
    }

    public boolean amIStuck(int xi, int yi, int move) {

        int x = xi + Character.W / 2;
        int y = yi + Character.H / 2;

        switch (move) {

            case RIGHT:
                if (iMaze[calcCellColumn(x + cellDim.width / 2)][calcCellRow(y)].equals("WALL"))
                    return true;
                break;
            case LEFT:
                if (iMaze[calcCellColumn(x - cellDim.width / 2)][calcCellRow(y)].equals("WALL"))
                    return true;
                break;
            case UP:
                if (iMaze[calcCellColumn(x)][calcCellRow(y - cellDim.height / 2)].equals("WALL"))
                    return true;
                break;
            case DOWN:
                if (iMaze[calcCellColumn(x)][calcCellRow(y + cellDim.height / 2)].equals("WALL"))
                    return true;
                break;
        }
        return false;
    }

    public int calcCellRow(int y) {
        return y / cellDim.height;
    }

    public int calcCellColumn(int x) {
        return x / cellDim.width;
    }


    public Point getCellCenter(Point p) {

    	int x = p.x + Character.W / 2;
    	int y = p.y + Character.H / 2;

    	int cellColumn = calcCellColumn(x);
    	int cellRow = calcCellRow(y);
    	int x1 = (cellColumn * cellDim.width) + cellDim.width / 2;
    	int y1 = (cellRow * cellDim.height) + cellDim.height / 2;

    	return new Point(x1, y1);
    }

    public String getContentsInCell(int chaPosX,int chaPosY){
    	int x = chaPosX + Character.W/2;
    	int y = chaPosY + Character.H/2;

    	return iMaze[calcCellColumn(x)][calcCellRow(y)];
    }

    public void setCellBlank(int xi,int yi){
    	int x = xi + Character.W/2;
    	int y = yi + Character.H/2;

    	iMaze[calcCellColumn(x)][calcCellRow(y)] = "BLANK";
    }
    
    
    public void setGameOver(String winner){
    	this.winner = true;
    	if(winner.equals("Pacman")){
    		String[] gameOverMaze =
    			{
    				"XXXXXXXXXXXXXXXXXXXXX",    
    				"X                   X",    
    				"X   XXX  XXX  XXX   X",    
    				"X   X X  X X  X     X",    
    				"X   XXX  XXX  X     X",    
    				"X   X    X X  X     X",    
    				"X   X    X X  XXX   X",    
    				"X                   X",    
    				"X         X         X",    
    				"X X      XXX      X X",    
    				"X XX    XXXXX    XX X",    
    				"X XXX  XXXXXXX  XXX X",    
    				"X XXXXXXXXXXXXXXXXX X",    
    				"X XXXXXXXXXXXXXXXXX X",    
    				"X                   X",    
    				"XXXXXXXXXXXXXXXXXXXXX",    
    			};
    		MazeDefine = gameOverMaze;
    	}else{   
    		String[] gameOverMaze =
    			{
    				"XXXXXXXXXXXXXXXXXXXXX",    
    				"X                   X",    
    				"X XXX X X XXX XX XXXX",    
    				"X X   X X X X X   X X",    
    				"X X X XXX X X XX  X X",    
    				"X X X X X X X  X  X X",    
    				"X XXX X X XXX XX  X X",    
    				"X                   X",    
    				"X         X         X",    
    				"X X      XXX      X X",    
    				"X XX    XXXXX    XX X",  
    				"X XXX  XXXXXXX  XXX X",  
    				"X XXXXXXXXXXXXXXXXX X",    
    				"X XXXXXXXXXXXXXXXXX X",    
    				"X                   X",    
    				"XXXXXXXXXXXXXXXXXXXXX",    
    			};
    		MazeDefine = gameOverMaze;
            GameOverGhost = true;
    	}

    	initIMaze();
    }


}
