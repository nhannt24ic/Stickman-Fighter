package org.keyyh.stickmanfighter.client.gui;

import org.keyyh.stickmanfighter.client.game.models.StickmanCharacter;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameScreen extends JPanel implements ActionListener {

    private final int screenWidth;
    private final int screenHeight;
    private final int DELAY = 16; // Khoảng 60 FPS (1000ms / 60 = ~16.6ms)

    private final java.util.List<StickmanCharacter> characters = new java.util.ArrayList<>();
    private final java.util.List<Runnable> characterUpdates = new java.util.ArrayList<>();

    private InputHandler inputHandler;
    private Timer gameLoopTimer;

    public GameScreen(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;

        inputHandler = new InputHandler();

        initPanel();
        initGameObjects();
    }

    private void initPanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.LIGHT_GRAY);
        setFocusable(true); // Quan trọng để JPanel có thể nhận sự kiện bàn phím
        addKeyListener(inputHandler); // Thêm trình xử lý input
    }

    private void initGameObjects() {
        // Khởi tạo nhân vật mẫu
        characters.clear();
        characterUpdates.clear();
        characters.add(new StickmanCharacter(100, screenHeight - 100, Color.BLACK, false));
        characters.add(new StickmanCharacter(1000, screenHeight - 100, Color.RED, true));
        // Có thể thêm nhiều nhân vật hơn ở đây

        for (StickmanCharacter c : characters) {
            if (c.isAI()) {
                characterUpdates.add(() -> c.updateAI(getPlayerTarget(c), screenWidth, screenHeight));
            } else {
                characterUpdates.add(() -> c.updatePlayer(inputHandler, screenWidth, screenHeight));
            }
        }
    }

    // Hàm tự động tìm mục tiêu cho AI (ví dụ: tìm người chơi đầu tiên)
    private StickmanCharacter getPlayerTarget(StickmanCharacter ai) {
        for (StickmanCharacter c : characters) {
            if (!c.isAI()) return c;
        }
        return null;
    }

    public void startGameLoop() {
        gameLoopTimer = new Timer(DELAY, this);
        gameLoopTimer.start();
        // InputHandler.printPanelFocus(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame();
        repaint();
    }

    private void updateGame() {
        // inputHandler.printInputState();
        for (Runnable r : characterUpdates) r.run();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Vẽ nền (background color)

        Graphics2D g2d = (Graphics2D) g;

        // Bật khử răng cưa để hình vẽ mượt hơn (tùy chọn)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (StickmanCharacter c : characters) {
            c.draw(g2d);
        }

    }

    public static class InputHandler extends KeyAdapter {
        private boolean moveLeft = false;
        private boolean moveRight = false;
        private boolean jump = false;
        // Thêm các trạng thái phím khác nếu cần

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
                moveLeft = true;
            }
            if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
                moveRight = true;
            }
            if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP || key == KeyEvent.VK_SPACE) {
                jump = true;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
                moveLeft = false;
            }
            if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
                moveRight = false;
            }
            if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP || key == KeyEvent.VK_SPACE) {
                jump = false;
            }
        }

        public boolean isMoveLeft() {
            return moveLeft;
        }

        public boolean isMoveRight() {
            return moveRight;
        }

        public boolean isJumpPressed() { // Có thể đổi tên để rõ ràng hơn là một lần nhấn
            boolean currentJumpState = jump;
            return currentJumpState;
        }

        // DEBUG: Kiểm tra trạng thái input
        public void printInputState() {
            System.out.println("[InputHandler] moveLeft=" + moveLeft + ", moveRight=" + moveRight + ", jump=" + jump);
        }

        // DEBUG: Kiểm tra trạng thái focus của panel
        public static void printPanelFocus(GameScreen panel) {
            System.out.println("[GameScreen] isFocusable=" + panel.isFocusable() + ", hasFocus=" + panel.hasFocus());
        }
    }
}