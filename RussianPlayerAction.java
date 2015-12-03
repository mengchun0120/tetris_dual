/*
 * RussianPlayerAction.java
 *
 * Created on 2004年12月13日, 下午3:03
 */

/**
 *
 * @author  Administrator
 */
// endGameAction is performed when the player has ended his game
// fullLineAction when the player has filled full lines
public interface RussianPlayerAction {
    public void endGameAction(Player player);
    public void fullLineAction(Player player, int fullLineCount);
}
