package systems.scoring;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.controller.PIDController;
import robot.Devices;
import robot.Limelight;
import robot.Limelight.LedEnum;
import utilities.CANSparkMaxSendable;
import utilities.MathUtil;

public class Shooter {

	public final static double lHeight = 25.5; // Limelight Height
	public final static double tHeight = 98.6; // Target Height
	public final static double angleLimelight = 21; // Limelight Angle in Degrees

    private CANSparkMaxSendable leftMotor;
    private CANSparkMaxSendable rightMotor;
    private Servo servo;
    private PIDController shooterSpeedPID;
    private double hoodAngle = 0;
    private double incrementValue = 0;
    private final double DISTANCE_Y = 2.50444-0.6477; // Difference between goal height and shooter height in meters
    private final double SHOOTER_FLYWHEEL_RADIUS = 0.0762; // Radius of the shooter's flywheel in meters


    public Shooter (int lmChannel, int rmChannel, int sChannel) {
        leftMotor = new CANSparkMaxSendable(lmChannel, MotorType.kBrushless);
        rightMotor = new CANSparkMaxSendable(rmChannel, MotorType.kBrushless);
        servo = new Servo(sChannel);

        
        rightMotor.follow(leftMotor, true);
        rightMotor.burnFlash();

        // shooterSpeedPID = new PIDController(1.0, 0.0, 0.0);
    }

    public double getTargetRpm(double angle, double distanceX) {
        double velocity = Math.sqrt((-4.9*Math.pow(distanceX/39.37, 2)*(Math.pow(1/Math.cos(Math.toRadians(Math.abs(angle))), 2)))/
                          (DISTANCE_Y-((distanceX/39.37)*Math.tan(Math.toRadians(Math.abs(angle))))));

                        //   System.out.println(distanceX);

        return velocity/(SHOOTER_FLYWHEEL_RADIUS*0.10472);
    }

    public double getShooterSpeed () {
        return leftMotor.getEncoder().getVelocity();
    }

    public void setShooterSpeed (double speed) {
        leftMotor.set(-speed);
    }

    public void incrementValue (double value) {
        incrementValue = incrementValue + value;
    }

    public double getIncrement () {
        return incrementValue;
    }

    public void setIncrement (double value) {
        incrementValue = value;
    }

    public void setServoAngle (double angle) {
        servo.setPosition(angle);
	}
	
	public Servo getHoodServo() {
		return this.servo;
	}

	public CANSparkMaxSendable getLeftMotor() {
		return this.leftMotor;
	}

	public CANSparkMaxSendable getRightMotor() {
		return this.rightMotor;
	}
}