package world;

import org.junit.BeforeClass;
import org.junit.Test;

import asciiPanel.AsciiPanel;

import static org.junit.Assert.*;

public class BlockTest {
    private static World world;
    private static Block block;

    @BeforeClass
    public static void setUpBeforeClass(){
        world = new WorldBuilder(50, 50).buildWall().build();
        block = new Block(world, (char) 176, AsciiPanel.cyan, 3);
    }

    @Test
    public void testPos(){
        world.addAtLocation(block, 20, 10);
        assertEquals(20, block.x());
        assertEquals(10, block.y());
    }

    @Test
    public void testColor(){
        assertEquals(AsciiPanel.cyan, block.color());
        assertNotEquals(AsciiPanel.yellow, block.color());
    }

    @Test
    public void testType(){
        assertEquals(3, block.type());
    }

    @Test
    public void testHard(){
        Bullet bullet = new Bullet(world, (char) 124, AsciiPanel.green, 1, 1);
        world.addAtLocation(bullet, 21, 10);
        bullet.run();
        assertEquals(null, world.creature(20, 10));
    }
}