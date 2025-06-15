// File: StickmanCharacter.java
// Phiên bản này tương thích hoàn toàn với file GameScreen bạn đã cung cấp.
package org.KeyyH.stickmanfighter.client.game.models;

import org.KeyyH.stickmanfighter.client.gui.GameScreen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.geom.Point2D;

public class StickmanCharacter {
    public double rootX, rootY;
    private Color characterColor;
    private float lineWidth = 6f;

    // Kích thước
    private final double headRadius = 15;
    private final double torsoLength = 60;
    private final double neckLength = 15;
    private final double shoulderWidthFromBodyCenter = 2; // Bạn đã khôi phục lại vai
    private final double upperArmLength = 40;
    private final double lowerArmLength = 35;
    private final double thighLength = 50;
    private final double calfLength = 45;

    private Point2D.Double hip, neck, headCenter;
    private Point2D.Double shoulderL, elbowL, wristL;
    private Point2D.Double shoulderR, elbowR, wristR;
    private Point2D.Double kneeL, footL;
    private Point2D.Double kneeR, footR;

    // Góc điều khiển
    private double torsoAngle;
    private double neckAngle;
    private double shoulderLAngle;
    private double elbowLAngle;
    private double shoulderRAngle;
    private double elbowRAngle;
    private double hipLAngle;
    private double kneeLAngle;
    private double hipRAngle;
    private double kneeRAngle;

    // Trạng thái nhảy
    private boolean isJumping = false;
    private double jumpInitialSpeed = -12.0;
    private double gravity = 0.5;
    private double currentVerticalSpeed = 0;
    private int groundLevel;

    public StickmanCharacter(double rootX, double rootY, Color characterColor) {
        this.rootX = rootX;
        this.rootY = rootY;
        this.characterColor = characterColor;
        this.groundLevel = (int) rootY + (int)(thighLength + calfLength);

        hip = new Point2D.Double(); neck = new Point2D.Double(); headCenter = new Point2D.Double();
        shoulderL = new Point2D.Double(); elbowL = new Point2D.Double(); wristL = new Point2D.Double();
        shoulderR = new Point2D.Double(); elbowR = new Point2D.Double(); wristR = new Point2D.Double();
        kneeL = new Point2D.Double(); footL = new Point2D.Double();
        kneeR = new Point2D.Double(); footR = new Point2D.Double();

        setToIdlePose();
    }

    public void setToIdlePose() {
        this.torsoAngle = 0;
        this.neckAngle = 0;
        this.shoulderLAngle = Math.toRadians(125);
        this.elbowLAngle = Math.toRadians(-10);
        this.shoulderRAngle = Math.toRadians(55);
        this.elbowRAngle = Math.toRadians(10);
        this.hipLAngle = Math.toRadians(115);
        this.kneeLAngle = Math.toRadians(-5);
        this.hipRAngle = Math.toRadians(65);
        this.kneeRAngle = Math.toRadians(5);

        updatePointsFromAngles(); 
    }

    public void update(GameScreen.InputHandler inputHandler, int screenWidth, int screenHeight) {
    // --- 1. Xử lý Input và cập nhật góc (Animation) ---
        if (inputHandler.isMoveLeft() || inputHandler.isMoveRight()) {
            this.torsoAngle = Math.toRadians(15);
            double time = System.currentTimeMillis() / 100.0;
            double hipSwingAmplitude = Math.toRadians(35);
            double shoulderSwingAmplitude = Math.toRadians(40);
            this.hipLAngle = Math.toRadians(115) + Math.sin(time) * hipSwingAmplitude;
            this.hipRAngle = Math.toRadians(65) - Math.sin(time) * hipSwingAmplitude;
            this.shoulderLAngle = Math.toRadians(125) - Math.sin(time) * shoulderSwingAmplitude;
            this.shoulderRAngle = Math.toRadians(55) + Math.sin(time) * shoulderSwingAmplitude;
        } else {
            this.torsoAngle = 0;
            if (!isJumping) {
                setToIdlePose();
            }
        }

        // --- 2. Cập nhật vật lý (Di chuyển & Trọng lực) ---
        double speedX = 4.0;
        if (inputHandler.isMoveLeft()) rootX -= speedX;
        if (inputHandler.isMoveRight()) rootX += speedX;

        if (inputHandler.isJumpPressed() && !isJumping) {
            isJumping = true;
            currentVerticalSpeed = jumpInitialSpeed;
        }

        rootY += currentVerticalSpeed;
        currentVerticalSpeed += gravity;

        // --- 3. Cập nhật lại toàn bộ "bộ xương" với vị trí và góc mới ---
        updatePointsFromAngles();

        // --- 4. Kiểm tra và xử lý va chạm đất bằng tọa độ bàn chân ---
        // Tìm xem bàn chân nào ở vị trí thấp nhất
        double deepestFootY = Math.max(footL.y, footR.y);

        // Nếu có một bàn chân đi xuyên qua mặt đất
        if (deepestFootY > this.groundLevel) {
            // Tính toán độ lún
            double penetration = deepestFootY - this.groundLevel;

            // Dịch chuyển TOÀN BỘ nhân vật lên trên một khoảng bằng độ lún
            this.rootY -= penetration;

            // Cập nhật lại vị trí các điểm một lần nữa sau khi đã dịch chuyển
            updatePointsFromAngles();

            // Dừng trạng thái nhảy và reset vận tốc rơi
            isJumping = false;
            currentVerticalSpeed = 0;
        }
    }
    
    // --- PHƯƠNG THỨC TIỆN ÍCH ĐÃ ĐƯỢC THÊM VÀO ---
    private Point2D.Double calculateEndPoint(Point2D.Double start, double length, double angle) {
        double endX = start.x + length * Math.cos(angle);
        double endY = start.y + length * Math.sin(angle);
        return new Point2D.Double(endX, endY);
    }
    
    private void updatePointsFromAngles() {
        // Gán vị trí hông từ gốc
        this.hip.setLocation(rootX, rootY);

        // Tính toán các điểm khác dựa trên hông và các góc
        double verticalUp = -Math.PI / 2;
        this.neck.setLocation(calculateEndPoint(this.hip, torsoLength, verticalUp + torsoAngle));
        this.headCenter.setLocation(calculateEndPoint(this.neck, neckLength, verticalUp + torsoAngle + neckAngle));
        this.shoulderL.setLocation(calculateEndPoint(this.neck, shoulderWidthFromBodyCenter, Math.PI + torsoAngle));
        this.shoulderR.setLocation(calculateEndPoint(this.neck, shoulderWidthFromBodyCenter, 0 + torsoAngle));
        this.elbowL.setLocation(calculateEndPoint(this.shoulderL, upperArmLength, shoulderLAngle + torsoAngle));
        this.wristL.setLocation(calculateEndPoint(this.elbowL, lowerArmLength, shoulderLAngle + elbowLAngle + torsoAngle));
        this.elbowR.setLocation(calculateEndPoint(this.shoulderR, upperArmLength, shoulderRAngle + torsoAngle));
        this.wristR.setLocation(calculateEndPoint(this.elbowR, lowerArmLength, shoulderRAngle + elbowRAngle + torsoAngle));
        this.kneeL.setLocation(calculateEndPoint(this.hip, thighLength, hipLAngle));
        this.footL.setLocation(calculateEndPoint(this.kneeL, calfLength, hipLAngle + kneeLAngle));
        this.kneeR.setLocation(calculateEndPoint(this.hip, thighLength, hipRAngle));
        this.footR.setLocation(calculateEndPoint(this.kneeR, calfLength, hipRAngle + kneeRAngle));
    }                      

    public void draw(Graphics2D g2d) {
        g2d.setColor(this.characterColor);
        g2d.setStroke(new BasicStroke(this.lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Giờ đây draw() chỉ có nhiệm vụ nối các điểm đã được tính toán sẵn
        g2d.drawLine((int) hip.x, (int) hip.y, (int) neck.x, (int) neck.y);
        g2d.drawLine((int) neck.x, (int) neck.y, (int) headCenter.x, (int) headCenter.y);
        g2d.drawLine((int) neck.x, (int) neck.y, (int) shoulderL.x, (int) shoulderL.y);
        g2d.drawLine((int) neck.x, (int) neck.y, (int) shoulderR.x, (int) shoulderR.y);
        g2d.drawLine((int) shoulderL.x, (int) shoulderL.y, (int) elbowL.x, (int) elbowL.y);
        g2d.drawLine((int) elbowL.x, (int) elbowL.y, (int) wristL.x, (int) wristL.y);
        g2d.drawLine((int) shoulderR.x, (int) shoulderR.y, (int) elbowR.x, (int) elbowR.y);
        g2d.drawLine((int) elbowR.x, (int) elbowR.y, (int) wristR.x, (int) wristR.y);
        g2d.drawLine((int) hip.x, (int) hip.y, (int) kneeL.x, (int) kneeL.y);
        g2d.drawLine((int) kneeL.x, (int) kneeL.y, (int) footL.x, (int) footL.y);
        g2d.drawLine((int) hip.x, (int) hip.y, (int) kneeR.x, (int) kneeR.y);
        g2d.drawLine((int) kneeR.x, (int) kneeR.y, (int) footR.x, (int) footR.y);
        g2d.fillOval((int) (headCenter.x - headRadius), (int) (headCenter.y - headRadius), (int) (2 * headRadius), (int) (2 * headRadius));
    }
}