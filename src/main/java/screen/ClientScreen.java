package screen;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.awt.event.KeyEvent;
import java.io.IOException;

import asciiPanel.AsciiPanel;
import network.Client;
import network.WriteClient;
import world.*;

public class ClientScreen extends PlayScreen {
    private Client client;
    private WriteClient wclient;
    private int id;

    public ClientScreen(int id) throws IOException, InterruptedException {
        super(1);
        enemyCount = 1;
        this.screenWidth = 50;
        this.screenHeight = 50;
        worldBuilder = new WorldBuilder(this.screenWidth, this.screenHeight);
        this.world = worldBuilder.buildWall().build();
        this.messages = new ArrayList<String>();
        this.oldMessages = new ArrayList<String>();

        this.id = id;
        net = true;
        client = new Client(this);
        exec = Executors.newCachedThreadPool();
        exec.execute(client);
        exec.shutdown();

        wclient = new WriteClient(this);
    }

    public void handle(String in) {
        String[] lines = in.split("\n");

        screenWidth = 50;
        screenHeight = 50;
        worldBuilder = new WorldBuilder(this.screenWidth, this.screenHeight);
        this.world = worldBuilder.buildWall().build();
        this.messages = new ArrayList<String>();
        this.oldMessages = new ArrayList<String>();
        CreatureFactory creatureFactory = new CreatureFactory(world);
        enemyCount = 1;

        for (String line : lines) {
            if (line == "")
                break;
            String[] arr = line.split(" ");
            switch (Integer.parseInt(arr[0])) {
                case 0: // player
                    // System.out.println(arr[0] + " " + arr[1] + " " + arr[2]);
                    Player playerx = creatureFactory.newPlayerAtLocation(screen, messages, Integer.parseInt(arr[1]),
                            Integer.parseInt(arr[2]), Integer.parseInt(arr[5]));
                    playerx.setHp(Integer.parseInt(arr[3]));
                    playerx.setMoney(Integer.parseInt(arr[4]));
                    if (playerx.id() == id) {
                        player = playerx;
                    }
                    break;
                case 1: // enemy
                    Enemy enemy = new Enemy(this.world, (char) 2, AsciiPanel.red, 5, 2, 0, 9, null);
                    enemy.setHp(Integer.parseInt(arr[3]));
                    world.addAtLocation(enemy, Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                    enemyCount++;
                    break;
                case 2: // coin
                    Coin coin = new Coin(this.world, (char) 36, AsciiPanel.yellow);
                    world.addAtLocation(coin, Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                    break;
                case 3: // ice
                    world.addAtLocation(new Block(this.world, (char) 176, AsciiPanel.cyan, 3), Integer.parseInt(arr[1]),
                            Integer.parseInt(arr[2]));
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
                    break;
                case 6: // bomb
                    Bomb bomb = new Bomb(world, (char) 9, AsciiPanel.magenta, 5);
                    bomb.setCount(Integer.parseInt(arr[3]));
                    world.addAtLocation(bomb, Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                    break;
                default:
                    break;
            }
        }
        enemyCount--;
    }

    public int id() {
        return id;
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        if (enemyCount == 0) {
            return new WinScreen();
        } else if (player != null && player.hp() <= 0) {
            return new LoseScreen();
        } else {
            try {
                wclient.action(key.getKeyCode());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        // Terrain and creatures
        displayTiles(terminal, 0, 0);
        // Player
        // terminal.write(player.glyph(), player.x() - getScrollX(), player.y() -
        // getScrollY(), player.color());
        // Stats
        terminal.write("Get Money and", 50, 0);
        terminal.write("Buy things here.", 50, 1);
        terminal.write("Bullet(WASD):$10", 50, 3);
        terminal.write("Bomb(SPACE): $50", 50, 4);
        terminal.write("1Life(H):    $20", 50, 5);
        terminal.write("----------------", 50, 7);
        terminal.write("Bullet can", 50, 8);
        terminal.write("break IceBlock", 50, 9);
        terminal.write("----------------", 50, 10);
        terminal.write("Bomb can", 50, 11);
        terminal.write("break Stone", 50, 12);
        terminal.write("---------------", 50, 13);
        terminal.write("Bullet can", 50, 14);
        terminal.write("Fire Bomb", 50, 15);
        // terminal.write("---------------", 50, 16);
        // terminal.write("Press F5 to save", 50, 17);
        String stats;
        if (player != null) {
            stats = String.format("Hp:%3d/%3d ", player.hp(), player.maxHP());
            terminal.write(stats, 50, 20);
            stats = String.format("Money:%3d ", player.money());
            terminal.write(stats, 50, 21);
        }
        stats = String.format("Left Enemy:%3d ", enemyCount);
        terminal.write(stats, 50, 25);
        if (enemyCount == 0) {
            terminal.write("You Win!", 50, 30);
            terminal.write("No Enemy Left!", 50, 31);
        }
        // else if (player.hp() <= 0) {
        // terminal.write("You Lose :(", 50, 30);
        // terminal.write("You have no Hp.", 50, 31);
        // }
        // Messages
        // displayMessages(terminal, this.messages);
    }
}