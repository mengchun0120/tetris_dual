/*
 * ScorePad.java
 *
 * Created on 2004年11月19日, 下午3:06
 */

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author  Administrator
 */
public class ScorePad extends JComponent {
    public static final int MAX_DIGITS = 8;				// 能够显示得最多位数
    public static final int DIGIT_WIDTH = 13;			// 每一位的宽度
    public static final int DIGIT_HEIGHT = 20;		// 每一位的高度
    public static final Color LIGHT = new Color(255, 0, 0);		// 亮色
    public static final Color DARK = new Color(128, 0, 0);		// 暗色
    public static final Color BACKGROUND = Color.BLACK;				// 背景色
    public static final int[][] lines = {			// 每条线的相对坐标
                    {2, 1, 10, 1}, {1, 2, 1, 8}, {11, 2, 11, 8},
                    {2, 9, 10, 9}, {1, 10, 1, 17}, {2, 18, 10, 18},
                    {11, 10, 11, 17}};
    public static final int[] digitLines = {	// 每个数字对应的线图
                    0x77, 0x11, 0x5e, 0x5b, 0x39, 0x6b, 0x6f, 0x51, 0x7f, 0x7b, 0x00};
    protected long score;
    protected boolean showScore;
                    
    /** Creates a new instance of ScorePad */
    public ScorePad() {		// 生成分数窗口
        setOpaque(true);
        setFocusable(false);
        setPreferredSize(new Dimension(MAX_DIGITS * DIGIT_WIDTH + 2, DIGIT_HEIGHT + 2));	// 设置分数窗口大小
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        score = 0;		// 当前分数为0
        showScore = false;	// 当前不显示分数
    }
    
    // draw a digit
    protected static void drawDigit(Graphics g, int x, int y, int digit){	// 在指定坐标绘制数字
        Color oldColor = g.getColor();
        int map = digitLines[digit];	// 获取数字的线图
        int mask = 0x40;	// mask用于读取线图中的指定位
        for(int i = 0; i < 7; i++){	// 绘制数字
            g.setColor(((map & mask) != 0) ? LIGHT : DARK);
            g.drawLine(x + lines[i][0], y + lines[i][1], x + lines[i][2], y + lines[i][3]);
            mask >>= 1;
        }
        g.setColor(oldColor);
    }
    
    // set score
    public void setScore(long newScore){	// 设置分数
        score = newScore;
        paintScore(getGraphics());	// 重绘窗口
    }

    // set the score's visibility
    public void setScoreVisible(boolean visible){	// 设置分数是否可见
        showScore = visible;
        paintScore(getGraphics());
    }
    
    // get the score
    public long getScore(){	// 返回当前分数
        return score;
    }
    
    // check whether the score can be painted
    public boolean isScoreVisible(){	// 返回当前分数是否可见
        return showScore;
    }
    
    // paint the score
    protected void paintScore(Graphics g){	// 绘制分数
        Color oldColor = g.getColor();
        g.setColor(BACKGROUND);
        g.fillRect(1, 1, MAX_DIGITS * DIGIT_WIDTH, DIGIT_HEIGHT);	// 清除当前分数
        g.setColor(oldColor);
        
        int x = 1 + (MAX_DIGITS - 1) * DIGIT_WIDTH;
        int y = 1;
        long s = score, d;
        for(int i = 0; i < MAX_DIGITS; i++){
            if(showScore){
                if(i == 0 || ((i > 0) && (s > 0))){
                    drawDigit(g, x, y, (int)(s % 10));
                    s /= 10;
                }else{
                    drawDigit(g, x, y, 10);
                }
            }else{
                drawDigit(g, x, y, 10);
            }
            x -= DIGIT_WIDTH;
        }
    }
    
    public void paintComponent(Graphics g){
        paintScore(g);
    }
}
