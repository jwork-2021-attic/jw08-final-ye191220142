package world;

import org.junit.BeforeClass;
import org.junit.Test;

import asciiPanel.AsciiPanel;

import static org.junit.Assert.*;

public class CoinTest {
    private static World world;
    private static Coin coin;

    @BeforeClass
    public static void setUpBeforeClass(){
        world = new WorldBuilder(50, 50).buildWall().build();
        coin = new Coin(world, (char) 36, AsciiPanel.yellow);
    }

    @Test
    public void testPos(){
        world.addAtLocation(coin, 20, 10);
        assertEquals(20, coin.x());
        assertEquals(10, coin.y());
    }

    @Test
    public void testColor(){
        assertEquals(AsciiPanel.yellow, coin.color());
    }
}