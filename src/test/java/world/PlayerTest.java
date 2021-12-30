package world;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.awt.Color;

public class PlayerTest {
    private static World world;
    private static Player player;

    @BeforeClass
    public static void setUpBeforeClass(){
        world = new WorldBuilder(50, 50).buildWall().build();
        player = new Player(null, world, (char) 1, Color.gray, 10, 0, 0, 9, null, 0);
    }

    @Test
    public void testPos()
    {
        world.addAtLocation(player, 10, 20);
        assertEquals(10, player.x());
        assertEquals(20, player.y());
        player.moveBy(1,0);
        assertEquals(11, player.x());
        assertEquals(20, player.y());
        player.moveBy(-1, 0);
    }

    @Test
    public void testId(){
        assertEquals(0, player.id());
    }

    @Test
    public void testHp(){
        assertEquals(10, player.hp());
        player.modifyHP(-8);
        assertEquals(2, player.hp());
        player.heal(2);
        assertEquals(4, player.hp());
        player.heal(10);
        assertEquals(10, player.hp());
    }

    @Test
    public void testMoney(){
        assertEquals(100, player.money());
        player.setMoney(200);
        assertEquals(200, player.money());
    }

    @Test
    public void testShoot(){
        player.shoot(0);
        player.moveBy(1, 0);
        assertEquals(5, world.creature(10, 20).type());
    }
}