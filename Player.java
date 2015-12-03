/*
 * Player.java
 *
 * Created on 2004年12月13日, 上午11:29
 */

import java.awt.*;
import javax.swing.*;
import java.util.Random;

/**
 *
 * @author  Administrator
 */
public class Player extends JPanel {
    public static final int ROW_COUNT = 16;			// 行数
    public static final int COL_COUNT = 10;			// 列数
    public static final Color BUCKET_BACK = Color.WHITE;	// 游戏主界面背景颜色
    public static final int LEFT_KEY_INDEX = 0;		// 左移键索引
    public static final int RIGHT_KEY_INDEX = 1;	// 右移键索引
    public static final int DOWN_KEY_INDEX = 2;		// 快速下降键索引
    public static final int ROTATE_KEY_INDEX = 3;	// 旋转键索引
    public static final int FLASH_TIMES = 5;			// 闪烁次数
    public static final int QUICKDOWN_INTERVAL = 20;	// 快速下降时的时间间隔
    public static final int QUICKDOWN_STEPS = 4;	// 快速下降的格数

    private String playerName;
    private JLabel nameLabel;
    protected BoxBucket bucket;		// 主界面
    protected Preview preview;		// 预览窗口
    protected ScorePad scorePad;	// 分数窗口
    protected RussianBox rbox = new RussianBox();	// 当前块
    protected int curRow, curCol;	// 当前块所在的行、列
    private boolean enabled;
    private boolean gameStart;		// 游戏是否开始
    private int[] keyMap;					// 按键列表
    private boolean[][] fullMatrix = new boolean[ROW_COUNT + 4][COL_COUNT];	// 游戏主界面的块矩阵
    private int[][] imgMatrix = new int[ROW_COUNT + 4][COL_COUNT];		// 游戏主界面的图片矩阵
    private int bucketTop;		// 最高行
    private long score;				// 分数
    private boolean showBox, showGameOver;	// 是否显示方块、游戏结束信息
    private RussianPlayerAction action;

    //------------ constructor ----------------------------
    /** Creates a new instance of Player */
    public Player(String name, int[] keys) {	// 生成游戏主界面，name和keys分别为玩家姓名和按键列表
        setOpaque(true);
        setFocusable(false);	// 该窗口不接受按键消息
        GridBagLayout gridbag = new GridBagLayout();	// 设置窗口的布局方式
        GridBagConstraints c = new GridBagConstraints();
        
        setLayout(gridbag);
        
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.gridheight = 2;
        c.insets = new Insets(10, 0, 0, 0);
        preview = new Preview();	// 生成预览窗口
        gridbag.setConstraints(preview, c);
        add(preview);	// 加入预览窗口
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        nameLabel = new JLabel(name);		// 生成名字标签
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN, 16));
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gridbag.setConstraints(nameLabel, c);
        add(nameLabel);	// 加入名字标签
        
        scorePad = new ScorePad();	// 生成分数窗口
        gridbag.setConstraints(scorePad, c);
        add(scorePad);	// 加入分数窗口
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(10, 0, 0, 0);
        bucket = new BoxBucket();	// 生成游戏主界面
        gridbag.setConstraints(bucket, c);
        add(bucket);
        
        keyMap = new int[4];
        enabled = false;
        setKeyMap(keys);	// 设置按键
    }
    
    //------------ end of constructor ------------------
    
    //------------ player-state-related functions ---------------------
    // enable or disable the player
    public void setPlayerEnabled(boolean b){	// 设置当前玩家能否继续玩游戏
        enabled = b;
    }
    
    // get whether the player is enabled
    public boolean isPlayerEnabled(){		// 返回当前玩家能否玩游戏
        return enabled;
    }
    
    // set the player's name
    public void setPlayerName(String name){	// 设置玩家的名字
        nameLabel.setText(name);
    }
    // get the player's name
    public String getPlayerName(){	// 获得玩家的名字
        return nameLabel.getText();
    }
    
    // set the key map
    public void setKeyMap(int[] keys){	// 设置玩家的按键
        System.arraycopy(keys, 0, keyMap, 0, 4);	// 将keys数组copy到keyMap数组中
    }
    
    // get the key map
    public int[] getKeyMap(){	// 获得玩家的按键
        int[] temparr = new int[4];	// 生成一个暂时数组
        System.arraycopy(keyMap, 0, temparr, 0, 4);	// 将keyMap数组copy到暂时数组中
        return temparr;
    }
    
    // get the preview
    public Preview getPreview(){	// 获得预览窗口
        return preview;
    }
    
    // get the scorePad
    public ScorePad getScorePad(){	// 获得分数窗口
        return scorePad;
    }
    // get whether the player is playing
    public boolean isPlaying(){	// 返回当前游戏是否正在运行
        return gameStart;
    }
    
    // set actions performed by player
    public void setRussianPlayerAction(RussianPlayerAction a){
        action = a;
    }
    
    // get actions performed by player
    public RussianPlayerAction getRussianPlayerAction(){
        return action;
    }
    
    // check whether the bucket reach the top
    public boolean reachTop(){	// 返回堆积的方块是否已到达顶端
        return bucketTop < 4;
    }
    
    // get the score
    public long getScore(){	// 返回分数
        return score;
    }
    //----------- end of player-state-related functions ----------------
    
    //----------- assistant methods for game control ----------------------
    
    // check whether the given russian box can be placed in the bucket
    private boolean canBePlacedInBucket(RussianBox r, int row, int col){	// 判断指定的方块是否能放在制定的行row和列col上
        int mask = 0x8000;					// mask用于获取方块位图的各个位
        int bitmap = r.getBitmap();	// 获取方块位图
        int i, j, crow, ccol;
        
        crow = row;
        for(i = 0; i < 4; i++){	// 从行row开始判断方块能否放在指定列
            ccol = col;
            for(j = 0; j < 4; j++){
                if((bitmap & mask) != 0){	// 如果当前位图的指定位非空。。。。。
                    if(crow < 0 || crow >= ROW_COUNT + 4 || ccol < 0 
                        || ccol >= COL_COUNT || fullMatrix[crow][ccol]){	// 如果当前行和列已经有一个方块，则指定方块不能放在指定位置
                        return false;
                    }
                }
                mask >>= 1;	// 将mask下移到下一个位置
                ccol++;
            }
            crow++;
        }
        
        return true;
    }
    
    // check whether the given row is full
    private boolean isFullRow(int row){	// 判断指定的行是否全满
        int i;
        for(i = 0; i < COL_COUNT; i++){
            if(!fullMatrix[row][i]){	// 如果row行i列非空，则该行未满，返回false
                return false;
            }
        }
        return true;
    }
    
    // copy one row to another row
    private void copyRow(int src, int dest){	// 将src行copy到dest行
        for(int i = 0; i < COL_COUNT; i++){
            fullMatrix[dest][i] = fullMatrix[src][i];
            imgMatrix[dest][i] = imgMatrix[src][i];
        }
    }
    
    // empty one row
    private void emptyRow(int row){	// 清空一行
        for(int i = 0; i < COL_COUNT; i++){
            fullMatrix[row][i] = false;
            imgMatrix[row][i] = -1;
        }
    }
    
    // empty the bucket's contents
    private void emptyBucket(){	// 清空所有行
        for(int i = 0; i < fullMatrix.length; i++){
            emptyRow(i);
        }
    }
    
    // place the russian box into the bucket
    protected void placeBoxInBucket(){	// 将当前方块放在主界面中
        int i, j, row, col;
        int mask = 0x8000;
        int bitmap = rbox.getBitmap();
        
        row = curRow;
        for(i = 0; i < 4; i++){
            col = curCol;
            for(j = 0; j < 4; j++){
                if((mask & bitmap) != 0 && row >= 0 && row < ROW_COUNT + 4 
                    && col >= 0 && col < COL_COUNT){
                    fullMatrix[row][col] = true;
                    imgMatrix[row][col] = rbox.getBoxType();
                }
                col++;
                mask >>= 1;
            }
            row++;
        }
        
        int top = curRow + rbox.getTop();
        
        if(top < bucketTop){	// 重新设置堆积方块的最高行数
            bucketTop = top;
        }
    }
    
   
    // erase full rows
    protected void eraseFullRows(int[] fullRows, int fullRowCount){	// 删除指定的行
        int step, i;
        step = 1;
        for(i = 0; i < fullRowCount; i++){	
            int startRow = fullRows[i] - 1;
            int endRow = (i == fullRowCount - 1) ? bucketTop : fullRows[i + 1] + 1;
            for(int j = startRow; j >= endRow; j--){ // 删除指定行，并下移上面的行
                copyRow(j, j + step);
            }
            step++;
        }
        for(i = bucketTop; i < bucketTop + fullRowCount; i++){	// 将上方空出的行清空
            emptyRow(i);
        }
        bucket.repaint();	// 重绘游戏界面
    }

    // drop the russian box by one row
    protected void moveBoxDownOneRow(){	// 将方块下移一行
        bucket.paintBox(rbox, curRow, curCol, true);	// 清除当前方块
        curRow++;	// 将方块的行数加一
        bucket.paintBox(rbox, curRow, curCol, false);	// 重绘当前方块
    }
    
    // get the bottom row the russian box can reach
    protected int getBottomRow(RussianBox rbox, int startRow, int col){	// 获得方块能到达的最底端的行数
        int i = startRow;
        while(i < ROW_COUNT + 4 && canBePlacedInBucket(rbox, i, col)){
            i++;
        }
        return (i - 1);
    }
    
    // move the russian box left by one column
    protected boolean moveBoxLeft(){	// 将方块向左移一格
        if(!canBePlacedInBucket(rbox, curRow, curCol - 1)){	// 如果方块不能放在指定位置，则返回
            return false;
        }
        
        bucket.paintBox(rbox, curRow, curCol, true);	// 清除当前方块
        curCol--;	// 将方块的列数减一
        bucket.paintBox(rbox, curRow, curCol, false);	// 重绘当前方块
        return true;
    }
    
    // move the russian box right by one column
    protected boolean moveBoxRight(){	// 将方块向右移一格
        if(!canBePlacedInBucket(rbox, curRow, curCol + 1)){
            return false;
        }
        
        bucket.paintBox(rbox, curRow, curCol, true);
        curCol++;
        bucket.paintBox(rbox, curRow, curCol, false);
        return true;
    }
    
    // rotate the russian box
    protected boolean rotateBox(){	// 旋转方块
        RussianBox r = new RussianBox(rbox);	// 生成一个和当前方块一样的暂时方块
        r.rotate();	// 旋转暂时方块
        if(!canBePlacedInBucket(r, curRow, curCol)){	// 判断该暂时方块能否放在指定位置
            return false;
        }
        
        bucket.paintBox(rbox, curRow, curCol, true);	// 清除当前方块
        rbox.setBox(r);		// 旋转当前方块
        bucket.paintBox(rbox, curRow, curCol, false);	// 重绘当前方块
        return true;
    }
    
    // move down the russian box fast
    protected boolean moveBoxDownFast(){	// 快速下移当前方块
        int i;
        for(i = 0; i < QUICKDOWN_STEPS; i++){	// 将方块至多下移QUICKDOWN_STEPS格
            if(canBePlacedInBucket(rbox, curRow + 1, curCol)){	// 如果当前方块可以下移，则下移一格
                moveBoxDownOneRow();
            }else{	// 否则，退出循环
                break;
            }
            try{
                Thread.sleep(QUICKDOWN_INTERVAL);	// 等待一小段时间
            }catch(InterruptedException e){
                System.err.println("Thread.sleep fails");
            }
        }
        return i == QUICKDOWN_STEPS;
    }
    
    // reset the russian box
    protected boolean resetBox(){	// 重新设置当前块
        RussianBox r = preview.getBox();	// 从预览窗口获得下一个方块
        int col = 5;
        int row = 4 - r.getTop();	// 计算下一个方块可以放置的行和列
        while(row >= 0 && !canBePlacedInBucket(r, row, col)){
            row--;
        }
        
        if(row < 0){	// 如果该方块不能放置在游戏界面中，则当前玩家的游戏结束
            bucketTop = -1;
            gameStart = false;
            preview.closePreview();
            showGameOver = true;	// 显示游戏结束
            bucket.repaint();	// 重绘游戏主界面
            action.endGameAction(this);
            return false;
        }
        
        rbox.setBox(r);	// 将当前方块设置为下一个方块
        curCol = col;
        curRow = row;
        showBox = true;	// 显示当前方块
        enabled = true;	// 允许用户输入
        preview.resetBox();	// 重设预览窗口的方块
        bucket.paintBox(rbox, curRow, curCol, false);	// 绘制当前方块
        
        return true;
    }

    //------------ end of assistant methods for game control ---------------
    
    //------------ methods for game control --------------------------------
    // process input
    public void processInput(int keyCode){	// 处理用户键盘消息
        if(!enabled && !gameStart){
            return;
        }
        int i;
        for(i = 0; i < keyMap.length; i++){	// 查找用户的按键列表，获取用户按键对应的索引
            if(keyMap[i] == keyCode){
                break;
            }
        }
        if(i == keyMap.length){	// 如果没有找到指定按键，则该按键消息不是发给当前用户的，返回
            return;
        }

        switch(i){
            case LEFT_KEY_INDEX:	// 用户按下了“左移”键
                moveBoxLeft();	
                break;
            case RIGHT_KEY_INDEX:	// 用户按下了“右移“键
                moveBoxRight();
                break;
            case DOWN_KEY_INDEX:	// 用户按下了“快速下移”键
                moveBoxDownFast();
                break;
            case ROTATE_KEY_INDEX:	// 用户按下了旋转键
                rotateBox();
                break;
        }
    }
    
    // start new game
    public void newGame(){	// 新开一个游戏
        emptyBucket();	// 清空游戏主界面
        rbox.randBox();	// 随机设置当前方块
        score = 0;
        bucketTop = ROW_COUNT + 4;
        curCol = 5;
        curRow = 4 - rbox.getTop();
        showBox = true;
        enabled = true;
        gameStart = true;
        preview.openPreview();
        scorePad.setScore(score);
        scorePad.setScoreVisible(true);
        showGameOver = false;
        bucket.repaint();
    }
    
    // end game and erase the bucket
    public void endGame(){	// 终止游戏
        emptyBucket();	// 清空游戏主界面
        showBox = false;
        enabled = false;
        gameStart = false;
        showGameOver = false;
        scorePad.setScoreVisible(false);	// 不显示分数
        preview.closePreview();
        bucket.repaint();
    }
    
    
    // drop the russian box in the bucket by one row
    public void drop(){	// 将当前方块下移一格
        if(!enabled || !gameStart){	// 如果当前玩家不能玩游戏或者游戏尚未开始，则退出
            return;
        }
        if(canBePlacedInBucket(rbox, curRow + 1, curCol)){	// 如果当前方块能下移一格，则下移一格并返回
            moveBoxDownOneRow();
            return;
        }
        // 如果当前方块不能下移一格，则该方块已经到达底端
        showBox = false;
        enabled = false;	// 在处理过程中不能接受用户输入
        placeBoxInBucket();	// 将当前块放置在游戏界面中

        int row, count;
        int[] fullRows = new int[4];
        count = 0;
        for(row = curRow + 3; row >= curRow; row--){	// 计算已满行
            if(isFullRow(row)){
                fullRows[count] = row;
                count++;
            }
        }

        if(count > 0){	// 如果至少有一行已满
            bucket.flashFullRows(fullRows, count, FLASH_TIMES); // 闪动已满的行
            eraseFullRows(fullRows, count);	// 清除已满的行
            bucketTop += count;	// 修改堆积方块的最高行数
            if(count > 1){	// 如果行数大于一，则给其他玩家加入行
                action.fullLineAction(this, count);
            }
        }
        
        score += calculateScore(rbox, count);	// 计算分数
        scorePad.setScore(score);	// 更新分数窗口
        
        if(reachTop()){	// 如果堆积方块已到达顶部，则游戏结束
            gameStart = false;
            showGameOver = true;
            preview.closePreview();
            bucket.repaint();
            action.endGameAction(this);
        }else{
            resetBox();	// 否则将当前方块设置为下一个方块
        }
    }
    
    // thrush lines into the bottom of the bucket
    public void thrushLines(int c){	// 在游戏界面中加入c行
        if(!gameStart || !enabled || c <= 0){
            return;
        }
        int bottom = getBottomRow(rbox, curRow, curCol);
        int i;
        for(i = Math.max(bucketTop, c); i < ROW_COUNT + 4; i++){	// 将堆积的方块上移c行
            copyRow(i, i - c);
        }
        
        Random r = new Random();
        int boxType = RussianBox.getBoxTypeCount();
        for(i = Math.max(ROW_COUNT + 4 - c, 0); i < ROW_COUNT + 4; i++){	// 在游戏主界面底部随机的加入c行
            for(int j = 0; j < COL_COUNT; j++){
                fullMatrix[i][j] = (r.nextInt(2) == 0);
                if(fullMatrix[i][j]){
                    imgMatrix[i][j] = r.nextInt(boxType + 1);
                }else{
                    imgMatrix[i][j] = -1;
                }
            }
        }
        
        if(curRow > bottom - c){	// 如果当前方块不能放置在当前位置，则将其上移到合适的位置
           for(i = Math.max(0, curRow - c); 
                    i <= curRow && canBePlacedInBucket(rbox, i, curCol); i++);
           if(i > curRow){
                bucketTop = -1;
           }else{
                curRow = i - 1;
                if(curRow <= 0){
                    bucketTop = -1;
                }
           }
        }
        
        bucketTop -= c;
        bucket.repaint();	// 重绘游戏主界面
        
        if(reachTop()){	// 如果堆积方块已到达顶端，则游戏结束
            showBox = false;
            enabled = false;
            gameStart = false;
            showGameOver = true;
            bucket.repaint();
            preview.closePreview();
            action.endGameAction(this);
        }
    }
    //---------------- end of methods for game control --------------------
    
    //--------------- class BoxBucket -------------------
    // class BoxBucket
    // used to show the main interface of player
    private class BoxBucket extends JComponent {	// 游戏主界面
        public static final int BUCKET_WIDTH = COL_COUNT * RussianBox.BOX_WIDTH + 2;	// 主界面宽度
        public static final int BUCKET_HEIGHT = ROW_COUNT * RussianBox.BOX_WIDTH + 2;	// 高度
        public static final int FLASH_INTERVAL = 100;		// 闪烁间隔
        private Insets insets;
        private Rectangle bounds;
    
        /** Creates a new instance of BoxBucket */
        public BoxBucket() {	// 生成游戏主界面
            setOpaque(true);
            setFocusable(false);	// 该对象不能接受键盘输入
            setPreferredSize(new Dimension(BUCKET_WIDTH + 2, BUCKET_HEIGHT + 2));	// 设置大小
            setBorder(BorderFactory.createLineBorder(Color.BLACK));	// 设置边界
            insets = getInsets();
            bounds = new Rectangle(insets.left, insets.top, BUCKET_WIDTH, BUCKET_HEIGHT);	// 设置主界面边界
            showGameOver = false;
        }
    
        // get the given row's top
        public int getRowTop(int row){	// 获得指定行的纵坐标
            return insets.top + (row - 4) * RussianBox.BOX_WIDTH;
        }
    
        // get the given coloumn's left
        public int getColLeft(int col){	// 获得指定列的横坐标
            return insets.left + col * RussianBox.BOX_WIDTH;
        }
    
        // draw the given row
        private void drawRow(Graphics g, int row, boolean useBackground){	// 画指定的行，useBackground用来指定是否用背景颜色画
            int i, x, y;
            x = getColLeft(0);
            y = getRowTop(row);
            for(i = 0; i < COL_COUNT; i++){
            	if(useBackground || !fullMatrix[row][i]) {
            		RussianBox.drawBox(g, x, y, BUCKET_BACK);
            	} else {
            		RussianBox.drawImg(g, x, y, imgMatrix[row][i]);
            	}
              x += RussianBox.BOX_WIDTH;
            }
        }
    
        // paint the box
        // if erase is true, the specified box will be drawn with background color
        public void paintBox(RussianBox r, int row, int col, boolean erase){	// 在指定行和列画方块
          if(!erase) {
          	r.draw(getGraphics(), getColLeft(col), getRowTop(row), bounds);
          } else {
          	r.draw(getGraphics(), getColLeft(col), getRowTop(row), BUCKET_BACK, bounds);
          }
        }
        
        public void paintComponent(Graphics g){	// 当需要重绘窗口时调用该方法
            Color oldColor = g.getColor();
            g.setColor(BUCKET_BACK);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);	// 用背景色清除当前窗口
            g.setColor(oldColor);
            for(int i = 4; i < fullMatrix.length; i++){	// 绘制所有行
                drawRow(g, i, false);	// 绘制第i行
            }
            if(showBox){
                rbox.draw(g, getColLeft(curCol), getRowTop(curRow), bounds);	// 绘制当前方块
            }
            if(showGameOver){		// 绘制游戏结束消息
                String s = "Game Over !!!";
                Font oldFont = g.getFont();
                g.setFont(oldFont.deriveFont(Font.BOLD, 24));
                g.setColor(new Color(200, 0, 0));
                g.drawString(s, 35, 170);
                g.setFont(oldFont);
            }
        }
        
        // flash full rows
        public void flashFullRows(int[] fullRows, int fullRowCount, int flashTimes){	// 闪烁指定的行，flashTimes制定闪烁的次数
            Graphics g = getGraphics();
            for(int i = 0; i < flashTimes; i++){
                boolean useBackground = (i % 2) == 0;
                for(int j = 0; j < fullRowCount; j++){
                    drawRow(g, fullRows[j], useBackground);
                }
                try{
                    Thread.sleep(FLASH_INTERVAL);
                }catch(InterruptedException e){
                    System.err.println("Thread.sleep fails");
                    return;
                }
            }
        }
    }
    //------------ end of class BoxBucket --------------
    
    //---------------- other utilities ------------------
    // get score according to the type and full line count
    public static long calculateScore(RussianBox box, int fullLineCount){
        int boxScore, lineScore;
        int boxtype = box.getBoxType();
        if(boxtype <= RussianBox.EASY_LEVEL){
            boxScore = 10;
        }else if(boxtype <= RussianBox.MID_LEVEL){
            boxScore = 20;
        }else{
            boxScore = 30;
        }
        if(fullLineCount <= 0){
            lineScore = 0;
        }else if(fullLineCount == 1){
            lineScore = 100;
        }else{
            lineScore = fullLineCount * 150;
        }
        return boxScore + lineScore;
    }
    
    //---------------- end of other utilities -------------------
}
