/*
 * GameBox.java
 *
 * Created on 2004年12月24日, 上午11:22
 */

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 *
 * @author  Administrator
 */
public class GameBox extends JApplet{
    JButton russianBox;
    
  	public void init(){
        try{
            SwingUtilities.invokeAndWait(new Runnable(){
               public void run() {
                    JPanel panel = new JPanel();
                    panel.setLayout(new FlowLayout());
                    russianBox = new JButton("Russian Box");
                    russianBox.setActionCommand("russian");
                    russianBox.addActionListener(new ActionListener(){
                       public void actionPerformed(ActionEvent e) {
                            GameFrame frame = new GameFrame();               
                       }
                    });
                    panel.add(russianBox);
                    setContentPane(panel);               
               }
            });
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
    
    public void start(){
        
    }
}
