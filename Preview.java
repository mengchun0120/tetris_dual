/*
 * Preview.java
 *
 * Created on 2004年11月19日, 下午2:38
 */

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author  Administrator
 */
public class Preview extends JComponent {
    public static final Color BACKGROUND = Color.WHITE;				// 背景颜色
    public static final int WIDTH = 4 * RussianBox.BOX_WIDTH + 4;	// 窗口宽度
    public static final int HEIGHT = 4 * RussianBox.BOX_WIDTH + 4;	// 高度
    private RussianBox rbox;	// 所包含的方块
    private int nOrgX, nOrgY;	// 绘制方块的坐标
    private Rectangle bounds;	// 边界
    private boolean showPreview, opened;
    
    /** Creates a new instance of Preview */
    public Preview() {	// 生成预览窗口
        setOpaque(true);
        setFocusable(false);	// 预览窗口不接受按键消息
        setPreferredSize(new Dimension(WIDTH, HEIGHT));	// 设置窗口大小
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));	// 设置窗口边界
        Insets insets = getInsets();
        nOrgX = insets.left;
        nOrgY = insets.top;
        bounds = new Rectangle(insets.left, insets.top, WIDTH - 2, HEIGHT - 2);	// 设置绘图边界
        rbox = new RussianBox();	// 随机生成方块
        showPreview = true;	// 初始情况下，显示预览
        opened = false;
    }
    
    // draw preview
    protected void drawPreview(Graphics g){	// 绘制预览窗口
        Color oldColor = g.getColor();
        g.setColor(BACKGROUND);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);	// 清除当前窗口
        g.setColor(oldColor);
        if(showPreview && opened){	// 如果允许显示预览并且预览已打开则显示下一个方块
            rbox.draw(g, nOrgX, nOrgY, bounds);
        }
    }
    
    // open the preivew
    public void openPreview(){	// 打开预览
        opened = true;
        drawPreview(getGraphics());
    }
    
    // close the preview
    public void closePreview(){	// 关闭预览
        opened = false;
        drawPreview(getGraphics());
    }
    
    // reset the russian box for preview
    public void resetBox(){	// 重设下一个方块
        rbox.randBox();
        drawPreview(getGraphics());
    }
    
    // make the sussian box's visibility
    public void setPreviewVisible(boolean visible){	// 设置是否能够显示下一个方块
        showPreview = visible;
        drawPreview(getGraphics());
    }
    
    // check whether the preview is visible
    public boolean isPreviewVisible(){	// 返回下一个方块能否显示
        return showPreview;
    }
    
    // get the russian box
    public RussianBox getBox(){	// 返回下一个方块
        return new RussianBox(rbox);
    }
    
    public void paintComponent(Graphics g){		// 当需要重新绘制窗口时调用该方法
        drawPreview(g);
    }
}
