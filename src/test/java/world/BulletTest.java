package world;

import org.junit.BeforeClass;
import org.junit.Test;

import asciiPanel.AsciiPanel;

import static org.junit.Assert.*;

public class BulletTest {
    private static World world;
    private static Bullet bullet;

    @BeforeClass
    public static void setUpBeforeClass(){
        world = new WorldBuilder(50, 50).buildWall().build();
        bullet = new Bullet(world, (char) 124, AsciiPanel.green, 1, 0);
    }

    @Test
    public void testPos(){
        world.addAtLocation(bullet, 20, 10);
        assertEquals(20, bullet.x());
        assertEquals(10, bullet.y());
    }

    @Test
    public void testColor(){
        assertEquals(AsciiPanel.green, bullet.color());
    }

    @Test
    public void testDir(){
        assertEquals(0, bullet.dir());
    }

    @Test
    public void testAttack(){
        Enemy enemy = new Enemy(world, (char) 2, AsciiPanel.red, 5, 2, 0, 9, null);
        world.addAtLocation(enemy, 21, 10);
        bullet.moveBy(1, 0);
        assertEquals(4, enemy.hp());
        world.addAtLocation(bullet, 20, 10);
    }
}