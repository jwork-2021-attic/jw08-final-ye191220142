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

import java.awt.event.KeyEvent;
import java.io.IOException;

import asciiPanel.AsciiPanel;

/**
 *
 * @author Aeranythe Echosong
 */
public abstract class RestartScreen implements Screen {

    protected Screen screen;

    @Override
    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    @Override
    public abstract void displayOutput(AsciiPanel terminal);

    @Override
    public Screen respondToUserInput(KeyEvent key) throws IOException {
        switch (key.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                return new PlayScreen();
            case KeyEvent.VK_1:
                return new PlayScreen();
            case KeyEvent.VK_NUMPAD1:
                return new PlayScreen();
            case KeyEvent.VK_2:
                return new LoadScreen();
            case KeyEvent.VK_NUMPAD2:
                return new LoadScreen();
            case KeyEvent.VK_3:
                return new ServerScreen();
            case KeyEvent.VK_NUMPAD3:
                return new ServerScreen();
            case KeyEvent.VK_4:
                return new SelectScreen();
            case KeyEvent.VK_NUMPAD4:
                return new SelectScreen();
            default:
                return this;
        }
    }

}
