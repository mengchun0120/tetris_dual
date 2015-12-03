/*
 * PlayerList.java
 *
 * Created on 2004年12月13日, 上午11:30
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author  Administrator
 */
public class PlayerList extends JPanel 
                    implements RussianPlayerAction, KeyListener, ActionListener{
    public static final int MAX_PLAYERCOUNT = 2;	// 最大玩家数
    public static final int DEF_DELAY = 1000;	// 缺省游戏速度
    public static final int GAME_END = 1;			// 游戏状态——游戏终止
    public static final int GAME_START = 2;		// 游戏状态——游戏正在运行
    public static final int GAME_PAUSE = 3;		// 游戏状态——游戏暂停
    protected Player[] players;	// 所有玩家
    protected javax.swing.Timer timer;		// 控制方块下降的定时器
    protected int gameState;	// 当前游戏状态
    private GameEndAction endAction;	// 游戏终止时需要执行的动作
    private int firstPlayer;	// 第一个被调度的玩家
    
    /** Creates a new instance of PlayerList */
    public PlayerList(String[] names, int[][] keyMaps) {	// 生成游戏主界面
        setOpaque(true);
        setFocusable(true);
        setLayout(new FlowLayout());	// 设置界面的布局方式
        gameState = GAME_END;					// 游戏初始状态为终止状态
        timer = new javax.swing.Timer(DEF_DELAY, this);	// 生成控制方块下降的定时器
        timer.setRepeats(true);
        setPlayers(names, keyMaps);		// 为各个玩家生成界面
        addKeyListener(this);					// 该窗口用来接收用户键盘输入
    }
    
    // set the game end action
    public void setGameAction(GameEndAction a){	// 设置游戏终止时的动作
        endAction = a;
    }
    
    // get player by index
    public Player getPlayerByIndex(int index){	// 根据索引返回玩家对象
        return (index >= 0 && index < players.length) ? players[index] : null;
    }
    
    // get player by name
    public Player getPlayerByName(String name){	// 根据玩家姓名返回玩家对象
        int i;
        for(i = 0; i < players.length; i++){
            if(players[i].getName().equals(name)){
                break;
            }
        }
        return (i < players.length) ? players[i] : null;
    }
    
    // set player list
    public void setPlayers(String[] names, int[][] keyMaps){	// 为各个玩家生成界面
        int i;
        if(players != null){
            for(i = 0; i < players.length; i++){	// 清除先前的玩家窗口
                players[i].setVisible(false);
                players[i] = null;
            }
            players = null;
        }
        
        int count = Math.min(MAX_PLAYERCOUNT, names.length);
        players = new Player[count];	// 重新生成玩家数组
        for(i = 0; i < players.length; i++){
            players[i] = new Player(names[i], keyMaps[i]);	// 生成玩家窗口，指定玩家姓名和按键设置
            players[i].setRussianPlayerAction(this);
            add(players[i]);	// 将玩家窗口加入游戏主界面
        }
    }
    
    // get the game's state
    public int getGameState(){	// 返回当前游戏状态
        return gameState;
    }
    
    // get players' count
    public int getPlayerCount(){	// 返回玩家数目
        return players.length;
    }
    
    // get the top player
    public Player getTopPlayer(){	// 返回分数最多的玩家
        int i, j = 0;
        long top = players[0].getScore();
        for(i = 1; i < players.length; i++){
            if(players[i].getScore() > top){
                j = i;
                top = players[i].getScore();
            }
        }
        return players[j];
    }
    
    // set the players' preview's visibility
    public void setPreviewVisible(boolean b){	// 设置所有玩家的预览状态
        for(int i = 0; i < players.length; i++){
            players[i].getPreview().setPreviewVisible(b);
        }
    }
    
    // start game
    public void startGame(){	// 开始游戏
        for(int i = 0; i < players.length; i++){	// 所有玩家都开始游戏
            players[i].newGame();
        }
        timer.start();	// 方块下降定时器开始计时
        gameState = GAME_START;	// 当前游戏状态为运行状态
    }
    
    // end game
    public void endGame(){	// 结束游戏
        if(gameState == GAME_END){	// 如果游戏已经停止，则直接退出
            return;
        }
        gameState = GAME_END;	// 当前游戏状态为终止状态
        for(int i = 0; i < players.length; i++){	// 停止所有玩家的游戏
            players[i].endGame();
        }
        timer.stop();	// 方块下降定时器停止计时
    }
    
    // pause game
    public void pauseGame(){	// 暂停游戏
        if(gameState != GAME_START){	// 如果游戏没有运行，则直接退出
            return;
        }
        gameState = GAME_PAUSE;	// 当前游戏状态为暂停
        timer.stop();	// 方块下降定时器停止计时
    }
    
    // resume game
    public void resumeGame(){	// 继续游戏
        if(gameState != GAME_PAUSE){	// 如果游戏没有暂停，则直接退出
            return;
        }
        gameState = GAME_START;	// 当前游戏状态为运行
        timer.start();	// 方块下降定时器开始计时
    }
    
    // get the timer
    public javax.swing.Timer getTimer(){	// 获得方块下降定时器对象
        return timer;
    }
    
    // set the action performed when all players have ended their games
    public void setGameEndAction(GameEndAction a){	// 设置游戏终止动作
        endAction = a;
    }
    
    // get the action performed when all players have ended their games
    public GameEndAction getGameEndAction(){	// 获得游戏终止动作对象
        return endAction;
    }
    
    // action performed by the timer
    public void actionPerformed(ActionEvent e){	// 当方块下降定时器到达指定时间时执行该动作
        if(gameState != GAME_START){	// 如果游戏没有运行，则直接返回
            return;
        }
        for(int i = 0; i < players.length; i++){	// 所有玩家的方块均下降一格
            players[i].drop();
        }
    }
    
    // action performed when one player has ended his game
    public void endGameAction(Player p){	// 当一个玩家的游戏终止时，执行该方法
        int count = 0;
        for(int i = 0; i < players.length; i++){	// 获得正在游戏的玩家个数
            if(players[i].isPlaying()){
                count++;
            }
        }
        if(count == 0){		// 如果所有玩家都停止游戏，则停止当前游戏
            gameState = GAME_END;
            timer.stop();
            endAction.gameEnd();
        }
    }
    
    // action performed when one player has filled some lines
    public void fullLineAction(Player p, int count){	// 当某个玩家消去多行后，执行该方法
        if(count < 1){	// 如果消去的行数不足一行，则返回
            return;
        }
        for(int i = 0; i < players.length; i++){	// 为其他玩家增加多行
            if(players[i] != p){
                players[i].thrushLines(count);
            }else{
                firstPlayer = i;	// 将第一个调度的玩家设置为消去多行的玩家
            }
        }
    }
    
    // event handler when some key is pressed
    public void keyPressed(KeyEvent e){	// 当用户按下键盘时执行该方法
        if(gameState != GAME_START){		// 如果游戏没有运行，则直接返回
            return;
        }
        int i, key = e.getKeyCode();
        for(i = firstPlayer; i < players.length; i++){	// 第一个玩家首先处理按键消息，而后其他玩家再处理该消息
            players[i].processInput(key);
        }
        for(i = 0; i < firstPlayer; i++){
            players[i].processInput(key);
        }
    }
    
    // event handler when some key is released
    public void keyReleased(KeyEvent e){
        
    }
    
    // event handler when some key is typed
    public void keyTyped(KeyEvent e){
        
    }    
}
