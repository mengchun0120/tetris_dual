/*
 * PlayerList.java
 *
 * Created on 2004��12��13��, ����11:30
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
    public static final int MAX_PLAYERCOUNT = 2;	// ��������
    public static final int DEF_DELAY = 1000;	// ȱʡ��Ϸ�ٶ�
    public static final int GAME_END = 1;			// ��Ϸ״̬������Ϸ��ֹ
    public static final int GAME_START = 2;		// ��Ϸ״̬������Ϸ��������
    public static final int GAME_PAUSE = 3;		// ��Ϸ״̬������Ϸ��ͣ
    protected Player[] players;	// �������
    protected javax.swing.Timer timer;		// ���Ʒ����½��Ķ�ʱ��
    protected int gameState;	// ��ǰ��Ϸ״̬
    private GameEndAction endAction;	// ��Ϸ��ֹʱ��Ҫִ�еĶ���
    private int firstPlayer;	// ��һ�������ȵ����
    
    /** Creates a new instance of PlayerList */
    public PlayerList(String[] names, int[][] keyMaps) {	// ������Ϸ������
        setOpaque(true);
        setFocusable(true);
        setLayout(new FlowLayout());	// ���ý���Ĳ��ַ�ʽ
        gameState = GAME_END;					// ��Ϸ��ʼ״̬Ϊ��ֹ״̬
        timer = new javax.swing.Timer(DEF_DELAY, this);	// ���ɿ��Ʒ����½��Ķ�ʱ��
        timer.setRepeats(true);
        setPlayers(names, keyMaps);		// Ϊ����������ɽ���
        addKeyListener(this);					// �ô������������û���������
    }
    
    // set the game end action
    public void setGameAction(GameEndAction a){	// ������Ϸ��ֹʱ�Ķ���
        endAction = a;
    }
    
    // get player by index
    public Player getPlayerByIndex(int index){	// ��������������Ҷ���
        return (index >= 0 && index < players.length) ? players[index] : null;
    }
    
    // get player by name
    public Player getPlayerByName(String name){	// �����������������Ҷ���
        int i;
        for(i = 0; i < players.length; i++){
            if(players[i].getName().equals(name)){
                break;
            }
        }
        return (i < players.length) ? players[i] : null;
    }
    
    // set player list
    public void setPlayers(String[] names, int[][] keyMaps){	// Ϊ����������ɽ���
        int i;
        if(players != null){
            for(i = 0; i < players.length; i++){	// �����ǰ����Ҵ���
                players[i].setVisible(false);
                players[i] = null;
            }
            players = null;
        }
        
        int count = Math.min(MAX_PLAYERCOUNT, names.length);
        players = new Player[count];	// ���������������
        for(i = 0; i < players.length; i++){
            players[i] = new Player(names[i], keyMaps[i]);	// ������Ҵ��ڣ�ָ����������Ͱ�������
            players[i].setRussianPlayerAction(this);
            add(players[i]);	// ����Ҵ��ڼ�����Ϸ������
        }
    }
    
    // get the game's state
    public int getGameState(){	// ���ص�ǰ��Ϸ״̬
        return gameState;
    }
    
    // get players' count
    public int getPlayerCount(){	// ���������Ŀ
        return players.length;
    }
    
    // get the top player
    public Player getTopPlayer(){	// ���ط����������
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
    public void setPreviewVisible(boolean b){	// ����������ҵ�Ԥ��״̬
        for(int i = 0; i < players.length; i++){
            players[i].getPreview().setPreviewVisible(b);
        }
    }
    
    // start game
    public void startGame(){	// ��ʼ��Ϸ
        for(int i = 0; i < players.length; i++){	// ������Ҷ���ʼ��Ϸ
            players[i].newGame();
        }
        timer.start();	// �����½���ʱ����ʼ��ʱ
        gameState = GAME_START;	// ��ǰ��Ϸ״̬Ϊ����״̬
    }
    
    // end game
    public void endGame(){	// ������Ϸ
        if(gameState == GAME_END){	// �����Ϸ�Ѿ�ֹͣ����ֱ���˳�
            return;
        }
        gameState = GAME_END;	// ��ǰ��Ϸ״̬Ϊ��ֹ״̬
        for(int i = 0; i < players.length; i++){	// ֹͣ������ҵ���Ϸ
            players[i].endGame();
        }
        timer.stop();	// �����½���ʱ��ֹͣ��ʱ
    }
    
    // pause game
    public void pauseGame(){	// ��ͣ��Ϸ
        if(gameState != GAME_START){	// �����Ϸû�����У���ֱ���˳�
            return;
        }
        gameState = GAME_PAUSE;	// ��ǰ��Ϸ״̬Ϊ��ͣ
        timer.stop();	// �����½���ʱ��ֹͣ��ʱ
    }
    
    // resume game
    public void resumeGame(){	// ������Ϸ
        if(gameState != GAME_PAUSE){	// �����Ϸû����ͣ����ֱ���˳�
            return;
        }
        gameState = GAME_START;	// ��ǰ��Ϸ״̬Ϊ����
        timer.start();	// �����½���ʱ����ʼ��ʱ
    }
    
    // get the timer
    public javax.swing.Timer getTimer(){	// ��÷����½���ʱ������
        return timer;
    }
    
    // set the action performed when all players have ended their games
    public void setGameEndAction(GameEndAction a){	// ������Ϸ��ֹ����
        endAction = a;
    }
    
    // get the action performed when all players have ended their games
    public GameEndAction getGameEndAction(){	// �����Ϸ��ֹ��������
        return endAction;
    }
    
    // action performed by the timer
    public void actionPerformed(ActionEvent e){	// �������½���ʱ������ָ��ʱ��ʱִ�иö���
        if(gameState != GAME_START){	// �����Ϸû�����У���ֱ�ӷ���
            return;
        }
        for(int i = 0; i < players.length; i++){	// ������ҵķ�����½�һ��
            players[i].drop();
        }
    }
    
    // action performed when one player has ended his game
    public void endGameAction(Player p){	// ��һ����ҵ���Ϸ��ֹʱ��ִ�и÷���
        int count = 0;
        for(int i = 0; i < players.length; i++){	// ���������Ϸ����Ҹ���
            if(players[i].isPlaying()){
                count++;
            }
        }
        if(count == 0){		// ���������Ҷ�ֹͣ��Ϸ����ֹͣ��ǰ��Ϸ
            gameState = GAME_END;
            timer.stop();
            endAction.gameEnd();
        }
    }
    
    // action performed when one player has filled some lines
    public void fullLineAction(Player p, int count){	// ��ĳ�������ȥ���к�ִ�и÷���
        if(count < 1){	// �����ȥ����������һ�У��򷵻�
            return;
        }
        for(int i = 0; i < players.length; i++){	// Ϊ����������Ӷ���
            if(players[i] != p){
                players[i].thrushLines(count);
            }else{
                firstPlayer = i;	// ����һ�����ȵ��������Ϊ��ȥ���е����
            }
        }
    }
    
    // event handler when some key is pressed
    public void keyPressed(KeyEvent e){	// ���û����¼���ʱִ�и÷���
        if(gameState != GAME_START){		// �����Ϸû�����У���ֱ�ӷ���
            return;
        }
        int i, key = e.getKeyCode();
        for(i = firstPlayer; i < players.length; i++){	// ��һ��������ȴ�������Ϣ��������������ٴ������Ϣ
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
