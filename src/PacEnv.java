import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Random;

public abstract class PacEnv extends JFrame {

    private World world;

    public void createGui() {
        addElementsToGui(this.getContentPane());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

    private void addElementsToGui(Container pane) {
        final JPanel header = new JPanel(new FlowLayout());
        final JPanel body = new JPanel();
        final JLabel label = new JLabel("Server: ");
        final JTextField textField = new JTextField();
        final JButton start = new JButton("Start Game");

        start.addActionListener(new ActionListener() { //moved from actionlogin()
            public void actionPerformed(ActionEvent ae) {
                registerMyself(textField.getText());
            }
        });

        textField.setColumns(20);
        header.add(label);
        header.add(textField);
        header.add(start);
        pane.add(header, BorderLayout.PAGE_START);
        world = new World();
        pane.add(world, BorderLayout.CENTER);
        this.setVisible(true);
    }

    public abstract void registerMyself(String server);

    //ggdsgs
    public World getWorld() {
        return world;
    }

    public int showCharacterDialog(int[] charactersToken) {
    	Object[] possibilities = new Object[2];
    	if ((charactersToken[Character.PAC] > 0) && (charactersToken[Character.GHOST] > 0)){
    		possibilities[0] = "Pac Man";
    		possibilities[1] = "Ghost";
    	}else{if(charactersToken[Character.PAC] > 0){
    		possibilities[0] = "Pac Man";
    	}else{possibilities[0] = "Ghost";}
    	}
    	
        String s = (String) JOptionPane.showInputDialog(
                this,
                "Note: if you quit a random character will be chosen\n"
                        + "\n"
                        + "I want to play as:\n",
                "Choose your hero...",
                JOptionPane.PLAIN_MESSAGE,
                null,//icon
                possibilities,
                "Pac Man");

        if ((s != null) && (s.length() > 0)) {
            if (s.equals("Pac Man"))
                return Character.PAC;
            if (s.equals("Ghost"))
                return Character.GHOST;
        }

        Random random = new Random();
        int k = random.nextInt(1);
        JOptionPane.showMessageDialog(this, "Your character is... " + possibilities[k]);
        return k;
    }
    
    public void showGameOver(){
    	
    }
    
}