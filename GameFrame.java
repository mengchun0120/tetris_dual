/*
 * GameFrame.java
 *
 * Created on 2004年12月17日, 下午3:35
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
    int[][] onePlayerKeyMap = {{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_UP}};	// 单人键盘控制
    int[][] twoPlayerKeyMap = {{KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_S, KeyEvent.VK_W},
            {KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_UP}};											// 双人键盘控制
    int[] delays = {1000, 900, 800, 700, 650, 600, 550, 500, 450, 400};			// 方块下降时间
    
    /** Creates a new instance of GameFrame */
		public GameFrame() {			// 生成游戏主窗口
        super("Russian Box");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initMenu();						// 生成菜单
        initToolBar();				// 生成工具栏
        RussianBox.initImg();

        playerList = new PlayerList(onePlayerNames, onePlayerKeyMap);		// 生成游戏主界面
        playerList.setGameAction(this);

        JPanel pane = new JPanel();			// 生成面板，该面板将用来放置工具栏和游戏主界面
        pane.setOpaque(true);					
        GridBagLayout gridbag = new GridBagLayout();				// 生成布局对象，通过该对象控制界面元素的位置
        GridBagConstraints c = new GridBagConstraints();
        pane.setLayout(gridbag);
        
        c.gridwidth = GridBagConstraints.REMAINDER;		// 为工具栏设置位置
        c.anchor = GridBagConstraints.WEST;
        gridbag.setConstraints(toolBar, c);
        pane.add(toolBar);						// 在面板中加入工具栏
        
        c.gridwidth = GridBagConstraints.REMAINDER;
            
        gridbag.setConstraints(playerList, c);	// 为游戏主界面设置位置
        pane.add(playerList);					// 在面板中放入游戏主界面
        
        setContentPane(pane);					// 将面板设置为程序内容窗口（Content Pane）

        setResizable(false);
        pack();
        setVisible(true);
    }
    
    // initialize menus
    public void initMenu(){
        JMenuBar menuBar = new JMenuBar();	// 生成菜单栏，以放置菜单
        
        JMenu gameMenu = new JMenu("Game");		// 生成Game菜单
        gameMenu.setMnemonic(KeyEvent.VK_G);
        
        startMenu = new JMenuItem("start");		// 生成start菜单项
        startMenu.setActionCommand("start");
        startMenu.setMnemonic(KeyEvent.VK_S);
        startMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));	// 设置start的快捷键
        startMenu.addActionListener(this);
        startMenu.setEnabled(true);
        gameMenu.add(startMenu);			// 将start菜单项加入Game菜单
        
        pauseMenu = new JMenuItem("pause");		// 生成pause菜单
        pauseMenu.setMnemonic(KeyEvent.VK_P);
        pauseMenu.setActionCommand("pause");
        pauseMenu.addActionListener(this);
        pauseMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
        pauseMenu.setEnabled(false);
        gameMenu.add(pauseMenu);
        
        stopMenu = new JMenuItem("stop");		// 生成stop菜单
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
        
        JMenu optionMenu = new JMenu("Options");	// 选项菜单
        optionMenu.setMnemonic(KeyEvent.VK_O);
        menuBar.add(optionMenu);
        
        JMenu previewMenu = new JMenu("Preview");	// 预览菜单
        previewMenu.setMnemonic(KeyEvent.VK_R);
        optionMenu.add(previewMenu);
        
        ButtonGroup previewGroup = new ButtonGroup();
        
        JRadioButtonMenuItem showPreviewMenu = new JRadioButtonMenuItem("show");	// 显示预览
        showPreviewMenu.setActionCommand("show");
        showPreviewMenu.setMnemonic(KeyEvent.VK_S);
        showPreviewMenu.setSelected(true);
        showPreviewMenu.addActionListener(this);
        previewGroup.add(showPreviewMenu);
        previewMenu.add(showPreviewMenu);
        
        JRadioButtonMenuItem hidePreviewMenu = new JRadioButtonMenuItem("hide");	// 隐藏预览
        hidePreviewMenu.setActionCommand("hide");
        hidePreviewMenu.setMnemonic(KeyEvent.VK_H);
        hidePreviewMenu.setSelected(false);
        hidePreviewMenu.addActionListener(this);
        previewGroup.add(hidePreviewMenu);
        previewMenu.add(hidePreviewMenu);
        
        JMenu gameLevelMenu = new JMenu("Game level");	// 游戏等级
        gameLevelMenu.setMnemonic(KeyEvent.VK_G);
                
        ButtonGroup levelGroup = new ButtonGroup();
        
        JRadioButtonMenuItem easyLevelMenu = new JRadioButtonMenuItem("easy");	// 容易
        easyLevelMenu.setActionCommand("easy");
        easyLevelMenu.setSelected(true);
        easyLevelMenu.addActionListener(this);
        levelGroup.add(easyLevelMenu);
        gameLevelMenu.add(easyLevelMenu);
        
        JRadioButtonMenuItem middleLevelMenu = new JRadioButtonMenuItem("middle");	// 中等
        middleLevelMenu.setActionCommand("middle");
        middleLevelMenu.setSelected(false);
        middleLevelMenu.addActionListener(this);
        levelGroup.add(middleLevelMenu);
        gameLevelMenu.add(middleLevelMenu);
        
        JRadioButtonMenuItem hardLevelMenu = new JRadioButtonMenuItem("hard");	// 困难
        hardLevelMenu.setActionCommand("hard");
        hardLevelMenu.setSelected(false);
        hardLevelMenu.addActionListener(this);
        levelGroup.add(hardLevelMenu);
        gameLevelMenu.add(hardLevelMenu);
        
        optionMenu.add(gameLevelMenu);
        
        JMenu speedMenu = new JMenu("Speed");		// 游戏速度
        speedMenu.setMnemonic(KeyEvent.VK_S);
        
        ButtonGroup speedGroup = new ButtonGroup();
        
        JRadioButtonMenuItem speed;
        for(int i = 0; i < 10; i++){		// 添加十个速度菜单项
            String s = "Speed " + (i + 1);
            speed = new JRadioButtonMenuItem(s);
            speed.setActionCommand(s);
            speed.addActionListener(this);
            speed.setSelected(i == 0);
            speedGroup.add(speed);
            speedMenu.add(speed);
        }
        
        optionMenu.add(speedMenu);
        
        JMenu playerCountMenu = new JMenu("Player count");	// 添加玩家个数菜单
        playerCountMenu.setMnemonic(KeyEvent.VK_P);
        
        ButtonGroup playerCountGroup = new ButtonGroup();
        
        JRadioButtonMenuItem onePlayerMenu = new JRadioButtonMenuItem("One player");	// 添加“一个玩家“菜单项
        onePlayerMenu.setMnemonic(KeyEvent.VK_O);
        onePlayerMenu.addActionListener(this);
        onePlayerMenu.setActionCommand("one");
        onePlayerMenu.setSelected(true);
        playerCountGroup.add(onePlayerMenu);
        playerCountMenu.add(onePlayerMenu);
        
        JRadioButtonMenuItem twoPlayerMenu = new JRadioButtonMenuItem("Two player");	// 添加“两个玩家“菜单项
        twoPlayerMenu.setMnemonic(KeyEvent.VK_T);
        twoPlayerMenu.addActionListener(this);
        twoPlayerMenu.setActionCommand("two");
        twoPlayerMenu.setSelected(false);
        playerCountGroup.add(twoPlayerMenu);
        playerCountMenu.add(twoPlayerMenu);
        
        optionMenu.add(playerCountMenu);
        
        JMenu helpMenu = new JMenu("Help");		// 添加帮助菜单
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpMenu);
        
        JMenuItem keyHelpMenu = new JMenuItem("keys");	// 添加键盘帮助菜单
        keyHelpMenu.setMnemonic(KeyEvent.VK_K);
        keyHelpMenu.setActionCommand("keys");
        keyHelpMenu.addActionListener(this);
        helpMenu.add(keyHelpMenu);
        
        JMenuItem aboutMenu = new JMenuItem("about");		// 添加“有关”菜单
        aboutMenu.setMnemonic(KeyEvent.VK_A);
        aboutMenu.setActionCommand("about");
        aboutMenu.addActionListener(this);
        helpMenu.add(aboutMenu);
        
        setJMenuBar(menuBar);
    }
    
    // initialize toolbar
    public void initToolBar(){	// 生成工具栏
        toolBar = new JPanel();	// 生成面板
        toolBar.setLayout(new FlowLayout());	// 设置面板的布局方式
        
        startButton = new JButton(createImageIcon("play.gif"));		// 生成play按钮
        startButton.setToolTipText("start a new game");		// 设置play按钮的帮助信息
        startButton.setActionCommand("startButton");
        startButton.setEnabled(true);
        startButton.addActionListener(this);
        toolBar.add(startButton);
        
        pauseButton = new JButton(createImageIcon("pause.gif"));	// 生成pause按钮
        pauseButton.setToolTipText("pause the current game");
        pauseButton.setActionCommand("pauseButton");
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(this);
        toolBar.add(pauseButton);
        
        stopButton = new JButton(createImageIcon("stop.gif"));	// 生成stop按钮
        stopButton.setToolTipText("stop the current game");
        stopButton.setActionCommand("stopButton");
        stopButton.addActionListener(this);
        stopButton.setEnabled(false);
        toolBar.add(stopButton);
    }
    
    // process menu commands
    public void actionPerformed(ActionEvent e){	// 处理用户的菜单命令和按钮命令
        String cmd = e.getActionCommand();
        if("start".equals(cmd)){		// start菜单
            startGame();
            
        }else if("pause".equals(cmd)){	// pause菜单
            int gameState = playerList.getGameState();
            if(gameState == playerList.GAME_PAUSE){	// 如果当前游戏已暂停，则继续游戏
                resumeGame();
            }else if(gameState == playerList.GAME_START){	// 如果当前游戏已开始，则暂停游戏
                pauseGame();
            }
            
        }else if("stop".equals(cmd)){	// stop菜单
            stopGame();
            
        }else if("startButton".equals(cmd)){	// play按钮
            int gameState = playerList.getGameState();
            if(gameState == playerList.GAME_END){	// 如果当前游戏已停止，则开始游戏
                startGame();
            }else if(gameState == playerList.GAME_PAUSE){	// 如果当前游戏已暂停，则继续游戏
                resumeGame();
            }
            
        }else if("pauseButton".equals(cmd)){	// pause按钮
            pauseGame();
            
        }else if("stopButton".equals(cmd)){	// stop按钮
            stopGame();
            
        }else if("easy".equals(cmd)){	// easy菜单
            RussianBox.setMaxBoxType(RussianBox.EASY_LEVEL);	// 设置游戏等级为容易
            
        }else if("middle".equals(cmd)){	// middle菜单
            RussianBox.setMaxBoxType(RussianBox.MID_LEVEL);	// 设置游戏等级为中等
            
        }else if("hard".equals(cmd)){	// hard菜单
            RussianBox.setMaxBoxType(RussianBox.HARD_LEVEL);	// 设置游戏等级为困难
        
        }else if(cmd.indexOf("Speed") != -1){	// 设置方块下降的速度
            int idx = Integer.parseInt(cmd.substring(6)) - 1;
            playerList.getTimer().setDelay(delays[idx]);	// 设置方块下降定时器的延迟
            
        }else if("one".equals(cmd)){	// one菜单
            if(playerList.getGameState() != PlayerList.GAME_END){	// 如果当前游戏还为终止，则首先停止游戏
                stopGame();
            }
            playerList.setPlayers(onePlayerNames, onePlayerKeyMap);	// 将游戏主界面设置为单一玩家界面
            pack();
        
        }else if("two".equals(cmd)){	// two菜单
            if(playerList.getGameState() != PlayerList.GAME_END){	// 如果当前游戏还为终止，则首先停止游戏
                stopGame();
            }
            playerList.setPlayers(twoPlayerNames, twoPlayerKeyMap);	// 将游戏主界面设置为两玩家界面
            pack();
        
        }else if("show".equals(cmd)){	// show菜单
            playerList.setPreviewVisible(true);	// 显示预览
            
        }else if("hide".equals(cmd)){	// hide菜单
            playerList.setPreviewVisible(false);	// 隐藏预览
            
        }else if("keys".equals(cmd)){	// 键盘帮助信息
            KeyHelpDialog d = new KeyHelpDialog(this);	// 显示键盘帮助窗口
            
        }else if("about".equals(cmd)){	// 关于菜单
            AboutDialog d = new AboutDialog(this);	// 显示关于窗口
        }
    }
    
    // start new game
    protected void startGame(){	// 开始一个游戏
        playerList.startGame();	// 开始游戏
        pauseMenu.setEnabled(true); // 使能pause菜单
        stopMenu.setEnabled(true);
        pauseButton.setEnabled(true);	// 使能pause按钮
        stopButton.setEnabled(true);
        startButton.setEnabled(false);
        playerList.requestFocus();	// 将输入焦点设置为游戏主界面
    }
    
    // pause game
    protected void pauseGame(){	// 暂停游戏
        playerList.pauseGame();	// 暂停游戏
        pauseMenu.setText("resume");	// 将pause菜单改为resume菜单
        pauseButton.setEnabled(false);
        startButton.setEnabled(true);
        startButton.setToolTipText("resume the current game");	// 修改start按钮的帮助信息
    }
    
    // resume game
    protected void resumeGame(){	// 继续游戏
        playerList.resumeGame();	// 继续游戏
        pauseMenu.setText("pause");	// 将resume菜单改为pause菜单
        pauseButton.setEnabled(true);
        startButton.setToolTipText("pause the current game");
        startButton.setEnabled(false);
        playerList.requestFocus();	// 将输入焦点设置为游戏主界面
    }
    
	    // stop game
	    protected void stopGame(){	// 停止游戏
        playerList.endGame();
        pauseMenu.setEnabled(false);
        stopMenu.setEnabled(false);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        startButton.setEnabled(true);
        startButton.setToolTipText("start a new game");
    }
    
    // action performed when game is end
    public void gameEnd(){	// 当游戏终止时执行
        pauseMenu.setText("pause");
        pauseMenu.setEnabled(false);
        stopMenu.setEnabled(false);
        startButton.setEnabled(true);
        startButton.setToolTipText("start a new game");
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        if(playerList.getPlayerCount() > 1){	// 当玩家数超过1个时，根据比赛结果显示信息
            String won = null;
            Player leftPlayer = playerList.getPlayerByIndex(0);
            Player rightPlayer = playerList.getPlayerByIndex(1);
            if(leftPlayer.getScore() != rightPlayer.getScore()){
                won = (leftPlayer.getScore() > rightPlayer.getScore()) ? 
                                leftPlayer.getPlayerName() : rightPlayer.getPlayerName();	// 分数较多地为获胜者
                JOptionPane.showMessageDialog(this, won + " has won the game!!!",
                    "game over", JOptionPane.INFORMATION_MESSAGE, null);	// 显示获胜信息
            }else{	// 如果两个人的分数一样多，则显示平局信息
                JOptionPane.showMessageDialog(this, "Both of you are good!!!",
                    "game over", JOptionPane.INFORMATION_MESSAGE, null);
            }
        }
    }
    
    // load image
    public static ImageIcon createImageIcon(String url){	// 根据图片名称生成图标对象
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
    public static void createMainUI(){	// 生成程序主窗口
        GameFrame frame = new GameFrame();
    }
    
    // main entry point
    public static void main(String[] args){	// 程序入口点
        SwingUtilities.invokeLater(new Runnable(){	// 生成一个消息驱动线程，该线程用来接收和处理用户的输入、输出
           public void run() {
               createMainUI();
           }
        });
    }
}
