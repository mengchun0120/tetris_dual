/*
 * ScorePad.java
 *
 * Created on 2004��11��19��, ����3:06
 */

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author  Administrator
 */
public class ScorePad extends JComponent {
    public static final int MAX_DIGITS = 8;				// �ܹ���ʾ�����λ��
    public static final int DIGIT_WIDTH = 13;			// ÿһλ�Ŀ��
    public static final int DIGIT_HEIGHT = 20;		// ÿһλ�ĸ߶�
    public static final Color LIGHT = new Color(255, 0, 0);		// ��ɫ
    public static final Color DARK = new Color(128, 0, 0);		// ��ɫ
    public static final Color BACKGROUND = Color.BLACK;				// ����ɫ
    public static final int[][] lines = {			// ÿ���ߵ��������
                    {2, 1, 10, 1}, {1, 2, 1, 8}, {11, 2, 11, 8},
                    {2, 9, 10, 9}, {1, 10, 1, 17}, {2, 18, 10, 18},
                    {11, 10, 11, 17}};
    public static final int[] digitLines = {	// ÿ�����ֶ�Ӧ����ͼ
                    0x77, 0x11, 0x5e, 0x5b, 0x39, 0x6b, 0x6f, 0x51, 0x7f, 0x7b, 0x00};
    protected long score;
    protected boolean showScore;
                    
    /** Creates a new instance of ScorePad */
    public ScorePad() {		// ���ɷ�������
        setOpaque(true);
        setFocusable(false);
        setPreferredSize(new Dimension(MAX_DIGITS * DIGIT_WIDTH + 2, DIGIT_HEIGHT + 2));	// ���÷������ڴ�С
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        score = 0;		// ��ǰ����Ϊ0
        showScore = false;	// ��ǰ����ʾ����
    }
    
    // draw a digit
    protected static void drawDigit(Graphics g, int x, int y, int digit){	// ��ָ�������������
        Color oldColor = g.getColor();
        int map = digitLines[digit];	// ��ȡ���ֵ���ͼ
        int mask = 0x40;	// mask���ڶ�ȡ��ͼ�е�ָ��λ
        for(int i = 0; i < 7; i++){	// ��������
            g.setColor(((map & mask) != 0) ? LIGHT : DARK);
            g.drawLine(x + lines[i][0], y + lines[i][1], x + lines[i][2], y + lines[i][3]);
            mask >>= 1;
        }
        g.setColor(oldColor);
    }
    
    // set score
    public void setScore(long newScore){	// ���÷���
        score = newScore;
        paintScore(getGraphics());	// �ػ洰��
    }

    // set the score's visibility
    public void setScoreVisible(boolean visible){	// ���÷����Ƿ�ɼ�
        showScore = visible;
        paintScore(getGraphics());
    }
    
    // get the score
    public long getScore(){	// ���ص�ǰ����
        return score;
    }
    
    // check whether the score can be painted
    public boolean isScoreVisible(){	// ���ص�ǰ�����Ƿ�ɼ�
        return showScore;
    }
    
    // paint the score
    protected void paintScore(Graphics g){	// ���Ʒ���
        Color oldColor = g.getColor();
        g.setColor(BACKGROUND);
        g.fillRect(1, 1, MAX_DIGITS * DIGIT_WIDTH, DIGIT_HEIGHT);	// �����ǰ����
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
