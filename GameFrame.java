/*
 * GameFrame.java
 *
 * Created on 2004��12��17��, ����3:35
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;

/**
 *
 * @author  Administrator
 */
public class GameFrame extends JFrame implements ActionListener, GameEndAction{ 
    PlayerList playerList;
    JMenuItem startMenu, pauseMenu, stopMenu;
    JButton startButton, pauseButton, stopButton;
    JPanel toolBar;
    String[] onePlayerNames = {"Unnamed Player"};
    String[] twoPlayerNames = {"Left Player", "Right Player"};
    int[][] onePlayerKeyMap = {{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_UP}};	// ���˼��̿���
    int[][] twoPlayerKeyMap = {{KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_S, KeyEvent.VK_W},
            {KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_UP}};											// ˫�˼��̿���
    int[] delays = {1000, 900, 800, 700, 650, 600, 550, 500, 450, 400};			// �����½�ʱ��
    
    /** Creates a new instance of GameFrame */
		public GameFrame() {			// ������Ϸ������
        super("Russian Box");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initMenu();						// ���ɲ˵�
        initToolBar();				// ���ɹ�����
        RussianBox.initImg();

        playerList = new PlayerList(onePlayerNames, onePlayerKeyMap);		// ������Ϸ������
        playerList.setGameAction(this);

        JPanel pane = new JPanel();			// ������壬����彫�������ù���������Ϸ������
        pane.setOpaque(true);					
        GridBagLayout gridbag = new GridBagLayout();				// ���ɲ��ֶ���ͨ���ö�����ƽ���Ԫ�ص�λ��
        GridBagConstraints c = new GridBagConstraints();
        pane.setLayout(gridbag);
        
        c.gridwidth = GridBagConstraints.REMAINDER;		// Ϊ����������λ��
        c.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(toolBar, c);
        pane.add(toolBar);						// ������м��빤����
        
        c.gridwidth = GridBagConstraints.REMAINDER;
            
        gridbag.setConstraints(playerList, c);	// Ϊ��Ϸ����������λ��
        pane.add(playerList);					// ������з�����Ϸ������
        
        setContentPane(pane);					// ���������Ϊ�������ݴ��ڣ�Content Pane��

        setResizable(false);
        pack();
        setVisible(true);
    }
    
    // initialize menus
    public void initMenu(){
        JMenuBar menuBar = new JMenuBar();	// ���ɲ˵������Է��ò˵�
        
        JMenu gameMenu = new JMenu("Game");		// ����Game�˵�
        gameMenu.setMnemonic(KeyEvent.VK_G);
        
        startMenu = new JMenuItem("start");		// ����start�˵���
        startMenu.setActionCommand("start");
        startMenu.setMnemonic(KeyEvent.VK_S);
        startMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));	// ����start�Ŀ�ݼ�
        startMenu.addActionListener(this);
        startMenu.setEnabled(true);
        gameMenu.add(startMenu);			// ��start�˵������Game�˵�
        
        pauseMenu = new JMenuItem("pause");		// ����pause�˵�
        pauseMenu.setMnemonic(KeyEvent.VK_P);
        pauseMenu.setActionCommand("pause");
        pauseMenu.addActionListener(this);
        pauseMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
        pauseMenu.setEnabled(false);
        gameMenu.add(pauseMenu);
        
        stopMenu = new JMenuItem("stop");		// ����stop�˵�
        stopMenu.setActionCommand("stop");
        stopMenu.setMnemonic(KeyEvent.VK_T);
        stopMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK));
        stopMenu.addActionListener(this);
        stopMenu.setEnabled(false);
        gameMenu.add(stopMenu);
        
        gameMenu.add(new JSeparator());
        
        JMenuItem exitMenu = new JMenuItem("exit");
        exitMenu.setActionCommand("exit");
        exitMenu.setMnemonic(KeyEvent.VK_X);
        exitMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        exitMenu.addActionListener(this);
        gameMenu.add(exitMenu);
        
        menuBar.add(gameMenu);
        
        JMenu optionMenu = new JMenu("Options");	// ѡ��˵�
        optionMenu.setMnemonic(KeyEvent.VK_O);
        menuBar.add(optionMenu);
        
        JMenu previewMenu = new JMenu("Preview");	// Ԥ���˵�
        previewMenu.setMnemonic(KeyEvent.VK_R);
        optionMenu.add(previewMenu);
        
        ButtonGroup previewGroup = new ButtonGroup();
        
        JRadioButtonMenuItem showPreviewMenu = new JRadioButtonMenuItem("show");	// ��ʾԤ��
        showPreviewMenu.setActionCommand("show");
        showPreviewMenu.setMnemonic(KeyEvent.VK_S);
        showPreviewMenu.setSelected(true);
        showPreviewMenu.addActionListener(this);
        previewGroup.add(showPreviewMenu);
        previewMenu.add(showPreviewMenu);
        
        JRadioButtonMenuItem hidePreviewMenu = new JRadioButtonMenuItem("hide");	// ����Ԥ��
        hidePreviewMenu.setActionCommand("hide");
        hidePreviewMenu.setMnemonic(KeyEvent.VK_H);
        hidePreviewMenu.setSelected(false);
        hidePreviewMenu.addActionListener(this);
        previewGroup.add(hidePreviewMenu);
        previewMenu.add(hidePreviewMenu);
        
        JMenu gameLevelMenu = new JMenu("Game level");	// ��Ϸ�ȼ�
        gameLevelMenu.setMnemonic(KeyEvent.VK_G);
                
        ButtonGroup levelGroup = new ButtonGroup();
        
        JRadioButtonMenuItem easyLevelMenu = new JRadioButtonMenuItem("easy");	// ����
        easyLevelMenu.setActionCommand("easy");
        easyLevelMenu.setSelected(true);
        easyLevelMenu.addActionListener(this);
        levelGroup.add(easyLevelMenu);
        gameLevelMenu.add(easyLevelMenu);
        
        JRadioButtonMenuItem middleLevelMenu = new JRadioButtonMenuItem("middle");	// �е�
        middleLevelMenu.setActionCommand("middle");
        middleLevelMenu.setSelected(false);
        middleLevelMenu.addActionListener(this);
        levelGroup.add(middleLevelMenu);
        gameLevelMenu.add(middleLevelMenu);
        
        JRadioButtonMenuItem hardLevelMenu = new JRadioButtonMenuItem("hard");	// ����
        hardLevelMenu.setActionCommand("hard");
        hardLevelMenu.setSelected(false);
        hardLevelMenu.addActionListener(this);
        levelGroup.add(hardLevelMenu);
        gameLevelMenu.add(hardLevelMenu);
        
        optionMenu.add(gameLevelMenu);
        
        JMenu speedMenu = new JMenu("Speed");		// ��Ϸ�ٶ�
        speedMenu.setMnemonic(KeyEvent.VK_S);
        
        ButtonGroup speedGroup = new ButtonGroup();
        
        JRadioButtonMenuItem speed;
        for(int i = 0; i < 10; i++){		// ���ʮ���ٶȲ˵���
            String s = "Speed " + (i + 1);
            speed = new JRadioButtonMenuItem(s);
            speed.setActionCommand(s);
            speed.addActionListener(this);
            speed.setSelected(i == 0);
            speedGroup.add(speed);
            speedMenu.add(speed);
        }
        
        optionMenu.add(speedMenu);
        
        JMenu playerCountMenu = new JMenu("Player count");	// �����Ҹ����˵�
        playerCountMenu.setMnemonic(KeyEvent.VK_P);
        
        ButtonGroup playerCountGroup = new ButtonGroup();
        
        JRadioButtonMenuItem onePlayerMenu = new JRadioButtonMenuItem("One player");	// ��ӡ�һ����ҡ��˵���
        onePlayerMenu.setMnemonic(KeyEvent.VK_O);
        onePlayerMenu.addActionListener(this);
        onePlayerMenu.setActionCommand("one");
        onePlayerMenu.setSelected(true);
        playerCountGroup.add(onePlayerMenu);
        playerCountMenu.add(onePlayerMenu);
        
        JRadioButtonMenuItem twoPlayerMenu = new JRadioButtonMenuItem("Two player");	// ��ӡ�������ҡ��˵���
        twoPlayerMenu.setMnemonic(KeyEvent.VK_T);
        twoPlayerMenu.addActionListener(this);
        twoPlayerMenu.setActionCommand("two");
        twoPlayerMenu.setSelected(false);
        playerCountGroup.add(twoPlayerMenu);
        playerCountMenu.add(twoPlayerMenu);
        
        optionMenu.add(playerCountMenu);
        
        JMenu helpMenu = new JMenu("Help");		// ��Ӱ����˵�
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpMenu);
        
        JMenuItem keyHelpMenu = new JMenuItem("keys");	// ��Ӽ��̰����˵�
        keyHelpMenu.setMnemonic(KeyEvent.VK_K);
        keyHelpMenu.setActionCommand("keys");
        keyHelpMenu.addActionListener(this);
        helpMenu.add(keyHelpMenu);
        
        JMenuItem aboutMenu = new JMenuItem("about");		// ��ӡ��йء��˵�
        aboutMenu.setMnemonic(KeyEvent.VK_A);
        aboutMenu.setActionCommand("about");
        aboutMenu.addActionListener(this);
        helpMenu.add(aboutMenu);
        
        setJMenuBar(menuBar);
    }
    
    // initialize toolbar
    public void initToolBar(){	// ���ɹ�����
        toolBar = new JPanel();	// �������
        toolBar.setLayout(new FlowLayout());	// �������Ĳ��ַ�ʽ
        
        startButton = new JButton(createImageIcon("play.gif"));		// ����play��ť
        startButton.setToolTipText("start a new game");		// ����play��ť�İ�����Ϣ
        startButton.setActionCommand("startButton");
        startButton.setEnabled(true);
        startButton.addActionListener(this);
        toolBar.add(startButton);
        
        pauseButton = new JButton(createImageIcon("pause.gif"));	// ����pause��ť
        pauseButton.setToolTipText("pause the current game");
        pauseButton.setActionCommand("pauseButton");
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(this);
        toolBar.add(pauseButton);
        
        stopButton = new JButton(createImageIcon("stop.gif"));	// ����stop��ť
        stopButton.setToolTipText("stop the current game");
        stopButton.setActionCommand("stopButton");
        stopButton.addActionListener(this);
        stopButton.setEnabled(false);
        toolBar.add(stopButton);
    }
    
    // process menu commands
    public void actionPerformed(ActionEvent e){	// �����û��Ĳ˵�����Ͱ�ť����
        String cmd = e.getActionCommand();
        if("start".equals(cmd)){		// start�˵�
            startGame();
            
        }else if("pause".equals(cmd)){	// pause�˵�
            int gameState = playerList.getGameState();
            if(gameState == playerList.GAME_PAUSE){	// �����ǰ��Ϸ����ͣ���������Ϸ
                resumeGame();
            }else if(gameState == playerList.GAME_START){	// �����ǰ��Ϸ�ѿ�ʼ������ͣ��Ϸ
                pauseGame();
            }
            
        }else if("stop".equals(cmd)){	// stop�˵�
            stopGame();
            
        }else if("startButton".equals(cmd)){	// play��ť
            int gameState = playerList.getGameState();
            if(gameState == playerList.GAME_END){	// �����ǰ��Ϸ��ֹͣ����ʼ��Ϸ
                startGame();
            }else if(gameState == playerList.GAME_PAUSE){	// �����ǰ��Ϸ����ͣ���������Ϸ
                resumeGame();
            }
            
        }else if("pauseButton".equals(cmd)){	// pause��ť
            pauseGame();
            
        }else if("stopButton".equals(cmd)){	// stop��ť
            stopGame();
            
        }else if("easy".equals(cmd)){	// easy�˵�
            RussianBox.setMaxBoxType(RussianBox.EASY_LEVEL);	// ������Ϸ�ȼ�Ϊ����
            
        }else if("middle".equals(cmd)){	// middle�˵�
            RussianBox.setMaxBoxType(RussianBox.MID_LEVEL);	// ������Ϸ�ȼ�Ϊ�е�
            
        }else if("hard".equals(cmd)){	// hard�˵�
            RussianBox.setMaxBoxType(RussianBox.HARD_LEVEL);	// ������Ϸ�ȼ�Ϊ����
        
        }else if(cmd.indexOf("Speed") != -1){	// ���÷����½����ٶ�
            int idx = Integer.parseInt(cmd.substring(6)) - 1;
            playerList.getTimer().setDelay(delays[idx]);	// ���÷����½���ʱ�����ӳ�
            
        }else if("one".equals(cmd)){	// one�˵�
            if(playerList.getGameState() != PlayerList.GAME_END){	// �����ǰ��Ϸ��Ϊ��ֹ��������ֹͣ��Ϸ
                stopGame();
            }
            playerList.setPlayers(onePlayerNames, onePlayerKeyMap);	// ����Ϸ����������Ϊ��һ��ҽ���
            pack();
        
        }else if("two".equals(cmd)){	// two�˵�
            if(playerList.getGameState() != PlayerList.GAME_END){	// �����ǰ��Ϸ��Ϊ��ֹ��������ֹͣ��Ϸ
                stopGame();
            }
            playerList.setPlayers(twoPlayerNames, twoPlayerKeyMap);	// ����Ϸ����������Ϊ����ҽ���
            pack();
        
        }else if("show".equals(cmd)){	// show�˵�
            playerList.setPreviewVisible(true);	// ��ʾԤ��
            
        }else if("hide".equals(cmd)){	// hide�˵�
            playerList.setPreviewVisible(false);	// ����Ԥ��
            
        }else if("keys".equals(cmd)){	// ���̰�����Ϣ
            KeyHelpDialog d = new KeyHelpDialog(this);	// ��ʾ���̰�������
            
        }else if("about".equals(cmd)){	// ���ڲ˵�
            AboutDialog d = new AboutDialog(this);	// ��ʾ���ڴ���
        }
    }
    
    // start new game
    protected void startGame(){	// ��ʼһ����Ϸ
        playerList.startGame();	// ��ʼ��Ϸ
        pauseMenu.setEnabled(true); // ʹ��pause�˵�
        stopMenu.setEnabled(true);
        pauseButton.setEnabled(true);	// ʹ��pause��ť
        stopButton.setEnabled(true);
        startButton.setEnabled(false);
        playerList.requestFocus();	// �����뽹������Ϊ��Ϸ������
    }
    
    // pause game
    protected void pauseGame(){	// ��ͣ��Ϸ
        playerList.pauseGame();	// ��ͣ��Ϸ
        pauseMenu.setText("resume");	// ��pause�˵���Ϊresume�˵�
        pauseButton.setEnabled(false);
        startButton.setEnabled(true);
        startButton.setToolTipText("resume the current game");	// �޸�start��ť�İ�����Ϣ
    }
    
    // resume game
    protected void resumeGame(){	// ������Ϸ
        playerList.resumeGame();	// ������Ϸ
        pauseMenu.setText("pause");	// ��resume�˵���Ϊpause�˵�
        pauseButton.setEnabled(true);
        startButton.setToolTipText("pause the current game");
        startButton.setEnabled(false);
        playerList.requestFocus();	// �����뽹������Ϊ��Ϸ������
    }
    
	    // stop game
	    protected void stopGame(){	// ֹͣ��Ϸ
        playerList.endGame();
        pauseMenu.setEnabled(false);
        stopMenu.setEnabled(false);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        startButton.setEnabled(true);
        startButton.setToolTipText("start a new game");
    }
    
    // action performed when game is end
    public void gameEnd(){	// ����Ϸ��ֹʱִ��
        pauseMenu.setText("pause");
        pauseMenu.setEnabled(false);
        stopMenu.setEnabled(false);
        startButton.setEnabled(true);
        startButton.setToolTipText("start a new game");
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        if(playerList.getPlayerCount() > 1){	// �����������1��ʱ�����ݱ��������ʾ��Ϣ
            String won = null;
            Player leftPlayer = playerList.getPlayerByIndex(0);
            Player rightPlayer = playerList.getPlayerByIndex(1);
            if(leftPlayer.getScore() != rightPlayer.getScore()){
                won = (leftPlayer.getScore() > rightPlayer.getScore()) ? 
                                leftPlayer.getPlayerName() : rightPlayer.getPlayerName();	// �����϶��Ϊ��ʤ��
                JOptionPane.showMessageDialog(this, won + " has won the game!!!",
                    "game over", JOptionPane.INFORMATION_MESSAGE, null);	// ��ʾ��ʤ��Ϣ
            }else{	// ��������˵ķ���һ���࣬����ʾƽ����Ϣ
                JOptionPane.showMessageDialog(this, "Both of you are good!!!",
                    "game over", JOptionPane.INFORMATION_MESSAGE, null);
            }
        }
    }
    
    // load image
    public static ImageIcon createImageIcon(String url){	// ����ͼƬ��������ͼ�����
        URL imageURL = GameFrame.class.getResource(url);
        ImageIcon icon = null;
        if(imageURL != null){
            icon = new ImageIcon(imageURL);
        }else{
            System.err.println("cannot load image " + url);
        }
        return icon;
    }
    
    // create ui
    public static void createMainUI(){	// ���ɳ���������
        GameFrame frame = new GameFrame();
    }
    
    // main entry point
    public static void main(String[] args){	// ������ڵ�
        SwingUtilities.invokeLater(new Runnable(){	// ����һ����Ϣ�����̣߳����߳��������պʹ����û������롢���
           public void run() {
               createMainUI();
           }
        });
    }
}
