/*
 * RussianBox.java
 *
 * Created on 2004年11月12日, 下午2:22
 */

import java.awt.*;
import javax.swing.*;
import java.util.Random;

/**
 *
 * @author  Administrator
 */
public class RussianBox {
    public static final int BOX_WIDTH = 30;			// 方块宽度
    public static final int BOXTYPE_COUNT = 11;	// 方块类型数
    public static final int EASY_LEVEL = 6;	// 游戏难度——容易
    public static final int MID_LEVEL = 8;	// 游戏难度——中等
    public static final int HARD_LEVEL = 10;	// 游戏难度——困难
    public static final int IMGCLASS_COUNT = 1;	// 图片类别数
    public static final String[][] imgurl = {
    	{"1-1.jpg", "1-2.jpg", "1-3.jpg", "1-4.jpg", "1-5.jpg", "1-6.jpg", 
    		"1-7.jpg", "1-8.jpg", "1-9.jpg", "1-10.jpg", "1-11.jpg"}
    };
    public static final ImageIcon[][] img = new ImageIcon[IMGCLASS_COUNT][BOXTYPE_COUNT];
    public static final int[][] rbox = {	// 方块位图
                    {0x8888, 0x000f, 0x8888, 0x000f},
                    {0x004e, 0x04c4, 0x00e4, 0x08c8},
                    {0x00cc, 0x00cc, 0x00cc, 0x00cc},
                    {0x08c4, 0x006c, 0x08c4, 0x006c},
                    {0x04c8, 0x00c6, 0x04c8, 0x00c6},
                    {0x0c88, 0x008e, 0x044c, 0x00e2},
                    {0x0c44, 0x00e8, 0x088c, 0x002e},
                    {0x00ae, 0x0c4c, 0x00ea, 0x0c8c},
                    {0x04e4, 0x04e4, 0x04e4, 0x04e4},
                    {0x0aea, 0x0e4e, 0x0aea, 0x0e4e},
                    {0x0eae, 0x0eae, 0x0eae, 0x0eae}};
                    
    protected static int maxBoxType = EASY_LEVEL;	// 方块类型最大值缺省为容易
    protected static int curImgClass = 0;			// 当前图片类型
    private int boxType;			// 方块类型
    private int rotateIndex;	// 旋转索引
    
    // create a russian box with random box type and rotate index
    public RussianBox() {	// 随机的生成一个方块
        boxType = (int)(Math.random() * (maxBoxType + 1));
        rotateIndex = (int)(Math.random() * 4);
    }
    
    public RussianBox(RussianBox r){	// 生成一个和r一样的方块
        setBox(r.getBoxType(), r.getRotateIndex());
    }
    
    // create a russion box with the specified box type and rotate index
    public RussianBox(int type, int rotate){	// 生成指定类型和旋转索引的方块
        setBox(type, rotate);
    }
    
    // get the box type
    public int getBoxType(){	// 返回方块类型
        return boxType;
    }
    
    // get the rotate index
    public int getRotateIndex(){	// 返回方块的旋转索引
        return rotateIndex;
    }
    
    // get the box's bitmap
    public int getBitmap(){	// 返回方块的位图
        return rbox[boxType][rotateIndex];
    }
    
    // get the box's image
    public ImageIcon getImg() {
    	return img[curImgClass][boxType];
    }
    
    // set the box's type and rotate index
    public void setBox(int newType, int newRotate){	// 设置方块的类型和旋转索引
        if(newType >= 0 && newType <= maxBoxType){
            boxType = newType;
        }else if(newType < 0){
            boxType = 0;
        }else{
            boxType = maxBoxType;
        }
        
        if(newRotate >= 0 && newRotate < 4){
            rotateIndex = newRotate;
        }else if(newRotate < 0){
            rotateIndex = 0;
        }else{
            rotateIndex = 3;
        }
    }
    
    // set the box using another russian box
    public void setBox(RussianBox r){	// 将当前方块设置为r
        setBox(r.getBoxType(), r.getRotateIndex());
    }
    
    // rotate the box
    public void rotate(){	// 旋转方块
        rotateIndex = (rotateIndex + 1) % 4;
    }
    
    // randomize the box
    public void randBox(){	// 随机化方块
        boxType = (int)(Math.random() * (maxBoxType + 1));
        rotateIndex = (int)(Math.random() * 4);
    }
    
    // get the box type count
    public static int getBoxTypeCount(){
        return maxBoxType;
    }
    
    // set the box type's upper bounds
    public static void setMaxBoxType(int type){	// 设置方块的最大类型数
        if(type >= 0 && type < BOXTYPE_COUNT){
            maxBoxType = type;
        }
    }
    
    // load the images
    public static void initImg() {
    	int i, j;
    	for(i = 0; i < IMGCLASS_COUNT; ++i) {
    		for(j = 0; j < BOXTYPE_COUNT; ++j) {
    			img[i][j] = GameFrame.createImageIcon(imgurl[i][j]);
    		}
    	}
    }
    
    public static void drawBox(Graphics g, int x, int y, Color c) {
    	Color oldColor = g.getColor();
    	g.setColor(c);
    	g.fillRect(x, y, BOX_WIDTH, BOX_WIDTH);
    	g.setColor(oldColor);
    }
    
    public static void drawImg(Graphics g, int x, int y, int imgIndex) {
    	g.drawImage(img[curImgClass][imgIndex].getImage(), x, y, 
    						BOX_WIDTH - 1, BOX_WIDTH - 1, null);
    }
    
    // get the top
    public int getTop(){	// 返回方块的顶端
        int i, j;
        int mask = 0x8000;
        int bitmap = getBitmap();
        
        for(i = 0; i < 4; i++){
            for(j = 0; j < 4; j++){
                if((bitmap & mask) != 0){
                    return i;
                }
                mask >>= 1;
            }
        }
        
        return i;
    }
    
    // paint the box in the rectangle
    public void draw(Graphics g, int x, int y, Rectangle bounds){	// 在指定边界范围内画方块
        int cx, cy, i, j;
        int mask = 0x8000;
        int bitmap = getBitmap();
        cy = y;
        for(i = 0; i < 4; i++){
            cx = x;
            for(j = 0; j < 4; j++){
                if((bitmap & mask) != 0 && bounds.contains(cx, cy)){
                	g.drawImage(img[curImgClass][boxType].getImage(), cx, cy, 
                						BOX_WIDTH - 1, BOX_WIDTH - 1, null);
                }
                mask >>= 1;
                cx += BOX_WIDTH;
            }
            cy += BOX_WIDTH;
        }
    }
    
    // use the specified color to paint the box
    public void draw(Graphics g, int x, int y, Color c, Rectangle bounds) {
        int cx, cy, i, j;
        int mask = 0x8000;
        int bitmap = getBitmap();
        Color oldColor = g.getColor();
				g.setColor(c);
        cy = y;
        for(i = 0; i < 4; i++){
            cx = x;
            for(j = 0; j < 4; j++){
                if((bitmap & mask) != 0 && bounds.contains(cx, cy)){
                	g.fillRect(cx, cy, BOX_WIDTH, BOX_WIDTH);
                }
                mask >>= 1;
                cx += BOX_WIDTH;
            }
            cy += BOX_WIDTH;
        }
        g.setColor(oldColor);	
    }
}
