package world;

import org.junit.BeforeClass;
import org.junit.Test;

import asciiPanel.AsciiPanel;

import static org.junit.Assert.*;

public class EnemyTest {
    private static World world;
    private static Enemy enemy;

    @BeforeClass
    public static void setUpBeforeClass(){
        world = new WorldBuilder(50, 50).buildMaze().build();
        enemy = new Enemy(world, (char) 2, AsciiPanel.red, 5, 2, 0, 9, null);
    }

    @Test
    public void testPos()
    {
        world.addAtLocation(enemy, 10, 20);
        assertEquals(10, enemy.x());
        assertEquals(20, enemy.y());
        enemy.moveBy(0, -1);
        assertEquals(10, enemy.x());
        assertEquals(19, enemy.y());
    }

    @Test
    public void testHp(){
        assertEquals(5, enemy.hp());
        enemy.modifyHP(-8);
        assertEquals(0, enemy.hp());
        enemy.modifyHP(20);
        assertEquals(5, enemy.hp());
    }
}