/*
 * KeyHelpDialog.java
 *
 * Created on 2004年12月22日, 上午10:28
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author  Administrator
 */
public class KeyHelpDialog extends JDialog implements ActionListener {
    
    /** Creates a new instance of KeyHelpDialog */
    public KeyHelpDialog(Frame owner) {	// 生成按键帮助窗口
        super(owner, "key help", false);	// 调用父类构造函数
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        
        JTabbedPane tabpane = new JTabbedPane();
        
        JPanel onekey = new JPanel();
        onekey.setLayout(new GridLayout(0, 1));
        onekey.add(new JLabel("Left             left arrow"));
        onekey.add(new JLabel("Right           right arrow"));
        onekey.add(new JLabel("Down          down arrow"));
        onekey.add(new JLabel("Rotate         up arrow"));
        tabpane.add(onekey, "one player");
        
        JPanel twokey = new JPanel();
        twokey.setLayout(new GridLayout(0, 1));
        twokey.add(new JLabel("           Left Player     Right Player       "));
        twokey.add(new JLabel("Left        A                 left arrow"));
        twokey.add(new JLabel("Right      D                right arrow"));
        twokey.add(new JLabel("Down      S                down arrow"));
        twokey.add(new JLabel("Rotate    W                up arrow"));
        tabpane.add(twokey, "two players");
        
        content.add(tabpane, BorderLayout.PAGE_START);
        
        JButton okbutton = new JButton("Ok");
        okbutton.addActionListener(this);
        getRootPane().setDefaultButton(okbutton);
        content.add(okbutton, BorderLayout.CENTER);
        
        setContentPane(content);
        
        pack();
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e){	// 如果点击ok按钮则关闭窗口
        dispose();
    }
}
