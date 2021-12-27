/*
 * Copyright (C) 2015 Aeranythe Echosong
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package screen;

import world.*;
import asciiPanel.AsciiPanel;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.print.DocFlavor.STRING;
import javax.swing.JFrame;

import updater.CountTask;

/**
 *
 * @author Aeranythe Echosong
 */
public class PlayScreen implements Screen {

    protected World world;
    protected WorldBuilder worldBuilder;
    protected int[][] maze; // 用来dfs
    protected Player player;
    protected int screenWidth;
    protected int screenHeight;
    protected List<String> messages;
    protected List<String> oldMessages;
    protected Screen screen;
    protected int enemyCount;
    protected ExecutorService exec;

    public PlayScreen() {
        this.screenWidth = 50;
        this.screenHeight = 50;
        createWorld();
        this.messages = new ArrayList<String>();
        this.oldMessages = new ArrayList<String>();
        exec = Executors.newCachedThreadPool();

        enemyCount = 5;
        CreatureFactory creatureFactory = new CreatureFactory(this.world);
        createBlocks(creatureFactory);
        createCreatures(creatureFactory);
    }

    public PlayScreen(int i) {
        // 因为子类会默认调用父类无参构造函数，所以需要写一个带参构造函数并显示调用，以防止读取屏幕时会创建一个新建屏幕
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    private void createBlocks(CreatureFactory creatureFactory) {
        for (int i = 0; i < this.screenWidth - 2; i++) {
            for (int j = 0; j < this.screenHeight - 2; j++) {
                if (maze[i][j] == 1)
                    creatureFactory.newBlock(1, i + 1, j + 1);
                else if (maze[i][j] == 2)
                    creatureFactory.newBlock(2, i + 1, j + 1);
            }
        }
    }

    private void createCreatures(CreatureFactory creatureFactory) {
        this.player = creatureFactory.newPlayer(screen, this.messages);
        for (int i = 0; i < 100; i++) {
            // creatureFactory.newCoin();
        }
        for (int i = 0; i < enemyCount; i++) {
            Enemy enemy = creatureFactory.newEnemy(player);
            exec.execute(enemy);
        }
        exec.execute(creatureFactory);
        // CountTask counttask = new CountTask(world, enemyCount);
        exec.execute(new CountTask(world, this));
        exec.shutdown();
    }

    private void createWorld() {
        // world = new WorldBuilder(60, 60).makeCaves().build();
        worldBuilder = new WorldBuilder(this.screenWidth, this.screenHeight);
        world = worldBuilder.buildMaze().build();
        maze = worldBuilder.getMaze(); // 0是路，1是木箱，2是石头
    }

    private void clear() {
        for (int[] row : maze)
            for (int x : row)
                if (x > 1)
                    x = 1;
    }

    private void displayTiles(AsciiPanel terminal, int left, int top) {
        // Show terrain
        for (int x = 0; x < screenWidth; x++) {
            for (int y = 0; y < screenHeight; y++) {
                int wx = x + left;
                int wy = y + top;

                // if (player.canSee(wx, wy)) {
                terminal.write(world.glyph(wx, wy), x, y, world.color(wx, wy));
                // } else {
                // terminal.write(world.glyph(wx, wy), x, y, Color.DARK_GRAY);
                // }
            }
        }
        // Show creatures
        for (Creature creature : world.getCreatures()) {
            if (creature.x() >= left && creature.x() < left + screenWidth && creature.y() >= top
                    && creature.y() < top + screenHeight) {
                // if (player.canSee(creature.x(), creature.y())) {
                terminal.write(creature.glyph(), creature.x() - left, creature.y() - top, creature.color());
                // }
            }
        }
        // Creatures can choose their next action now
        world.update();
    }

    private void displayMessages(AsciiPanel terminal, List<String> messages) {
        int top = this.screenHeight - messages.size();
        for (int i = 0; i < messages.size(); i++) {
            terminal.write(messages.get(i), 1, top + i + 1);
        }
        this.oldMessages.addAll(messages);
        messages.clear();
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        // Terrain and creatures
        displayTiles(terminal, getScrollX(), getScrollY());
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
        terminal.write("---------------", 50, 16);
        terminal.write("Press F5 to save", 50, 17);
        String stats = String.format("Hp:%3d/%3d ", player.hp(), player.maxHP());
        terminal.write(stats, 50, 20);
        stats = String.format("Money:%3d ", player.money());
        terminal.write(stats, 50, 21);
        stats = String.format("Left Enemy:%3d ", enemyCount);
        terminal.write(stats, 50, 25);
        if (enemyCount == 0) {
            terminal.write("You Win!", 50, 30);
            terminal.write("No Enemy Left!", 50, 31);
        } else if (player.hp() <= 0) {
            terminal.write("You Lose :(", 50, 30);
            terminal.write("You have no Hp.", 50, 31);
        }
        // Messages
        // displayMessages(terminal, this.messages);
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

    public void save() {
        String fileName = "saves/save.txt";
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            out.write(String.valueOf(screenWidth) + " " + screenHeight);
            out.newLine(); // 注意\n不一定在各种计算机上都能产生换行的效果
            for (Creature creature : world.getCreatures()) {
                switch (creature.type()) {
                    case 0: // player
                        out.write(String.valueOf(creature.type()) + " " + creature.x() + " " + creature.y() + " ");
                        out.write(String.valueOf(creature.hp()) + " " + String.valueOf(creature.money()));
                        break;
                    case 1: // enemy
                        out.write(String.valueOf(creature.type()) + " " + creature.x() + " " + creature.y() + " ");
                        out.write(String.valueOf(creature.hp()));
                        break;
                    case 2: // coin
                        out.write(String.valueOf(creature.type()) + " " + creature.x() + " " + creature.y());
                        break;
                    case 3: // ice
                        out.write(String.valueOf(creature.type()) + " " + creature.x() + " " + creature.y());
                        break;
                    case 4: // stone
                        out.write(String.valueOf(creature.type()) + " " + creature.x() + " " + creature.y());
                        break;
                    case 5: // bullet
                        out.write(String.valueOf(creature.type()) + " " + creature.x() + " " + creature.y() + " ");
                        Bullet bullet = (Bullet) creature;
                        out.write(String.valueOf(bullet.dir()));
                        break;
                    case 6: // bomb
                        out.write(String.valueOf(creature.type()) + " " + creature.x() + " " + creature.y() + " ");
                        out.write(String.valueOf(creature.count()));
                        break;
                }
                out.newLine();
            }
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void checkEnemyCount(int n) {
        this.enemyCount = n;
    }

    public int getScrollX() {
        return Math.max(0, Math.min(player.x() - screenWidth / 2, world.width() - screenWidth));
    }

    public int getScrollY() {
        return Math.max(0, Math.min(player.y() - screenHeight / 2, world.height() - screenHeight));
    }

}
