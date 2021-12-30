package world;

import org.junit.BeforeClass;
import org.junit.Test;

import asciiPanel.AsciiPanel;

import static org.junit.Assert.*;

public class BombTest {
    private static World world;
    private static Bomb bomb;

    @BeforeClass
    public static void setUpBeforeClass(){
        world = new WorldBuilder(50, 50).buildWall().build();
        bomb = new Bomb(world, (char) 124, AsciiPanel.green, 5);
    }

    @Test
    public void testPos(){
        world.addAtLocation(bomb, 20, 10);
        assertEquals(20, bomb.x());
        assertEquals(10, bomb.y());
    }

    @Test
    public void testColor(){
        assertEquals(AsciiPanel.green, bomb.color());
    }

    @Test
    public void testAttack(){
        Enemy enemy = new Enemy(world, (char) 2, AsciiPanel.red, 5, 2, 0, 9, null);
        world.addAtLocation(enemy, 21, 10);
        bomb.run();
        assertEquals(0, enemy.hp());
        world.addAtLocation(bomb, 20, 10);
    }
}