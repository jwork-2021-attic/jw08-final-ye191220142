package screen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;

import asciiPanel.AsciiPanel;

import java.awt.event.KeyEvent;

import network.Server;
import updater.CountTask;
import world.*;

public class ServerScreen extends PlayScreen {
    private Server server;
    private Player player0;
    private Player player1;
    private CreatureFactory creatureFactory;

    public ServerScreen() throws IOException {
        this.screenWidth = 50;
        this.screenHeight = 50;
        createWorld();
        this.messages = new ArrayList<String>();
        this.oldMessages = new ArrayList<String>();
        exec = Executors.newCachedThreadPool();

        enemyCount = 5;
        creatureFactory = new CreatureFactory(this.world);
        createBlocks(creatureFactory);
        // this.player = creatureFactory.newPlayer(screen, this.messages);
        this.player0 = creatureFactory.newPlayerAtLocation(screen, this.messages, 1, 1, 0);
        this.player1 = creatureFactory.newPlayerAtLocation(screen, this.messages, 2, 1, 1);
        for (int i = 0; i < enemyCount; i++) {
            Enemy enemy = creatureFactory.newEnemy(player);
            exec.execute(enemy);
        }
        exec.execute(creatureFactory);
        // exec.execute(new CountTask(world, this));
        exec.shutdown();
        server = new Server("localhost", 9093, this);
        server.startServer();
    }

    public void action(int id, String s) {
        if (id == 0) {

        }
    }

    @Override
    protected void createBlocks(CreatureFactory creatureFactory) {
        for (int i = 1; i < this.screenWidth - 2; i++) {
            for (int j = 1; j < this.screenHeight - 2; j++) {
                if (maze[i][j] == 1)
                    creatureFactory.newBlock(1, i + 1, j + 1);
                else if (maze[i][j] == 2)
                    creatureFactory.newBlock(2, i + 1, j + 1);
            }
        }
    }

    public void handle(String s) {
        String[] arr = s.split(" ");
        if (arr[0].equals("action")) {
            if (Integer.parseInt(arr[1]) == 0) {
                switch (Integer.parseInt(arr[2])) {
                    case KeyEvent.VK_LEFT:
                        player0.moveBy(-1, 0);
                        break;
                    case KeyEvent.VK_RIGHT:
                        player0.moveBy(1, 0);
                        break;
                    case KeyEvent.VK_UP:
                        player0.moveBy(0, -1);
                        break;
                    case KeyEvent.VK_DOWN:
                        player0.moveBy(0, 1);
                        break;
                    case KeyEvent.VK_H:
                        player0.heal(1);
                        break;
                    case KeyEvent.VK_SPACE:
                        player0.setBomb();
                        break;
                    case KeyEvent.VK_W:
                        player0.shoot(0);
                        break;
                    case KeyEvent.VK_A:
                        player0.shoot(1);
                        break;
                    case KeyEvent.VK_S:
                        player0.shoot(2);
                        break;
                    case KeyEvent.VK_D:
                        player0.shoot(3);
                        break;
                    default:
                        break;
                }
            } else {
                switch (Integer.parseInt(arr[2])) {
                    case KeyEvent.VK_LEFT:
                        player1.moveBy(-1, 0);
                        break;
                    case KeyEvent.VK_RIGHT:
                        player1.moveBy(1, 0);
                        break;
                    case KeyEvent.VK_UP:
                        player1.moveBy(0, -1);
                        break;
                    case KeyEvent.VK_DOWN:
                        player1.moveBy(0, 1);
                        break;
                    case KeyEvent.VK_H:
                        player1.heal(1);
                        break;
                    case KeyEvent.VK_SPACE:
                        player1.setBomb();
                        break;
                    case KeyEvent.VK_W:
                        player1.shoot(0);
                        break;
                    case KeyEvent.VK_A:
                        player1.shoot(1);
                        break;
                    case KeyEvent.VK_S:
                        player1.shoot(2);
                        break;
                    case KeyEvent.VK_D:
                        player1.shoot(3);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        exec.execute(creatureFactory);
        for (int i = 0; i < enemyCount; i++) {
            Enemy enemy = creatureFactory.newEnemy(player);
            exec.execute(enemy);
        }
        exec.execute(new CountTask(world, this));

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
        // String stats = String.format("Hp:%3d/%3d ", player.hp(), player.maxHP());
        // terminal.write(stats, 50, 20);
        // stats = String.format("Money:%3d ", player.money());
        // terminal.write(stats, 50, 21);
        String stats = String.format("Left Enemy:%3d ", enemyCount);
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