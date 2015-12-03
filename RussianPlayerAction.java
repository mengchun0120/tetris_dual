/*
 * RussianPlayerAction.java
 *
 * Created on 2004��12��13��, ����3:03
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
