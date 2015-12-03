/*
 * AboutDialog.java
 *
 * Created on 2004��12��24��, ����11:01
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author  Administrator
 */
public class AboutDialog extends JDialog implements ActionListener {
    
    /** Creates a new instance of AboutDialog */
    public AboutDialog(Frame owner) {		// ���ɹ��ڴ���
        super(owner, "about", false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        JPanel content = new JPanel();
        GridBagLayout grid = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        content.setLayout(grid);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        JLabel about = new JLabel("Russian Box By Meng Chun.");
        about.setPreferredSize(new Dimension(200, 100));
        about.setFont(about.getFont().deriveFont(Font.BOLD, 12));
        about.setHorizontalAlignment(SwingConstants.CENTER);
        about.setVerticalAlignment(SwingConstants.CENTER);
        grid.setConstraints(about, c);
        content.add(about);
        
        c.anchor = GridBagConstraints.CENTER;
        JButton ok = new JButton("OK");
        ok.addActionListener(this);
        grid.setConstraints(ok, c);
        content.add(ok);
        
        setContentPane(content);
        
        pack();
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e){	// �����okʱ�رմ���
        dispose();
    }
    
}
