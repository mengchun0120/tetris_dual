/*
 * Preview.java
 *
 * Created on 2004��11��19��, ����2:38
 */

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author  Administrator
 */
public class Preview extends JComponent {
    public static final Color BACKGROUND = Color.WHITE;				// ������ɫ
    public static final int WIDTH = 4 * RussianBox.BOX_WIDTH + 4;	// ���ڿ��
    public static final int HEIGHT = 4 * RussianBox.BOX_WIDTH + 4;	// �߶�
    private RussianBox rbox;	// �������ķ���
    private int nOrgX, nOrgY;	// ���Ʒ��������
    private Rectangle bounds;	// �߽�
    private boolean showPreview, opened;
    
    /** Creates a new instance of Preview */
    public Preview() {	// ����Ԥ������
        setOpaque(true);
        setFocusable(false);	// Ԥ�����ڲ����ܰ�����Ϣ
        setPreferredSize(new Dimension(WIDTH, HEIGHT));	// ���ô��ڴ�С
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));	// ���ô��ڱ߽�
        Insets insets = getInsets();
        nOrgX = insets.left;
        nOrgY = insets.top;
        bounds = new Rectangle(insets.left, insets.top, WIDTH - 2, HEIGHT - 2);	// ���û�ͼ�߽�
        rbox = new RussianBox();	// ������ɷ���
        showPreview = true;	// ��ʼ����£���ʾԤ��
        opened = false;
    }
    
    // draw preview
    protected void drawPreview(Graphics g){	// ����Ԥ������
        Color oldColor = g.getColor();
        g.setColor(BACKGROUND);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);	// �����ǰ����
        g.setColor(oldColor);
        if(showPreview && opened){	// ���������ʾԤ������Ԥ���Ѵ�����ʾ��һ������
            rbox.draw(g, nOrgX, nOrgY, bounds);
        }
    }
    
    // open the preivew
    public void openPreview(){	// ��Ԥ��
        opened = true;
        drawPreview(getGraphics());
    }
    
    // close the preview
    public void closePreview(){	// �ر�Ԥ��
        opened = false;
        drawPreview(getGraphics());
    }
    
    // reset the russian box for preview
    public void resetBox(){	// ������һ������
        rbox.randBox();
        drawPreview(getGraphics());
    }
    
    // make the sussian box's visibility
    public void setPreviewVisible(boolean visible){	// �����Ƿ��ܹ���ʾ��һ������
        showPreview = visible;
        drawPreview(getGraphics());
    }
    
    // check whether the preview is visible
    public boolean isPreviewVisible(){	// ������һ�������ܷ���ʾ
        return showPreview;
    }
    
    // get the russian box
    public RussianBox getBox(){	// ������һ������
        return new RussianBox(rbox);
    }
    
    public void paintComponent(Graphics g){		// ����Ҫ���»��ƴ���ʱ���ø÷���
        drawPreview(g);
    }
}
