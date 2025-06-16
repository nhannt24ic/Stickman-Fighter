package org.KeyyH.stickmanfighter.client.gui;

import org.KeyyH.stickmanfighter.client.game.models.StickmanCharacter;

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

    private StickmanCharacter player1;
    // private StickmanCharacter player2;

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
        // Khởi tạo nhân vật
        player1 = new StickmanCharacter(100, screenHeight - 100, Color.BLACK); // y là đáy của nhân vật
    }

    public void startGameLoop() {
        gameLoopTimer = new Timer(DELAY, this);
        gameLoopTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame();
        repaint();
    }

    private void updateGame() {
        if (player1 != null) {
            player1.update(inputHandler, screenWidth, screenHeight);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Vẽ nền (background color)

        Graphics2D g2d = (Graphics2D) g;

        // Bật khử răng cưa để hình vẽ mượt hơn (tùy chọn)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ nhân vật
        if (player1 != null) {
            player1.draw(g2d);
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
    }
}