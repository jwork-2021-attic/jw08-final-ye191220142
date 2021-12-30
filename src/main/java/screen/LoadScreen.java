package screen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.awt.event.KeyEvent;

import asciiPanel.AsciiPanel;
import updater.CountTask;
import world.*;

public class LoadScreen extends PlayScreen {

    public LoadScreen() {
        super(1);
        String fileName = "saves/save.txt";
        String line = "";

        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            line = in.readLine();
            if (line != null) {
                String[] arr = line.split("\\s+");
                screenWidth = Integer.parseInt(arr[0]);
                screenHeight = Integer.parseInt(arr[1]);
                worldBuilder = new WorldBuilder(this.screenWidth, this.screenHeight);
                world = worldBuilder.buildWall().build();
                this.messages = new ArrayList<String>();
                this.oldMessages = new ArrayList<String>();
            }

            exec = Executors.newCachedThreadPool();
            CreatureFactory creatureFactory = new CreatureFactory(this.world);

            enemyCount = 0;
            line = in.readLine();
            while (line != null) {
                // System.out.println(line);
                String[] arr = line.split("\\s+");
                switch (Integer.parseInt(arr[0])) {
                    case 0: // player
                        player = creatureFactory.newPlayerAtLocation(screen, messages, Integer.parseInt(arr[1]),
                                Integer.parseInt(arr[2]), 0);
                        player.setHp(Integer.parseInt(arr[3]));
                        player.setMoney(Integer.parseInt(arr[4]));
                        break;
                    case 1: // enemy
                        Enemy enemy = new Enemy(this.world, (char) 2, AsciiPanel.red, 5, 2, 0, 9, player);
                        enemy.setHp(Integer.parseInt(arr[3]));
                        world.addAtLocation(enemy, Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                        exec.execute(enemy);
                        enemyCount++;
                        break;
                    case 2: // coin
                        Coin coin = new Coin(this.world, (char) 36, AsciiPanel.yellow);
                        world.addAtLocation(coin, Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                        break;
                    case 3: // ice
                        world.addAtLocation(new Block(this.world, (char) 176, AsciiPanel.cyan, 3),
                                Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                        break;
                    case 4: // stone
                        world.addAtLocation(new Block(this.world, (char) 178, AsciiPanel.brightBlack, 4),
                                Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                        break;
                    case 5: // bullet
                        Bullet bullet;
                        if (Integer.parseInt(arr[3]) == 0 || Integer.parseInt(arr[3]) == 2) {
                            bullet = new Bullet(world, (char) 124, AsciiPanel.green, 1, Integer.parseInt(arr[3]));
                            world.addAtLocation(bullet, Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                        } else {
                            bullet = new Bullet(world, (char) 45, AsciiPanel.green, 1, Integer.parseInt(arr[3]));
                            world.addAtLocation(bullet, Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                        }
                        exec.execute(bullet);
                        break;
                    case 6: // bomb
                        Bomb bomb = new Bomb(world, (char) 9, AsciiPanel.magenta, 5);
                        bomb.setCount(Integer.parseInt(arr[3]));
                        world.addAtLocation(bomb, Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                        exec.execute(bomb);
                        break;
                    default:
                        break;
                }
                line = in.readLine();
            }
            exec.execute(creatureFactory);
            exec.execute(new CountTask(world, this));
            exec.shutdown();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        switch (key.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                player.moveBy(-1, 0);
                break;
            case KeyEvent.VK_RIGHT:
                player.moveBy(1, 0);
                break;
            case KeyEvent.VK_UP:
                player.moveBy(0, -1);
                break;
            case KeyEvent.VK_DOWN:
                player.moveBy(0, 1);
                break;
            case KeyEvent.VK_H:
                player.heal(1);
                break;
            case KeyEvent.VK_SPACE:
                player.setBomb();
                break;
            case KeyEvent.VK_W:
                player.shoot(0);
                break;
            case KeyEvent.VK_A:
                player.shoot(1);
                break;
            case KeyEvent.VK_S:
                player.shoot(2);
                break;
            case KeyEvent.VK_D:
                player.shoot(3);
                break;
            case KeyEvent.VK_F5:
                save();
                return new StartScreen();
        }
        if (enemyCount == 0)
            return new WinScreen();
        else if (player.hp() > 0)
            return this;
        else
            return new LoseScreen();
    }
}