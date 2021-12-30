package screen;

import org.junit.BeforeClass;
import org.junit.Test;

import world.Creature;
import world.Player;
import world.Tile;

import static org.junit.Assert.*;

import java.awt.event.KeyEvent;

public class PlayScreenTest {
    private static PlayScreen ps;

    @BeforeClass
    public static void setUpBeforeClass() {
        ps = new PlayScreen();
    }

    @Test
    public void testArea() {
        assertEquals(50, ps.screenHeight());
        assertEquals(50, ps.screenWidth());
    }

    @Test
    public void testNet() {
        assertEquals(false, ps.net);
    }

    @Test
    public void testInitCount() {
        assertEquals(5, ps.enemyCount);
        ps.checkEnemyCount(3);
        assertEquals(3, ps.enemyCount);
        ps.checkEnemyCount(5);
    }

    @Test
    public void testScroll() {
        assertEquals(0, ps.getScrollX());
        assertEquals(0, ps.getScrollY());
    }

    @Test
    public void testPlayer() {
        Player player = null;
        for (Creature creature : ps.world().getCreatures()) {
            if (creature.type() == 0) {
                player = (Player) creature;
                break;
            }
        }
        assertNotEquals(null, player);
    }

    @Test
    public void testTile() {
        Tile tile = Tile.WALL;
        assertEquals(tile, ps.world.tile(0, 0));
        tile = Tile.FLOOR;
        assertEquals(tile, ps.world.tile(25, 25));
    }
}