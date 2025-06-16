package org.KeyyH.stickmanfighter.client.game.animation;

public class Pose {
    // Sử dụng public final để tạo một đối tượng dữ liệu đơn giản và bất biến (immutable)
    public final double torso, neck;
    public final double shoulderL, elbowL, shoulderR, elbowR;
    public final double hipL, kneeL, hipR, kneeR;
    /**
     * Constructor to create a Pose object with angles in degrees.
     * The angles are converted to radians for internal representation.
     *
     * @param torso      Angle of the torso in degrees
     * @param neck       Angle of the neck in degrees
     * @param shoulderL  Angle of the left shoulder in degrees
     * @param elbowL     Angle of the left elbow in degrees
     * @param shoulderR  Angle of the right shoulder in degrees
     * @param elbowR     Angle of the right elbow in degrees
     * @param hipL       Angle of the left hip in degrees
     * @param kneeL      Angle of the left knee in degrees
     * @param hipR       Angle of the right hip in degrees
     * @param kneeR      Angle of the right knee in degrees
     */
    
    public Pose(double torso, double neck, 
                double shoulderL, double elbowL, double shoulderR, double elbowR, 
                double hipL, double kneeL, double hipR, double kneeR) 
    {
        this.torso = Math.toRadians(torso);
        this.neck = Math.toRadians(neck);
        this.shoulderL = Math.toRadians(shoulderL);
        this.elbowL = Math.toRadians(elbowL);
        this.shoulderR = Math.toRadians(shoulderR);
        this.elbowR = Math.toRadians(elbowR);
        this.hipL = Math.toRadians(hipL);
        this.kneeL = Math.toRadians(kneeL);
        this.hipR = Math.toRadians(hipR);
        this.kneeR = Math.toRadians(kneeR);
    }
}