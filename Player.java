/*
 * Player.java
 *
 * Created on 2004��12��13��, ����11:29
 */

import java.awt.*;
import javax.swing.*;
import java.util.Random;

/**
 *
 * @author  Administrator
 */
public class Player extends JPanel {
    public static final int ROW_COUNT = 16;			// ����
    public static final int COL_COUNT = 10;			// ����
    public static final Color BUCKET_BACK = Color.WHITE;	// ��Ϸ�����汳����ɫ
    public static final int LEFT_KEY_INDEX = 0;		// ���Ƽ�����
    public static final int RIGHT_KEY_INDEX = 1;	// ���Ƽ�����
    public static final int DOWN_KEY_INDEX = 2;		// �����½�������
    public static final int ROTATE_KEY_INDEX = 3;	// ��ת������
    public static final int FLASH_TIMES = 5;			// ��˸����
    public static final int QUICKDOWN_INTERVAL = 20;	// �����½�ʱ��ʱ����
    public static final int QUICKDOWN_STEPS = 4;	// �����½��ĸ���

    private String playerName;
    private JLabel nameLabel;
    protected BoxBucket bucket;		// ������
    protected Preview preview;		// Ԥ������
    protected ScorePad scorePad;	// ��������
    protected RussianBox rbox = new RussianBox();	// ��ǰ��
    protected int curRow, curCol;	// ��ǰ�����ڵ��С���
    private boolean enabled;
    private boolean gameStart;		// ��Ϸ�Ƿ�ʼ
    private int[] keyMap;					// �����б�
    private boolean[][] fullMatrix = new boolean[ROW_COUNT + 4][COL_COUNT];	// ��Ϸ������Ŀ����
    private int[][] imgMatrix = new int[ROW_COUNT + 4][COL_COUNT];		// ��Ϸ�������ͼƬ����
    private int bucketTop;		// �����
    private long score;				// ����
    private boolean showBox, showGameOver;	// �Ƿ���ʾ���顢��Ϸ������Ϣ
    private RussianPlayerAction action;

    //------------ constructor ----------------------------
    /** Creates a new instance of Player */
    public Player(String name, int[] keys) {	// ������Ϸ�����棬name��keys�ֱ�Ϊ��������Ͱ����б�
        setOpaque(true);
        setFocusable(false);	// �ô��ڲ����ܰ�����Ϣ
        GridBagLayout gridbag = new GridBagLayout();	// ���ô��ڵĲ��ַ�ʽ
        GridBagConstraints c = new GridBagConstraints();
        
        setLayout(gridbag);
        
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.gridheight = 2;
        c.insets = new Insets(10, 0, 0, 0);
        preview = new Preview();	// ����Ԥ������
        gridbag.setConstraints(preview, c);
        add(preview);	// ����Ԥ������
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        nameLabel = new JLabel(name);		// �������ֱ�ǩ
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN, 16));
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gridbag.setConstraints(nameLabel, c);
        add(nameLabel);	// �������ֱ�ǩ
        
        scorePad = new ScorePad();	// ���ɷ�������
        gridbag.setConstraints(scorePad, c);
        add(scorePad);	// �����������
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(10, 0, 0, 0);
        bucket = new BoxBucket();	// ������Ϸ������
        gridbag.setConstraints(bucket, c);
        add(bucket);
        
        keyMap = new int[4];
        enabled = false;
        setKeyMap(keys);	// ���ð���
    }
    
    //------------ end of constructor ------------------
    
    //------------ player-state-related functions ---------------------
    // enable or disable the player
    public void setPlayerEnabled(boolean b){	// ���õ�ǰ����ܷ��������Ϸ
        enabled = b;
    }
    
    // get whether the player is enabled
    public boolean isPlayerEnabled(){		// ���ص�ǰ����ܷ�����Ϸ
        return enabled;
    }
    
    // set the player's name
    public void setPlayerName(String name){	// ������ҵ�����
        nameLabel.setText(name);
    }
    // get the player's name
    public String getPlayerName(){	// �����ҵ�����
        return nameLabel.getText();
    }
    
    // set the key map
    public void setKeyMap(int[] keys){	// ������ҵİ���
        System.arraycopy(keys, 0, keyMap, 0, 4);	// ��keys����copy��keyMap������
    }
    
    // get the key map
    public int[] getKeyMap(){	// �����ҵİ���
        int[] temparr = new int[4];	// ����һ����ʱ����
        System.arraycopy(keyMap, 0, temparr, 0, 4);	// ��keyMap����copy����ʱ������
        return temparr;
    }
    
    // get the preview
    public Preview getPreview(){	// ���Ԥ������
        return preview;
    }
    
    // get the scorePad
    public ScorePad getScorePad(){	// ��÷�������
        return scorePad;
    }
    // get whether the player is playing
    public boolean isPlaying(){	// ���ص�ǰ��Ϸ�Ƿ���������
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
    public boolean reachTop(){	// ���ضѻ��ķ����Ƿ��ѵ��ﶥ��
        return bucketTop < 4;
    }
    
    // get the score
    public long getScore(){	// ���ط���
        return score;
    }
    //----------- end of player-state-related functions ----------------
    
    //----------- assistant methods for game control ----------------------
    
    // check whether the given russian box can be placed in the bucket
    private boolean canBePlacedInBucket(RussianBox r, int row, int col){	// �ж�ָ���ķ����Ƿ��ܷ����ƶ�����row����col��
        int mask = 0x8000;					// mask���ڻ�ȡ����λͼ�ĸ���λ
        int bitmap = r.getBitmap();	// ��ȡ����λͼ
        int i, j, crow, ccol;
        
        crow = row;
        for(i = 0; i < 4; i++){	// ����row��ʼ�жϷ����ܷ����ָ����
            ccol = col;
            for(j = 0; j < 4; j++){
                if((bitmap & mask) != 0){	// �����ǰλͼ��ָ��λ�ǿա���������
                    if(crow < 0 || crow >= ROW_COUNT + 4 || ccol < 0 
                        || ccol >= COL_COUNT || fullMatrix[crow][ccol]){	// �����ǰ�к����Ѿ���һ�����飬��ָ�����鲻�ܷ���ָ��λ��
                        return false;
                    }
                }
                mask >>= 1;	// ��mask���Ƶ���һ��λ��
                ccol++;
            }
            crow++;
        }
        
        return true;
    }
    
    // check whether the given row is full
    private boolean isFullRow(int row){	// �ж�ָ�������Ƿ�ȫ��
        int i;
        for(i = 0; i < COL_COUNT; i++){
            if(!fullMatrix[row][i]){	// ���row��i�зǿգ������δ��������false
                return false;
            }
        }
        return true;
    }
    
    // copy one row to another row
    private void copyRow(int src, int dest){	// ��src��copy��dest��
        for(int i = 0; i < COL_COUNT; i++){
            fullMatrix[dest][i] = fullMatrix[src][i];
            imgMatrix[dest][i] = imgMatrix[src][i];
        }
    }
    
    // empty one row
    private void emptyRow(int row){	// ���һ��
        for(int i = 0; i < COL_COUNT; i++){
            fullMatrix[row][i] = false;
            imgMatrix[row][i] = -1;
        }
    }
    
    // empty the bucket's contents
    private void emptyBucket(){	// ���������
        for(int i = 0; i < fullMatrix.length; i++){
            emptyRow(i);
        }
    }
    
    // place the russian box into the bucket
    protected void placeBoxInBucket(){	// ����ǰ���������������
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
        
        if(top < bucketTop){	// �������öѻ�������������
            bucketTop = top;
        }
    }
    
   
    // erase full rows
    protected void eraseFullRows(int[] fullRows, int fullRowCount){	// ɾ��ָ������
        int step, i;
        step = 1;
        for(i = 0; i < fullRowCount; i++){	
            int startRow = fullRows[i] - 1;
            int endRow = (i == fullRowCount - 1) ? bucketTop : fullRows[i + 1] + 1;
            for(int j = startRow; j >= endRow; j--){ // ɾ��ָ���У��������������
                copyRow(j, j + step);
            }
            step++;
        }
        for(i = bucketTop; i < bucketTop + fullRowCount; i++){	// ���Ϸ��ճ��������
            emptyRow(i);
        }
        bucket.repaint();	// �ػ���Ϸ����
    }

    // drop the russian box by one row
    protected void moveBoxDownOneRow(){	// ����������һ��
        bucket.paintBox(rbox, curRow, curCol, true);	// �����ǰ����
        curRow++;	// �������������һ
        bucket.paintBox(rbox, curRow, curCol, false);	// �ػ浱ǰ����
    }
    
    // get the bottom row the russian box can reach
    protected int getBottomRow(RussianBox rbox, int startRow, int col){	// ��÷����ܵ������׶˵�����
        int i = startRow;
        while(i < ROW_COUNT + 4 && canBePlacedInBucket(rbox, i, col)){
            i++;
        }
        return (i - 1);
    }
    
    // move the russian box left by one column
    protected boolean moveBoxLeft(){	// ������������һ��
        if(!canBePlacedInBucket(rbox, curRow, curCol - 1)){	// ������鲻�ܷ���ָ��λ�ã��򷵻�
            return false;
        }
        
        bucket.paintBox(rbox, curRow, curCol, true);	// �����ǰ����
        curCol--;	// �������������һ
        bucket.paintBox(rbox, curRow, curCol, false);	// �ػ浱ǰ����
        return true;
    }
    
    // move the russian box right by one column
    protected boolean moveBoxRight(){	// ������������һ��
        if(!canBePlacedInBucket(rbox, curRow, curCol + 1)){
            return false;
        }
        
        bucket.paintBox(rbox, curRow, curCol, true);
        curCol++;
        bucket.paintBox(rbox, curRow, curCol, false);
        return true;
    }
    
    // rotate the russian box
    protected boolean rotateBox(){	// ��ת����
        RussianBox r = new RussianBox(rbox);	// ����һ���͵�ǰ����һ������ʱ����
        r.rotate();	// ��ת��ʱ����
        if(!canBePlacedInBucket(r, curRow, curCol)){	// �жϸ���ʱ�����ܷ����ָ��λ��
            return false;
        }
        
        bucket.paintBox(rbox, curRow, curCol, true);	// �����ǰ����
        rbox.setBox(r);		// ��ת��ǰ����
        bucket.paintBox(rbox, curRow, curCol, false);	// �ػ浱ǰ����
        return true;
    }
    
    // move down the russian box fast
    protected boolean moveBoxDownFast(){	// �������Ƶ�ǰ����
        int i;
        for(i = 0; i < QUICKDOWN_STEPS; i++){	// ��������������QUICKDOWN_STEPS��
            if(canBePlacedInBucket(rbox, curRow + 1, curCol)){	// �����ǰ����������ƣ�������һ��
                moveBoxDownOneRow();
            }else{	// �����˳�ѭ��
                break;
            }
            try{
                Thread.sleep(QUICKDOWN_INTERVAL);	// �ȴ�һС��ʱ��
            }catch(InterruptedException e){
                System.err.println("Thread.sleep fails");
            }
        }
        return i == QUICKDOWN_STEPS;
    }
    
    // reset the russian box
    protected boolean resetBox(){	// �������õ�ǰ��
        RussianBox r = preview.getBox();	// ��Ԥ�����ڻ����һ������
        int col = 5;
        int row = 4 - r.getTop();	// ������һ��������Է��õ��к���
        while(row >= 0 && !canBePlacedInBucket(r, row, col)){
            row--;
        }
        
        if(row < 0){	// ����÷��鲻�ܷ�������Ϸ�����У���ǰ��ҵ���Ϸ����
            bucketTop = -1;
            gameStart = false;
            preview.closePreview();
            showGameOver = true;	// ��ʾ��Ϸ����
            bucket.repaint();	// �ػ���Ϸ������
            action.endGameAction(this);
            return false;
        }
        
        rbox.setBox(r);	// ����ǰ��������Ϊ��һ������
        curCol = col;
        curRow = row;
        showBox = true;	// ��ʾ��ǰ����
        enabled = true;	// �����û�����
        preview.resetBox();	// ����Ԥ�����ڵķ���
        bucket.paintBox(rbox, curRow, curCol, false);	// ���Ƶ�ǰ����
        
        return true;
    }

    //------------ end of assistant methods for game control ---------------
    
    //------------ methods for game control --------------------------------
    // process input
    public void processInput(int keyCode){	// �����û�������Ϣ
        if(!enabled && !gameStart){
            return;
        }
        int i;
        for(i = 0; i < keyMap.length; i++){	// �����û��İ����б���ȡ�û�������Ӧ������
            if(keyMap[i] == keyCode){
                break;
            }
        }
        if(i == keyMap.length){	// ���û���ҵ�ָ����������ð�����Ϣ���Ƿ�����ǰ�û��ģ�����
            return;
        }

        switch(i){
            case LEFT_KEY_INDEX:	// �û������ˡ����ơ���
                moveBoxLeft();	
                break;
            case RIGHT_KEY_INDEX:	// �û������ˡ����ơ���
                moveBoxRight();
                break;
            case DOWN_KEY_INDEX:	// �û������ˡ��������ơ���
                moveBoxDownFast();
                break;
            case ROTATE_KEY_INDEX:	// �û���������ת��
                rotateBox();
                break;
        }
    }
    
    // start new game
    public void newGame(){	// �¿�һ����Ϸ
        emptyBucket();	// �����Ϸ������
        rbox.randBox();	// ������õ�ǰ����
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
    public void endGame(){	// ��ֹ��Ϸ
        emptyBucket();	// �����Ϸ������
        showBox = false;
        enabled = false;
        gameStart = false;
        showGameOver = false;
        scorePad.setScoreVisible(false);	// ����ʾ����
        preview.closePreview();
        bucket.repaint();
    }
    
    
    // drop the russian box in the bucket by one row
    public void drop(){	// ����ǰ��������һ��
        if(!enabled || !gameStart){	// �����ǰ��Ҳ�������Ϸ������Ϸ��δ��ʼ�����˳�
            return;
        }
        if(canBePlacedInBucket(rbox, curRow + 1, curCol)){	// �����ǰ����������һ��������һ�񲢷���
            moveBoxDownOneRow();
            return;
        }
        // �����ǰ���鲻������һ����÷����Ѿ�����׶�
        showBox = false;
        enabled = false;	// �ڴ�������в��ܽ����û�����
        placeBoxInBucket();	// ����ǰ���������Ϸ������

        int row, count;
        int[] fullRows = new int[4];
        count = 0;
        for(row = curRow + 3; row >= curRow; row--){	// ����������
            if(isFullRow(row)){
                fullRows[count] = row;
                count++;
            }
        }

        if(count > 0){	// ���������һ������
            bucket.flashFullRows(fullRows, count, FLASH_TIMES); // ������������
            eraseFullRows(fullRows, count);	// �����������
            bucketTop += count;	// �޸Ķѻ�������������
            if(count > 1){	// �����������һ�����������Ҽ�����
                action.fullLineAction(this, count);
            }
        }
        
        score += calculateScore(rbox, count);	// �������
        scorePad.setScore(score);	// ���·�������
        
        if(reachTop()){	// ����ѻ������ѵ��ﶥ��������Ϸ����
            gameStart = false;
            showGameOver = true;
            preview.closePreview();
            bucket.repaint();
            action.endGameAction(this);
        }else{
            resetBox();	// ���򽫵�ǰ��������Ϊ��һ������
        }
    }
    
    // thrush lines into the bottom of the bucket
    public void thrushLines(int c){	// ����Ϸ�����м���c��
        if(!gameStart || !enabled || c <= 0){
            return;
        }
        int bottom = getBottomRow(rbox, curRow, curCol);
        int i;
        for(i = Math.max(bucketTop, c); i < ROW_COUNT + 4; i++){	// ���ѻ��ķ�������c��
            copyRow(i, i - c);
        }
        
        Random r = new Random();
        int boxType = RussianBox.getBoxTypeCount();
        for(i = Math.max(ROW_COUNT + 4 - c, 0); i < ROW_COUNT + 4; i++){	// ����Ϸ������ײ�����ļ���c��
            for(int j = 0; j < COL_COUNT; j++){
                fullMatrix[i][j] = (r.nextInt(2) == 0);
                if(fullMatrix[i][j]){
                    imgMatrix[i][j] = r.nextInt(boxType + 1);
                }else{
                    imgMatrix[i][j] = -1;
                }
            }
        }
        
        if(curRow > bottom - c){	// �����ǰ���鲻�ܷ����ڵ�ǰλ�ã��������Ƶ����ʵ�λ��
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
        bucket.repaint();	// �ػ���Ϸ������
        
        if(reachTop()){	// ����ѻ������ѵ��ﶥ�ˣ�����Ϸ����
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
    private class BoxBucket extends JComponent {	// ��Ϸ������
        public static final int BUCKET_WIDTH = COL_COUNT * RussianBox.BOX_WIDTH + 2;	// ��������
        public static final int BUCKET_HEIGHT = ROW_COUNT * RussianBox.BOX_WIDTH + 2;	// �߶�
        public static final int FLASH_INTERVAL = 100;		// ��˸���
        private Insets insets;
        private Rectangle bounds;
    
        /** Creates a new instance of BoxBucket */
        public BoxBucket() {	// ������Ϸ������
            setOpaque(true);
            setFocusable(false);	// �ö����ܽ��ܼ�������
            setPreferredSize(new Dimension(BUCKET_WIDTH + 2, BUCKET_HEIGHT + 2));	// ���ô�С
            setBorder(BorderFactory.createLineBorder(Color.BLACK));	// ���ñ߽�
            insets = getInsets();
            bounds = new Rectangle(insets.left, insets.top, BUCKET_WIDTH, BUCKET_HEIGHT);	// ����������߽�
            showGameOver = false;
        }
    
        // get the given row's top
        public int getRowTop(int row){	// ���ָ���е�������
            return insets.top + (row - 4) * RussianBox.BOX_WIDTH;
        }
    
        // get the given coloumn's left
        public int getColLeft(int col){	// ���ָ���еĺ�����
            return insets.left + col * RussianBox.BOX_WIDTH;
        }
    
        // draw the given row
        private void drawRow(Graphics g, int row, boolean useBackground){	// ��ָ�����У�useBackground����ָ���Ƿ��ñ�����ɫ��
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
        public void paintBox(RussianBox r, int row, int col, boolean erase){	// ��ָ���к��л�����
          if(!erase) {
          	r.draw(getGraphics(), getColLeft(col), getRowTop(row), bounds);
          } else {
          	r.draw(getGraphics(), getColLeft(col), getRowTop(row), BUCKET_BACK, bounds);
          }
        }
        
        public void paintComponent(Graphics g){	// ����Ҫ�ػ洰��ʱ���ø÷���
            Color oldColor = g.getColor();
            g.setColor(BUCKET_BACK);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);	// �ñ���ɫ�����ǰ����
            g.setColor(oldColor);
            for(int i = 4; i < fullMatrix.length; i++){	// ����������
                drawRow(g, i, false);	// ���Ƶ�i��
            }
            if(showBox){
                rbox.draw(g, getColLeft(curCol), getRowTop(curRow), bounds);	// ���Ƶ�ǰ����
            }
            if(showGameOver){		// ������Ϸ������Ϣ
                String s = "Game Over !!!";
                Font oldFont = g.getFont();
                g.setFont(oldFont.deriveFont(Font.BOLD, 24));
                g.setColor(new Color(200, 0, 0));
                g.drawString(s, 35, 170);
                g.setFont(oldFont);
            }
        }
        
        // flash full rows
        public void flashFullRows(int[] fullRows, int fullRowCount, int flashTimes){	// ��˸ָ�����У�flashTimes�ƶ���˸�Ĵ���
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
