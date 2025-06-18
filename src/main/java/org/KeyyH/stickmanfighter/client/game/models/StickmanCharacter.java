package org.KeyyH.stickmanfighter.client.game.models;

import org.KeyyH.stickmanfighter.client.game.animation.Pose;
import org.KeyyH.stickmanfighter.client.gui.GameScreen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class StickmanCharacter {
    public double rootX, rootY;
    private Color characterColor;
    private float lineWidth = 6f;
    private double speedX = 10.0; // Tốc độ di chuyển ngang
    private boolean isJumping = false;
    private double jumpInitialSpeed = -15.0;
    private double gravity = 1.0;
    private double currentVerticalSpeed = 0;
    private int groundLevel;

    // Kích thước
    private final double headRadius = 17;
    private final double torsoLength = 60;
    private final double neckLength = 17;
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

    private List<Pose> runRightKeyframes;
    private List<Pose> runLeftKeyframes;
    private List<Pose> jumpKeyframes;

    private List<Pose> currentAnimation;
    private long currentTime;
    private long lastFrameTime = 0;
    private int currentRunFrame = -1;
    private final int TIME_PER_RUN_FRAME = 25;
    private boolean isFacingRight;

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

        this.isFacingRight = true;

        initializeRunRightKeyframes();
        initializeRunLeftKeyframes();
        initializeJumpKeyframes();
        setToIdlePose();
    }

    private void initializeRunLeftKeyframes(){
        runLeftKeyframes = new ArrayList<>();
  
        for (Pose rightPose : runRightKeyframes) {
            double torso = -Math.toDegrees(rightPose.torso);
            double neck = -Math.toDegrees(rightPose.neck);

            // Lật gương các chi chính (vai, hông)
            double shoulderL = 180 - Math.toDegrees(rightPose.shoulderR);
            double shoulderR = 180 - Math.toDegrees(rightPose.shoulderL);
            double hipL = 180 - Math.toDegrees(rightPose.hipR);
            double hipR = 180 - Math.toDegrees(rightPose.hipL);
            
            // Tráo đổi các góc tương đối (khuỷu tay, đầu gối)
            double elbowL = - Math.toDegrees(rightPose.elbowR);
            double elbowR = - Math.toDegrees(rightPose.elbowL);
            double kneeL = - Math.toDegrees(rightPose.kneeR);
            double kneeR = - Math.toDegrees(rightPose.kneeL);
            
            runLeftKeyframes.add(new Pose(torso, neck, shoulderL, elbowL, shoulderR, elbowR, hipL, kneeL, hipR, kneeR));
        }
    }

    private void initializeRunRightKeyframes() {
        runRightKeyframes = new ArrayList<>();
        
        runRightKeyframes.add(new Pose(50, 0,  175 - 50 , -20, 40 - 40-10 , -110, 135, 10, 20, 80));
        runRightKeyframes.add(new Pose(50, 0,  125 -10, -40, 30-10, -110, 125, 20, 35, 90));
        runRightKeyframes.add(new Pose(50, 0,  115-10, -90, 70-10, -110,   100, 40,   50, 70));
        runRightKeyframes.add(new Pose(50, 0,  100-10, -110, 100-10, -110,   70, 50,   70, 50));
        runRightKeyframes.add(new Pose(50, 0,  70-10, -110, 115-10, -90,   50, 70,   100, 40));
        runRightKeyframes.add(new Pose(50, 0,  30-10, -110, 125-10, -40,   35, 90,   125, 20));
        runRightKeyframes.add(new Pose(50, 0,  0-10, -110, 135-10, -20, 20, 80, 135, 10));

    }

    private void initializeJumpKeyframes() {
        jumpKeyframes = new ArrayList<>();
        
        jumpKeyframes.add(new Pose(0, 0, 190, -80, -10, 80, 160, -90, 20, 90));
        jumpKeyframes.add(new Pose(0, 0, 200, -30, -20, 30, 210, -140, -30, 140));
        jumpKeyframes.add(new Pose(0, 0, 200, -50, -20, 50, 200, -120, -20, 120));
        jumpKeyframes.add(new Pose(0, 0, 170, -40, 10, 40, 160, -90, 20, 90));
        jumpKeyframes.add(new Pose(0, 0, 150, -50, 30, 50, 160, -60, 20, 60));
        jumpKeyframes.add(new Pose(0, 0, 135, -30, 45, 30, 130, -30, 50, 30));
        jumpKeyframes.add(new Pose(0, 0, 125, -10, 55, 10, 115, -5, 65, 5));

    }

    private void applyPose(Pose pose) {
        this.torsoAngle = pose.torso;
        this.neckAngle = pose.neck;
        this.shoulderLAngle = pose.shoulderL;
        this.elbowLAngle = pose.elbowL;
        this.shoulderRAngle = pose.shoulderR;
        this.elbowRAngle = pose.elbowR;
        this.hipLAngle = pose.hipL;
        this.kneeLAngle = pose.kneeL;
        this.hipRAngle = pose.hipR;
        this.kneeRAngle = pose.kneeR;
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
        // Ưu tiên động tác: NHẢY > các động tác khác > CHẠY
        currentTime = System.currentTimeMillis();
        boolean wantJump = inputHandler.isJumpPressed();
        boolean wantMove = inputHandler.isMoveLeft() || inputHandler.isMoveRight();
        boolean allowMove = true;

        // Kiểm tra đang ở trên mặt đất (grounded)
        boolean isGrounded = (Math.max(footL.y, footR.y) >= groundLevel - 25);

        // Nếu đang nhảy trước (isJumping == true trước khi xử lý input), không cho di chuyển ngang
        if (isJumping || !isGrounded) {
            allowMove = false;
        }

        // Xử lý nhảy (ưu tiên cao nhất, chỉ cho nhảy khi đang trên mặt đất)
        if (wantJump && !isJumping && isGrounded) {
            isJumping = true;
            currentVerticalSpeed = jumpInitialSpeed;
            currentRunFrame = 0; // Bắt đầu lại animation nhảy
            lastFrameTime = currentTime;
        }

        // Nếu được phép di chuyển ngang (chỉ khi không nhảy trước)
        if (allowMove) {
            if (inputHandler.isMoveLeft()) {
                rootX -= speedX;
                isFacingRight = false;
            }
            if (inputHandler.isMoveRight()) {
                rootX += speedX;
                isFacingRight = true;
            }
        }

        // Xử lý trọng lực
        rootY += currentVerticalSpeed;
        currentVerticalSpeed += gravity;

        // --- Animation: Ưu tiên động tác ---
        if (isJumping) {
            // Animation nhảy chỉ chạy 1 vòng, hết thì về idle
            if (currentTime - lastFrameTime >= TIME_PER_RUN_FRAME + 20) {
                lastFrameTime = currentTime;
                currentAnimation = jumpKeyframes;
                if (currentRunFrame < currentAnimation.size()) {
                    applyPose(currentAnimation.get(currentRunFrame));
                    currentRunFrame++;
                } else {
                    // Đã hết animation nhảy, về idle và chờ nhấn tiếp
                    isJumping = false;
                    setToIdlePose();
                    currentRunFrame = 0;
                }
            }
        } else if (wantMove &&  allowMove) {
            // Animation chạy (ưu tiên thấp hơn nhảy, lặp liên tục)
            if (currentTime - lastFrameTime > TIME_PER_RUN_FRAME) {
                lastFrameTime = currentTime;
                currentAnimation = isFacingRight ? runRightKeyframes : runLeftKeyframes;
                currentRunFrame = (currentRunFrame + 1) % currentAnimation.size();
                applyPose(currentAnimation.get(currentRunFrame));
            }
        } else {
            // Animation idle
            currentRunFrame = 0;
            setToIdlePose();
        }

        updatePointsFromAngles();
        handleGroundCollision();
    }
    
    // --- PHƯƠNG THỨC TIỆN ÍCH ĐÃ ĐƯỢC THÊM VÀO ---
    private void handleGroundCollision() {
        // Tìm xem bàn chân nào ở vị trí thấp nhất
        double deepestFootY = Math.max(footL.y, footR.y);

        // Nếu có một bàn chân đi xuyên qua mặt đất
        if (deepestFootY > groundLevel) {
            // Tính toán độ lún
            double penetration = deepestFootY - groundLevel;

            // Dịch chuyển TOÀN BỘ nhân vật lên trên một khoảng bằng độ lún
            this.rootY -= penetration;

            // Cập nhật lại vị trí các điểm một lần nữa sau khi đã dịch chuyển
            updatePointsFromAngles();

            // Dừng trạng thái nhảy và reset vận tốc rơi
            isJumping = false;
            if (currentVerticalSpeed > 0){
                currentVerticalSpeed = 0;
            }
        }
    }

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

        g2d.setColor(Color.BLUE);
        g2d.drawLine((int) neck.x, (int) neck.y, (int) shoulderL.x, (int) shoulderL.y);
        g2d.drawLine((int) shoulderL.x, (int) shoulderL.y, (int) elbowL.x, (int) elbowL.y);
        g2d.drawLine((int) elbowL.x, (int) elbowL.y, (int) wristL.x, (int) wristL.y);
        g2d.drawLine((int) hip.x, (int) hip.y, (int) kneeL.x, (int) kneeL.y);
        g2d.drawLine((int) kneeL.x, (int) kneeL.y, (int) footL.x, (int) footL.y);

        g2d.setColor(this.characterColor);
        // Giờ đây draw() chỉ có nhiệm vụ nối các điểm đã được tính toán sẵn
        g2d.drawLine((int) hip.x, (int) hip.y, (int) neck.x, (int) neck.y);
        g2d.drawLine((int) neck.x, (int) neck.y, (int) headCenter.x, (int) headCenter.y);
        g2d.fillOval((int) (headCenter.x - headRadius), (int) (headCenter.y - headRadius), (int) (2 * headRadius), (int) (2 * headRadius));

        g2d.setColor(Color.RED);
        g2d.drawLine((int) neck.x, (int) neck.y, (int) shoulderR.x, (int) shoulderR.y);
        g2d.drawLine((int) shoulderR.x, (int) shoulderR.y, (int) elbowR.x, (int) elbowR.y);
        g2d.drawLine((int) elbowR.x, (int) elbowR.y, (int) wristR.x, (int) wristR.y);
        g2d.drawLine((int) hip.x, (int) hip.y, (int) kneeR.x, (int) kneeR.y);
        g2d.drawLine((int) kneeR.x, (int) kneeR.y, (int) footR.x, (int) footR.y);
    }
}