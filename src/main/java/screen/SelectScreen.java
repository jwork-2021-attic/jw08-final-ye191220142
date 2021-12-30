package screen;

import java.awt.event.KeyEvent;
import java.io.IOException;

import asciiPanel.AsciiPanel;

public class SelectScreen implements Screen {

    protected Screen screen;

    @Override
    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    @Override
    public void displayOutput(AsciiPanel terminal){
        terminal.write("This is the select screen.", 0, 0);
        terminal.write("Choose the Player to start game.", 0, 1);
        terminal.write("1 : Player1", 0, 3);
        terminal.write("2 : Player2", 0, 5);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) throws IOException, InterruptedException {
        switch (key.getKeyCode()) {
            case KeyEvent.VK_1:
                return new ClientScreen(0);
            case KeyEvent.VK_NUMPAD1:
                return new ClientScreen(0);
            case KeyEvent.VK_2:
                return new ClientScreen(1);
            case KeyEvent.VK_NUMPAD2:
                return new ClientScreen(1);
            default:
                return this;
        }
    }

}