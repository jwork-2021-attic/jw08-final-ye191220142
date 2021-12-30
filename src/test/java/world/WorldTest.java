package world;

import org.junit.BeforeClass;
import org.junit.Test;

import asciiPanel.AsciiPanel;

import static org.junit.Assert.*;

public class WorldTest {
    private static World world;

    @BeforeClass
    public static void setUpBeforeClass() {
        world = new WorldBuilder(50, 50).buildWall().build();
    }

    @Test
    public void testTile(){
        Tile tile = Tile.WALL;
        assertEquals(tile, world.tile(0, 0));
        assertEquals((char)177, world.tile(0, 0).glyph());
        assertEquals(AsciiPanel.brightBlack, world.tile(0, 0).color());
        tile = Tile.FLOOR;
        assertEquals(tile, world.tile(25, 25));
        assertEquals((char)0, world.tile(25, 25).glyph());
        assertEquals(AsciiPanel.black, world.tile(25, 25).color());
        tile = Tile.BOUNDS;
        assertEquals(tile, world.tile(-1,-1));
        assertEquals('x', world.tile(-1, -1).glyph());
        assertEquals(AsciiPanel.magenta, world.tile(-1, -1).color());
    }

    @Test
    public void testArea(){
        assertEquals(50, world.width());
        assertEquals(50, world.height());
    }

    @Test
    public void testDig(){
        world.dig(1, 1);
        Tile tile = Tile.FLOOR;
        assertEquals(tile, world.tile(1, 1));
        world.dig(0, 0);
        tile = Tile.FLOOR;
        assertNotEquals(tile, world.tile(0, 0));
    }

    @Test
    public void testAdd(){
        Coin coin = new Coin(world, (char) 36, AsciiPanel.yellow);
        world.addAtLocation(coin, 5, 7);
        assertEquals(5, coin.x());
        assertEquals(7, coin.y());
        assertEquals(coin, world.creature(5, 7));
    }
}