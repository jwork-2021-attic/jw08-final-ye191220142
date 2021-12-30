package world;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class CreatureFactoryTest {
    private static World world;
    private static CreatureFactory creaturefactory;

    @BeforeClass
    public static void setUpBeforeClass(){
        world = new WorldBuilder(50, 50).buildWall().build();
        creaturefactory = new CreatureFactory(world);
    }

    @Test
    public void testAdd(){
        Player player = creaturefactory.newPlayerAtLocation(null, null, 1, 2, 3);
        assertEquals(1, player.x());
        assertEquals(2, player.y());
        assertEquals(3, player.id());
    }

    @Test
    public void testAddRandomly(){
        creaturefactory.newPlayer(null, null);
        boolean b= false;
        for (Creature creature : world.getCreatures()){
            if(creature.type() == 0){
                b = true;
            }
        }
        assertEquals(true, b);
    }
}